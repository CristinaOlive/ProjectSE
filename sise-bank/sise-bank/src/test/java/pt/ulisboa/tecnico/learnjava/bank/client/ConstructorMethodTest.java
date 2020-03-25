package pt.ulisboa.tecnico.learnjava.bank.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class ConstructorMethodTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";
	private static final int AGE = 33;
	String[] personalInfo = new String[] {FIRST_NAME, LAST_NAME, ADDRESS};

	private Bank bank;

	@Before
	public void setUp() throws BankException {
		bank = new Bank("CGD");
	}

	@Test
	public void success() throws ClientException {
		Client client = new Client(bank, personalInfo, NIF, PHONE_NUMBER, AGE);

		assertEquals(bank, client.getBank());
		assertEquals(FIRST_NAME, client.getFirstName());
		assertEquals(LAST_NAME, client.getLastName());
		assertEquals(NIF, client.getNif());
		assertEquals(PHONE_NUMBER, client.getPhoneNumber());
		assertEquals(ADDRESS, client.getAddress());
		assertTrue(bank.isClientOfBank(client));
	}

	@Test(expected = ClientException.class)
	public void negativeAge() throws ClientException {
		new Client(bank, personalInfo, "12345678A", PHONE_NUMBER, -1);
	}

	@Test(expected = ClientException.class)
	public void no9DigitsNif() throws ClientException {
		new Client(bank, personalInfo, "12345678A", PHONE_NUMBER, AGE);
	}

	@Test(expected = ClientException.class)
	public void no9DigitsPhoneNumber() throws ClientException {
		new Client(bank, personalInfo, NIF, "A87654321", AGE);
	}

	public void twoClientsSameNif() throws ClientException {
		new Client(bank, personalInfo, NIF, "A87654321", AGE);
		try {
			new Client(bank, personalInfo, NIF, "A87654321", AGE);
			fail();
		} catch (ClientException e) {
			assertEquals(1, bank.getTotalNumberOfClients());
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
