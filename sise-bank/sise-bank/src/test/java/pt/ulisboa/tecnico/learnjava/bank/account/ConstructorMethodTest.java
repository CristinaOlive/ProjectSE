package pt.ulisboa.tecnico.learnjava.bank.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class ConstructorMethodTest {
	private static final int AMOUNT = 100;

	private Bank bank;
	private Client client;

	@Before
	public void setUp() throws BankException, ClientException {
		bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		client = new Client(bank, personalInfo, "123456789", "987654321", 33);
	}

	@Test
	public void success() throws AccountException, ClientException {
		Account account = new CheckingAccount(client, AMOUNT);

		assertEquals(client, account.getClient());
		assertEquals(AMOUNT, account.getBalance());
		assertTrue(client.hasAccount(account));
	}

	@Test
	public void nullClient() throws ClientException {
		try {
			new CheckingAccount(null, AMOUNT);
			fail();
		} catch (AccountException e) {
			// passes
		}
	}

	@Test
	public void limitOfAccountsPerClient() throws AccountException, ClientException {
		new CheckingAccount(client, AMOUNT);
		new CheckingAccount(client, AMOUNT);
		new CheckingAccount(client, AMOUNT);
		new CheckingAccount(client, AMOUNT);
		new CheckingAccount(client, AMOUNT);

		try {
			new CheckingAccount(client, AMOUNT);
			fail();
		} catch (ClientException e) {
			assertEquals(5, client.getNumberOfAccounts());
		}

	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
