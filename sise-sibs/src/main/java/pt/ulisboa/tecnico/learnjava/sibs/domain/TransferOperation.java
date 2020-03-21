package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferOperation extends Operation {
	private final String sourceIban;
	private final String targetIban;
	String stateTransfer;

	public TransferOperation(String sourceIban, String targetIban, int value) throws OperationException {
		super(Operation.OPERATION_TRANSFER, value);

		if (invalidString(sourceIban) || invalidString(targetIban)) {
			throw new OperationException();
		}
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		stateTransfer="";
	}

	@Override
	public String getState() {
		return stateTransfer;
	}

	@Override
	public void setState(String state) {
		if(!stateTransfer.equals("error")) {
			stateTransfer = state;
		}
	}

	@Override
	public String Process(Services services) throws AccountException, SibsException {
		if(!stateTransfer.equals("error")) {
			setState state = new setState(stateTransfer);
			stateTransfer = state.pull(this, services);
			return stateTransfer;
		} else {
			return stateTransfer;
		}
	}

	@Override
	public String cancel() {
		if(!stateTransfer.equals("completed") && !stateTransfer.equals("error")) {
			stateTransfer = "cancelled";
		}
		return stateTransfer;
	}

	@Override
	public String retry() throws AccountException, SibsException {
		setState state = new setState(stateTransfer);
		stateTransfer = state.retry();
		return stateTransfer;
	}

	private boolean invalidString(String name) {
		return name == null || name.length() == 0;
	}

	@Override
	public int commission() {
		return (int) Math.round(super.commission() + getValue() * 0.1);
	}

	public String getSourceIban() {
		return sourceIban;
	}

	public String getTargetIban() {
		return targetIban;
	}
}
