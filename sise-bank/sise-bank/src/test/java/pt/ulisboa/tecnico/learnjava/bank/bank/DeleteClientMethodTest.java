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

public class DeleteClientMethodTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";
	String[] personalInfo = new String[] {FIRST_NAME, LAST_NAME, ADDRESS};

	private Bank bank;
	private Client client;

	@Before
	public void setUp() throws BankException, ClientException {
		bank = new Bank("CGD");
		client = new Client(bank, personalInfo, NIF, PHONE_NUMBER, 33);
	}

	@Test
	public void success() throws BankException, AccountException {
		bank.deleteClient(NIF);

		assertEquals(0, bank.getTotalNumberOfClients());
	}

	@Test
	public void successClientWithAccounts() throws BankException, AccountException, ClientException {
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);
		bank.createAccount(Bank.AccountType.CHECKING, client, 100, 0);

		bank.deleteClient(NIF);

		assertEquals(0, bank.getTotalNumberOfClients());
		assertEquals(0, bank.getTotalNumberOfAccounts());
	}

	@Test(expected = BankException.class)
	public void noClientForNif() throws BankException, AccountException {
		bank.deleteClient("123456780");
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
