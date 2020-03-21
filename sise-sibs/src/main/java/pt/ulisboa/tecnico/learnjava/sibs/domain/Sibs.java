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
		String state;
		int j = 0;
		try {
			state = operation.Process(services);
			if(!services.checkAccount(sourceIban) || !services.checkAccount(targetIban)) {
				throw new SibsException();
			}
			state = operation.Process(services);
			state = operation.Process(services);
			state = operation.Process(services);
			addOperation(Operation.OPERATION_TRANSFER, sourceIban, targetIban, amount, state);
			return operation;
		} catch (AccountException e) {
			state = operation.retry();
			addOperation(Operation.OPERATION_TRANSFER, sourceIban, targetIban, amount, state);
			throw new SibsException();
		}
	}

	public int addOperation(String type, String sourceIban, String targetIban, int value, String state)
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
			operation.setState(state);
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

	public void changeOperation(int position, String state) throws SibsException {
		if (position < 0 || position > operations.length) {
			throw new SibsException();
		}
		getOperation(position).setState(state);
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

	public int processOperation() throws AccountException, SibsException {
		int result = 0;
		String state;
		for (int i = 0; i < operations.length; i++) {
			if (operations[i] != null) {
				if(operations[i].getState()=="registered") {
					operations[i].Process(services);
					operations[i].Process(services);
					state = operations[i].Process(services);
					changeOperation(i, state);
					result++;
				}
				if(operations[i].getState()=="deposited") {
					operations[i].Process(services);
					state = operations[i].Process(services);
					changeOperation(i, state);
					result++;
				}
				if(operations[i].getState()=="withdrawn") {
					state = operations[i].Process(services);
					changeOperation(i, state);
					result++;
				}
			}
		}
		return result;
	}

	public void cancelOperation(int position) throws AccountException, SibsException {
		changeOperation(position, getOperation(position).cancel());
	}
}
