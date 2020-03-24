package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Sibs {
	final Operation[] operations;
	Services services;

	public Sibs(int maxNumberOfOperations, Services services) {
		operations = new Operation[maxNumberOfOperations];
		this.services = services;
	}

	public Operation transfer(String sourceIban, String targetIban, int amount)
			throws SibsException, AccountException, OperationException {
		Operation operation = new TransferOperation(sourceIban, targetIban, amount);
		if(!services.checkAccount(sourceIban) || !services.checkAccount(targetIban)) {
			throw new SibsException();
		}
		addOperation(Operation.OPERATION_TRANSFER, sourceIban, targetIban, amount);
		return operation;
	}

	public int addOperation(String type, String sourceIban, String targetIban, int value)
			throws OperationException, SibsException {
		int position = -1;
		for (int i = 0; i < operations.length; i++) {
			if (operations[i] == null) {
				position = i;
				break;
			}
		}
		if (position == -1) {
			throw new SibsException();
		}

		Operation operation;
		if (type.equals(Operation.OPERATION_TRANSFER)) {
			operation = new TransferOperation(sourceIban, targetIban, value);
		} else {
			operation = new PaymentOperation(targetIban, value);
		}

		operations[position] = operation;
		return position;
	}

	public void removeOperation(int position) throws SibsException {
		if (position < 0 || position > operations.length) {
			throw new SibsException();
		}
		operations[position] = null;
	}

	public Operation getOperation(int position) throws SibsException {
		if (position < 0 || position > operations.length) {
			throw new SibsException();
		}
		return operations[position];
	}

	public int getNumberOfOperations() {
		int result = 0;
		for (int i = 0; i < operations.length; i++) {
			if (operations[i] != null) {
				result++;
			}
		}
		return result;
	}

	public int getTotalValueOfOperations() {
		int result = 0;
		for (int i = 0; i < operations.length; i++) {
			if (operations[i] != null) {
				result = result + operations[i].getValue();
			}
		}
		return result;
	}

	public int getTotalValueOfOperationsForType(String type) {
		int result = 0;
		for (int i = 0; i < operations.length; i++) {
			if (operations[i] != null && operations[i].getType().equals(type)) {
				result = result + operations[i].getValue();
			}
		}
		return result;
	}

	public void processOperation() throws AccountException, SibsException, OperationException {
		String state;
		for (int i = 0; i < operations.length; i++) {
			Operation operation = operations[i];
			if(operation instanceof TransferOperation) {
				TransferOperation op = (TransferOperation) operations[i];
				if (op != null) {
					try {
						op.Process(services);
					}catch(OperationException e) {
						throw new SibsException();
					}
				}
			}
		}
	}

	public void cancelOperation(int position) throws AccountException, SibsException {
		Operation operation = getOperation(position);
		if(operation instanceof TransferOperation) {
			TransferOperation op = (TransferOperation) operation;
			op.cancel();
		}
	}
}
