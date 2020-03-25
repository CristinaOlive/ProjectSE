package pt.ulisboa.tecnico.learnjava.bank.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.YoungAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class UpgradeMethodTest {
	private Bank bank;
	private Client youngClient;
	private YoungAccount young;
	private Services services;

	@Before
	public void setUp() throws AccountException, ClientException, BankException {
		services = new Services();
		bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		youngClient = new Client(bank, personalInfo, "123456780", "987654321", 17);

		young = (YoungAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0));
	}

	@Test
	public void success() throws BankException, AccountException, ClientException {
		young.deposit(19_000);

		youngClient.setAge(18);
		CheckingAccount checking = young.upgrade();

		assertEquals(1, bank.getTotalNumberOfAccounts());
		assertEquals(1, youngClient.getNumberOfAccounts());
		assertTrue(youngClient.hasAccount(checking));

		assertEquals(youngClient, checking.getClient());
		assertEquals(19102, checking.getBalance());
	}

	@Test
	public void successWith5Accounts() throws BankException, AccountException, ClientException {
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);

		youngClient.setAge(18);
		CheckingAccount checking = young.upgrade();

		assertEquals(5, bank.getTotalNumberOfAccounts());
		assertEquals(5, youngClient.getNumberOfAccounts());
		assertTrue(youngClient.hasAccount(checking));
		assertFalse(youngClient.hasAccount(young));

		assertEquals(youngClient, checking.getClient());
		assertEquals(100, checking.getBalance());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
