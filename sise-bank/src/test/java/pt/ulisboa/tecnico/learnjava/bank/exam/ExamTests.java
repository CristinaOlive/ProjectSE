package pt.ulisboa.tecnico.learnjava.bank.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.SalaryAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.SavingsAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.YoungAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class ExamTests {
	private Bank bank;
	private Client client;
	private Client youngClient;
	private CheckingAccount checking;
	private CheckingAccount otherChecking;
	private SavingsAccount savings;
	private YoungAccount young;
	private SalaryAccount salary;
	private Services services;

	@Before
	public void setUp() throws AccountException, ClientException, BankException {
		services = new Services();
		bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		client = new Client(bank, personalInfo, "123456789", "987654321", 33);
		Client otherClient = new Client(bank, personalInfo, "023456789", "987654321", 33);
		youngClient = new Client(bank, personalInfo, "123456780", "987654321", 17);

		checking = (CheckingAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.CHECKING, client, 0, 0));

		otherChecking = (CheckingAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.CHECKING, otherClient, 100, 0));

		savings = (SavingsAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.SAVINGS, client, 100, 10));

		young = (YoungAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.YOUNG, youngClient, 0, 0));

		salary = (SalaryAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.SALARY, client, 0, 1000));
	}

	// OK 1.1 a
	@Test
	public void successInactiveAccount() throws AccountException {
		savings.inactive(checking);

		assertTrue(savings.isInactive());
		assertEquals(100, checking.getBalance());
	}

	// OK 1.1 b
	@Test
	public void isInactiveAccount() throws AccountException {
		savings.inactive(checking);

		try {
			savings.inactive(checking);
			fail();
		} catch (AccountException e) {
			assertTrue(savings.isInactive());
		}
	}

	// OK 1.1 c
	@Test
	public void differentClients() throws AccountException {
		try {
			checking.inactive(otherChecking);
			fail();
		} catch (AccountException e) {
			assertFalse(checking.isInactive());
			assertFalse(otherChecking.isInactive());
		}
	}

	// OK 1.1 d
	@Test
	public void sumOfBalancesIsNegative() throws AccountException {
		salary.withdraw(900);
		try {
			salary.inactive(checking);
			fail();
		} catch (AccountException e) {
			assertFalse(salary.isInactive());
			assertEquals(0, checking.getBalance());
			assertEquals(-900, salary.getBalance());
		}
	}

	// OK 1.1 e
	@Test(expected = AccountException.class)
	public void balanceOfAccountIsZeroAndCheckingIsNotNull() throws AccountException {
		young.inactive(checking);
	}

	// OK 1.1 f
	@Test
	public void balanceOfAccountIsZeroAndCheckingIsNull() throws AccountException {
		young.inactive(null);

		assertTrue(young.isInactive());
	}

	// OK 1.2
	@Test
	public void depositActiveAccount() throws AccountException {
		checking.deposit(100);
		assertEquals(100, checking.getBalance());
	}

	@Test(expected = AccountException.class)
	public void depositInactiveAccount() throws AccountException {
		checking.inactive(null);

		checking.deposit(100);
	}

	@Test
	public void withdrawActiveAccount() throws AccountException {
		otherChecking.withdraw(50);
		assertEquals(50, otherChecking.getBalance());
	}

	@Test(expected = AccountException.class)
	public void withdrawInactiveAccount() throws AccountException {
		checking.inactive(null);

		checking.withdraw(50);
	}

	// 1.3 OK
	@Test
	public void clientHasOneActiveAndTwoInactive() throws AccountException {
		savings.inactive(checking);
		salary.inactive(null);

		assertFalse(client.isInactive());
	}

	@Test
	public void clientHasAllThreeInactive() throws AccountException {
		savings.inactive(checking);
		salary.inactive(null);
		checking.withdraw(100);
		checking.inactive(null);

		assertTrue(client.isInactive());
	}

	// 1.4 OK
	@Test
	public void countNumberOfInactiveAccountsForClientThatHasOneActiveAndTwoInactive() throws AccountException {
		savings.inactive(checking);
		salary.inactive(null);

		assertEquals(2, client.numberOfInactiveAccounts());
	}

	@Test
	public void countNumberOfInactiveAccountsForClientThatHasAllThreeInactive() throws AccountException {
		savings.inactive(checking);
		salary.inactive(null);
		checking.withdraw(100);
		checking.inactive(null);

		assertEquals(3, client.numberOfInactiveAccounts());
	}

	// 2 OK
	@Test
	public void CheckingClientHasOver18() throws ClientException {
		int numberOfAccounts = bank.getTotalNumberOfAccounts();
		try {
			new CheckingAccount(youngClient, 0);
			fail();
		} catch (AccountException e) {
			assertEquals(numberOfAccounts, bank.getTotalNumberOfAccounts());
		}

	}

	@Test
	public void SavingsClientHasOver18() throws ClientException {
		int numberOfAccounts = bank.getTotalNumberOfAccounts();
		try {
			new SavingsAccount(youngClient, 0, 100);
			fail();
		} catch (AccountException e) {
			assertEquals(numberOfAccounts, bank.getTotalNumberOfAccounts());
		}

	}

	@Test
	public void SalaryClientHasOver18() throws ClientException {
		int numberOfAccounts = bank.getTotalNumberOfAccounts();
		try {
			new SalaryAccount(youngClient, 0, 1000);
			fail();
		} catch (AccountException e) {
			assertEquals(numberOfAccounts, bank.getTotalNumberOfAccounts());
		}

	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
