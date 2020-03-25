package pt.ulisboa.tecnico.learnjava.bank.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class DeleteAccountMethodTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";
	String[] personalInfo = new String[] {FIRST_NAME, LAST_NAME, ADDRESS};

	private Bank bank;
	private Client client;
	private Account account;
	private Services services;

	@Before
	public void setUp() throws BankException, ClientException, AccountException {
		services = new Services();
		bank = new Bank("CGD");
		client = new Client(bank, personalInfo, NIF, PHONE_NUMBER, 33);
		String iban = bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);
		account = services.getAccountByIban(iban);
	}

	@Test
	public void success() throws BankException, AccountException {
		bank.deleteAccount(account);

		assertEquals(0, bank.getTotalNumberOfAccounts());
		assertFalse(client.hasAccount(account));
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
