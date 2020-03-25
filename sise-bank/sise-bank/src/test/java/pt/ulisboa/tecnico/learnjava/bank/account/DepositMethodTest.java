package pt.ulisboa.tecnico.learnjava.bank.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.SavingsAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.YoungAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class DepositMethodTest {
	private CheckingAccount checking;
	private SavingsAccount savings;
	private YoungAccount young;

	@Before
	public void setUp() throws AccountException, ClientException, BankException {
		Bank bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		Client client = new Client(bank, personalInfo, "123456789", "987654321", 33);
		Client youngclient = new Client(bank, personalInfo, "123456780", "987654321", 17);

		checking = new CheckingAccount(client, 0);
		savings = new SavingsAccount(client, 100, 10);
		young = new YoungAccount(youngclient, 100);
	}

	@Test
	public void successForAccount() throws AccountException {
		checking.deposit(100);

		assertEquals(100, checking.getBalance());
	}

	@Test
	public void negativeAmountForAccount() {
		try {
			checking.deposit(-100);
			fail(); // here the test fails
		} catch (AccountException e) {
			// here is OK, the test passes
			assertEquals(0, checking.getBalance());
		}
	}

	@Test
	public void successForSavings() throws AccountException {
		savings.deposit(100);

		assertEquals(200, savings.getBalance());
		assertEquals(10, savings.getPoints());
	}

	@Test
	public void noMultipleValueForSavings() throws AccountException {
		try {
			savings.deposit(17);
			fail();
		} catch (AccountException e) {
			assertEquals(100, savings.getBalance());
			assertEquals(0, savings.getPoints());
		}
	}

	@Test
	public void successForYoung() throws AccountException {
		young.deposit(20);
		assertEquals(120, young.getBalance());
		assertEquals(2, young.getPoints());
	}

	@Test
	public void noMultipleOf10ForYoung() throws AccountException {
		try {
			young.deposit(17);
			fail();
		} catch (AccountException e) {
			assertEquals(100, savings.getBalance());
			assertEquals(0, savings.getPoints());
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
