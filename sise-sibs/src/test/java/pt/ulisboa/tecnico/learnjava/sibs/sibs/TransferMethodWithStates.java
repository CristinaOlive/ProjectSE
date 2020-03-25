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
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
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
	private final String[] personalInfo = { FIRST_NAME, LAST_NAME, PHONE_NUMBER };

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.sibs = new Sibs(100, this.services);
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourceClient = new Client(this.sourceBank, this.personalInfo, NIF, PHONE_NUMBER, 33);
		this.targetClient = new Client(this.targetBank, this.personalInfo, NIF, PHONE_NUMBER, 22);
	}

	@Test
	public void successCompleted()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);

		this.sibs.transfer(sourceIban, targetIban, 100);
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		this.sibs.processOperation();
		this.sibs.processOperation();
		this.sibs.processOperation();
		assertEquals(889, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(targetIban).getBalance());
		assertEquals(1, this.sibs.getNumberOfOperations());
		assertEquals(100, this.sibs.getTotalValueOfOperations());
		assertEquals(100, this.sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, this.sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
		assertEquals("completed", op.getState());
	}

	@Test
	public void successRegistered()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("registered", op.getState());
	}

	@Test
	public void successDeposited()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(sourceIban, targetIban, 200);
		this.sibs.transfer(sourceIban, targetIban, 400);
		this.sibs.processOperation();
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("deposited", op.getState());
		TransferOperation op2 = (TransferOperation) this.sibs.getOperation(1);
		assertEquals("deposited", op2.getState());
		TransferOperation op3 = (TransferOperation) this.sibs.getOperation(2);
		assertEquals("deposited", op3.getState());
		assertEquals(3, this.sibs.getNumberOfOperations());
	}

	@Test
	public void successMultipleCompleted()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(sourceIban, targetIban, 200);
		this.sibs.transfer(sourceIban, targetIban, 400);
		this.sibs.processOperation();
		this.sibs.processOperation();
		this.sibs.processOperation();
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("completed", op.getState());
		TransferOperation op2 = (TransferOperation) this.sibs.getOperation(1);
		assertEquals("completed", op2.getState());
		TransferOperation op3 = (TransferOperation) this.sibs.getOperation(2);
		assertEquals("completed", op3.getState());
		assertEquals(3, this.sibs.getNumberOfOperations());
	}

	@Test
	public void successProcessWithdraw()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(sourceIban, targetIban, 200);
		this.sibs.transfer(sourceIban, targetIban, 400);
		this.sibs.processOperation();
		this.sibs.processOperation();
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("withdrawn", op.getState());
		TransferOperation op2 = (TransferOperation) this.sibs.getOperation(1);
		assertEquals("withdrawn", op2.getState());
		TransferOperation op3 = (TransferOperation) this.sibs.getOperation(2);
		assertEquals("withdrawn", op3.getState());
		assertEquals(3, this.sibs.getNumberOfOperations());
	}

	@Test
	public void successProcessRegistered()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(sourceIban, targetIban, 200);
		this.sibs.transfer(sourceIban, targetIban, 400);
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("registered", op.getState());
		TransferOperation op2 = (TransferOperation) this.sibs.getOperation(1);
		assertEquals("registered", op2.getState());
		TransferOperation op3 = (TransferOperation) this.sibs.getOperation(2);
		assertEquals("registered", op3.getState());
		assertEquals(3, this.sibs.getNumberOfOperations());
	}

	@Test
	public void successProcessCancelled()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.cancelOperation(0);
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("cancelled", op.getState());
	}

	@Test
	public void successProcessCancelledDeposited()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.processOperation();
		this.sibs.cancelOperation(0);
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("cancelled", op.getState());
	}

	@Test
	public void successProcessCancelledWithdrawn()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.processOperation();
		this.sibs.processOperation();
		this.sibs.cancelOperation(0);
		TransferOperation op = (TransferOperation) this.sibs.getOperation(0);
		assertEquals("cancelled", op.getState());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
