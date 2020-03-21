package pt.ulisboa.tecnico.learnjava.sibs.sibs;

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
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodWithStates {
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

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		services = new Services();
		sibs = new Sibs(100, services);
		sourceBank = new Bank("CGD");
		targetBank = new Bank("BPI");
		sourceClient = new Client(sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		targetClient = new Client(targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
	}

	@Test
	public void successCompleted() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);

		Operation op = sibs.transfer(sourceIban, targetIban, 100);

		assertEquals(889, services.getAccountByIban(sourceIban).getBalance());
		assertEquals(1100, services.getAccountByIban(targetIban).getBalance());
		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
		assertEquals("completed", sibs.getOperation(0).getState());
	}

	@Test
	public void successRegistered() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "registered");
		assertEquals("registered", sibs.getOperation(0).getState());
	}

	@Test
	public void successNotFinished() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "deposited");
		assertEquals("deposited", sibs.getOperation(0).getState());
	}

	@Test
	public void successProcesss() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "deposited");
		sibs.transfer(sourceIban, targetIban, 200);
		sibs.transfer(sourceIban, targetIban, 400);
		assertEquals("deposited", sibs.getOperation(0).getState());
		assertEquals("completed", sibs.getOperation(1).getState());
		assertEquals("completed", sibs.getOperation(2).getState());
		int totalNotFinished = sibs.processOperation();
		assertEquals(3, sibs.getNumberOfOperations());
		assertEquals(1, totalNotFinished);
		assertEquals("completed", sibs.getOperation(0).getState());
	}

	@Test
	public void successProcessWithdraw() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "withdrawn");
		sibs.transfer(sourceIban, targetIban, 200);
		sibs.transfer(sourceIban, targetIban, 400);
		assertEquals("withdrawn", sibs.getOperation(0).getState());
		assertEquals("completed", sibs.getOperation(1).getState());
		assertEquals("completed", sibs.getOperation(2).getState());
		int totalNotFinished = sibs.processOperation();
		assertEquals(3, sibs.getNumberOfOperations());
		assertEquals(1, totalNotFinished);
		assertEquals("completed", sibs.getOperation(0).getState());
	}

	@Test
	public void successProcessRegistered() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "registered");
		sibs.transfer(sourceIban, targetIban, 200);
		sibs.transfer(sourceIban, targetIban, 400);
		assertEquals("registered", sibs.getOperation(0).getState());
		assertEquals("completed", sibs.getOperation(1).getState());
		assertEquals("completed", sibs.getOperation(2).getState());
		int totalNotFinished = sibs.processOperation();
		assertEquals(3, sibs.getNumberOfOperations());
		assertEquals(1, totalNotFinished);
		assertEquals("completed", sibs.getOperation(0).getState());
	}

	@Test
	public void successProcessCancelled() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "registered");
		sibs.cancelOperation(0);
		assertEquals("cancelled", sibs.getOperation(0).getState());
	}

	@Test
	public void successProcessCancelledDeposited() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "deposited");
		sibs.cancelOperation(0);
		assertEquals("cancelled", sibs.getOperation(0).getState());
	}

	@Test
	public void successProcessCancelledWithdrawn() throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Operation op = sibs.transfer(sourceIban, targetIban, 100);
		sibs.changeOperation(0, "withdrawn");
		sibs.cancelOperation(0);
		assertEquals("cancelled", sibs.getOperation(0).getState());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
