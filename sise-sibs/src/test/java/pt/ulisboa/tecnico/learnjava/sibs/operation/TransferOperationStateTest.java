package pt.ulisboa.tecnico.learnjava.sibs.operation;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferOperationStateTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";

	private Sibs sibs;
	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;
	private Services services;
	String SOURCE_IBAN;
	String TARGET_IBAN;
	String[] personalInfo = { FIRST_NAME, LAST_NAME, PHONE_NUMBER };
	int VALUE = 100;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.sibs = new Sibs(100, this.services);
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourceClient = new Client(this.sourceBank, this.personalInfo, NIF, PHONE_NUMBER, 33);
		this.targetClient = new Client(this.targetBank, this.personalInfo, NIF, PHONE_NUMBER, 22);
		this.SOURCE_IBAN = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		this.TARGET_IBAN = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
	}

	@Test
	public void successRegistered() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("registered", operation.getState());
		assertEquals(100, operation.getValue());
		assertEquals(this.SOURCE_IBAN, operation.getSourceIban());
		assertEquals(this.TARGET_IBAN, operation.getTargetIban());
	}

	@Test
	public void successWithdrawn() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());
		assertEquals(100, operation.getValue());
		assertEquals(this.SOURCE_IBAN, operation.getSourceIban());
		assertEquals(this.TARGET_IBAN, operation.getTargetIban());
	}

	@Test
	public void successCompleted()
			throws OperationException, AccountException, BankException, ClientException, SibsException {
		String NIF2 = "123456799";
		String[] personalInfo = { "Manuel", "Costa", ADDRESS };
		Client targetClient2 = new Client(this.sourceBank, personalInfo, NIF2, PHONE_NUMBER, 22);
		String targetIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, targetClient2, 1000, 0);
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, targetIban, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(this.services);
		operation.Process(this.services);
		assertEquals("completed", operation.getState());
	}

	@Test
	public void successDeposited() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(this.services);
		assertEquals("withdrawn", operation.getState());

	}

	@Test
	public void successDepositedCompleted() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(this.services);
		assertEquals("withdrawn", operation.getState());

		operation.Process(this.services);
		assertEquals("completed", operation.getState());
	}

	@Test
	public void successDepositedCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(this.services);
		assertEquals("withdrawn", operation.getState());

		operation.cancel();
		assertEquals("cancelled", operation.getState());
	}

	@Test
	public void successWithdrawnCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.cancel();
		assertEquals("cancelled", operation.getState());
	}

	@Test
	public void successRegisteredCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.cancel();
		assertEquals("cancelled", operation.getState());
	}

	@Test
	public void successDepositedCompletedNoCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(this.SOURCE_IBAN, this.TARGET_IBAN, this.VALUE);
		assertEquals("registered", operation.getState());

		operation.Process(this.services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(this.services);
		assertEquals("withdrawn", operation.getState());

		operation.Process(this.services);
		assertEquals("completed", operation.getState());

		operation.cancel();
		assertEquals("completed", operation.getState());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
