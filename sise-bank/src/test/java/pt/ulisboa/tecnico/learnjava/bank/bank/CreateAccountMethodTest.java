package pt.ulisboa.tecnico.learnjava.bank.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.SalaryAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.SavingsAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class CreateAccountMethodTest {
	private Bank bank;
	private Client client;
	private Services services;
	String[] personalInfo = new String[] {"José", "Manuel", "Street"};

	@Before
	public void setUp() throws BankException, ClientException {
		services = new Services();
		bank = new Bank("CGD");
		client = new Client(bank, personalInfo, "123456789", "987654321", 33);
	}

	@Test
	public void successCheckingAccount() throws BankException, AccountException, ClientException {
		String iban = bank.createAccount(AccountType.CHECKING, client, 100, 0);

		Account account = services.getAccountByIban(iban);

		assertTrue(account instanceof CheckingAccount);
		assertEquals(1, bank.getTotalNumberOfAccounts());
		assertEquals(client, account.getClient());
		assertEquals(100, account.getBalance());
	}

	@Test
	public void successSavingsAccount() throws BankException, AccountException, ClientException {
		String iban = bank.createAccount(AccountType.SAVINGS, client, 100, 100);

		Account account = services.getAccountByIban(iban);

		assertTrue(account instanceof SavingsAccount);
		assertEquals(1, bank.getTotalNumberOfAccounts());
		assertEquals(client, account.getClient());
		assertEquals(100, account.getBalance());
		assertEquals(100, ((SavingsAccount) account).getBase());
	}

	@Test
	public void successYoungAccount() throws BankException, AccountException, ClientException {
		Client youngClient = new Client(bank, personalInfo, "123456780", "987654321", 16);
		String iban = bank.createAccount(AccountType.YOUNG, youngClient, 100, 100);

		Account account = services.getAccountByIban(iban);

		assertTrue(account instanceof SavingsAccount);
		assertEquals(1, bank.getTotalNumberOfAccounts());
		assertEquals(youngClient, account.getClient());
		assertEquals(100, account.getBalance());
		assertEquals(10, ((SavingsAccount) account).getBase());
	}

	@Test
	public void failYoungAccountMoreThan17() throws BankException, AccountException, ClientException {
		Client notSoYoungClient = new Client(bank, personalInfo, "123456780", "987654321", 18);

		try {
			bank.createAccount(AccountType.YOUNG, notSoYoungClient, 100, 100);
			fail();
		} catch (AccountException e) {
			assertEquals(0, bank.getTotalNumberOfAccounts());
		}
	}

	@Test
	public void failYoungAccountBaseNot10() throws BankException, AccountException, ClientException {
		Client notSoYoungClient = new Client(bank, personalInfo, "123456780", "987654321", 18);

		try {
			bank.createAccount(AccountType.YOUNG, notSoYoungClient, 100, 100);
			fail();
		} catch (AccountException e) {
			assertEquals(0, bank.getTotalNumberOfAccounts());
		}
	}

	@Test
	public void successSalaryAccount() throws BankException, AccountException, ClientException {
		String iban = bank.createAccount(AccountType.SALARY, client, 100, 100);

		Account account = services.getAccountByIban(iban);

		assertTrue(account instanceof SalaryAccount);
		assertEquals(1, bank.getTotalNumberOfAccounts());
		assertEquals(client, account.getClient());
		assertEquals(100, account.getBalance());
		assertEquals(100, ((SalaryAccount) account).getSalary());
	}

	@Test(expected = BankException.class)
	public void clientHasAccountOfAnotherBank() throws AccountException, ClientException, BankException {
		Bank otherBank = new Bank("BPI");
		otherBank.createAccount(AccountType.CHECKING, client, 100, 0);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
