package pt.ulisboa.tecnico.learnjava.bank.bank;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class GetTotalBalanceMethodTest {
	private Bank bank;
	private Client client;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		client = new Client(bank, personalInfo, "123456789", "987654321", 33);
	}

	@Test
	public void fiveAccounts() throws BankException, AccountException, ClientException {
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);

		assertEquals(500, bank.getTotalBalance());
	}

	@Test
	public void noAccounts() {
		assertEquals(0, bank.getTotalBalance());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
