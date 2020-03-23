package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public abstract class Operation {
	public static final String OPERATION_TRANSFER = "transfer";
	public static final String OPERATION_PAYMENT = "payment";

	private final String type;
	private final int value;

	public Operation(String type, int value) throws OperationException {
		checkParameters(type, value);
		this.type = type;
		this.value = value;
	}

	private void checkParameters(String type, int value) throws OperationException {
		if (type == null || !type.equals(OPERATION_TRANSFER) && !type.equals(OPERATION_PAYMENT)) {
			throw new OperationException(type);
		}

		if (value <= 0) {
			throw new OperationException(value);
		}
	}

	public int commission() {
		return 1;
	}

	public String getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	public String getState() {
		return null;
	}

	public String cancel() {
		return null;
	}

	public void setState(String state) {

	}

	public String Process(Services services) throws OperationException, AccountException, SibsException {
		return null;
	}
}
