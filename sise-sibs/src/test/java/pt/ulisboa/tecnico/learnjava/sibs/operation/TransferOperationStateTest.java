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
	int VALUE = 100;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		services = new Services();
		sibs = new Sibs(100, services);
		sourceBank = new Bank("CGD");
		targetBank = new Bank("BPI");
		sourceClient = new Client(sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		targetClient = new Client(targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
		SOURCE_IBAN = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		TARGET_IBAN = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
	}

	@Test
	public void successRegistered() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		operation.Process(services);
		assertEquals("registered", operation.getState());
		assertEquals(100, operation.getValue());
		assertEquals(SOURCE_IBAN, operation.getSourceIban());
		assertEquals(TARGET_IBAN, operation.getTargetIban());
	}

	@Test
	public void successWithdrawn() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());
		assertEquals(100, operation.getValue());
		assertEquals(SOURCE_IBAN, operation.getSourceIban());
		assertEquals(TARGET_IBAN, operation.getTargetIban());
	}

	@Test
	public void successCompleted() throws OperationException, AccountException, BankException, ClientException, SibsException {
		String NIF2 = "123456799";
		String LAST = "Costa";
		String FIRST = "Manuel";
		Client targetClient2 = new Client(sourceBank, FIRST, LAST, NIF2, PHONE_NUMBER, ADDRESS, 22);
		String targetIban = sourceBank.createAccount(Bank.AccountType.CHECKING, targetClient2, 1000, 0);
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, targetIban, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(services);
		operation.Process(services);
		assertEquals("completed", operation.getState());
	}

	@Test
	public void successDeposited() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(services);
		assertEquals("withdrawn", operation.getState());

	}

	@Test
	public void successDepositedCompleted() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(services);
		assertEquals("withdrawn", operation.getState());

		operation.Process(services);
		assertEquals("completed", operation.getState());
	}

	@Test
	public void successDepositedCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(services);
		assertEquals("withdrawn", operation.getState());

		operation.cancel();
		assertEquals("cancelled", operation.getState());
	}

	@Test
	public void successWithdrawnCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.cancel();
		assertEquals("cancelled", operation.getState());
	}

	@Test
	public void successRegisteredCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.cancel();
		assertEquals("cancelled", operation.getState());
	}

	@Test
	public void successDepositedCompletedNoCancel() throws OperationException, AccountException, SibsException {
		TransferOperation operation = new TransferOperation(SOURCE_IBAN, TARGET_IBAN, VALUE);
		operation.Process(services);
		assertEquals("registered", operation.getState());

		operation.Process(services);
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("deposited", operation.getState());

		operation.Process(services);
		assertEquals("withdrawn", operation.getState());

		operation.Process(services);
		assertEquals("completed", operation.getState());

		operation.cancel();
		assertEquals("completed", operation.getState());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
