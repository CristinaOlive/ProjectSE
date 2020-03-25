package pt.ulisboa.tecnico.learnjava.bank.client;

import static org.junit.Assert.assertEquals;
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

public class HappyBirthdayMethodTest {
	private Bank bank;
	private Client youngClient;
	private YoungAccount young;
	private Services services;

	@Before
	public void setUp() throws AccountException, ClientException, BankException {
		services = new Services();
		bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		youngClient = new Client(bank, personalInfo, "123456780", "987654321", 16);

		young = (YoungAccount) services
				.getAccountByIban(bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0));
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);
		bank.createAccount(Bank.AccountType.YOUNG, youngClient, 100, 0);
	}

	@Test
	public void successNoUpgrade() throws BankException, AccountException, ClientException {
		youngClient.happyBirthDay();

		assertEquals(17, youngClient.getAge());
		assertTrue(youngClient.getAccounts().allMatch(a -> a instanceof YoungAccount));
	}

	@Test
	public void successUpGrade() throws BankException, AccountException, ClientException {
		youngClient.happyBirthDay();
		youngClient.happyBirthDay();

		assertEquals(18, youngClient.getAge());
		assertTrue(youngClient.getAccounts().allMatch(a -> a instanceof CheckingAccount));
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
