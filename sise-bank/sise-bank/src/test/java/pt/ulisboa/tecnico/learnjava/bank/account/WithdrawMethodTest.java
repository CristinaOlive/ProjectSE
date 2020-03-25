package pt.ulisboa.tecnico.learnjava.bank.account;

import static org.junit.Assert.assertEquals;
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

public class WithdrawMethodTest {
	private static final String OWNER_NAME = "JoÃ£o";

	private CheckingAccount checking;
	private SavingsAccount savings;
	private SalaryAccount salary;
	private YoungAccount young;

	@Before
	public void setUp() throws AccountException, BankException, ClientException {
		Bank bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		Client client = new Client(bank, personalInfo, "123456789", "987654321", 33);
		Client youngClient = new Client(bank, personalInfo, "123456780", "987654321", 17);

		checking = new CheckingAccount(client, 100);
		savings = new SavingsAccount(client, 100, 10);
		salary = new SalaryAccount(client, 100, 1000);
		young = new YoungAccount(youngClient, 100);
	}

	@Test
	public void successForCheckingAccount() throws AccountException {
		checking.withdraw(50);

		assertEquals(50, checking.getBalance());
	}

	@Test
	public void negativeAmountForCheckingAccount() {
		try {
			checking.withdraw(-10);
			fail();
		} catch (AccountException e) {
			assertEquals(100, checking.getBalance());
		}
	}

	@Test
	public void notEnoughBalanceForCheckingAccount() {
		try {
			checking.withdraw(200);
			fail();
		} catch (AccountException e) {
			assertEquals(100, checking.getBalance());
		}
	}

	@Test
	public void successForSavingsAccount() throws AccountException {
		savings.withdraw(100);

		assertEquals(0, savings.getBalance());
	}

	@Test
	public void negativeAmountForSavingsAccount() {
		try {
			savings.withdraw(-10);
			fail();
		} catch (AccountException e) {
			assertEquals(100, checking.getBalance());
		}
	}

	@Test
	public void amountNotEqualToBalanceInSavingsAccount() {
		try {
			savings.withdraw(50);
			fail();
		} catch (AccountException e) {
			assertEquals(100, savings.getBalance());
		}
	}

	@Test
	public void successNegativeBalanceForSalaryAccount() throws AccountException {
		salary.withdraw(900);

		assertEquals(-800, salary.getBalance());
	}

	@Test
	public void negativeAmountForSalaryAccount() {
		try {
			salary.withdraw(-10);
			fail();
		} catch (AccountException e) {
			assertEquals(100, checking.getBalance());
		}
	}

	@Test
	public void failNegativeBalanceForSalaryAccount() throws AccountException {
		try {
			salary.withdraw(2000);
			fail();
		} catch (AccountException e) {
			assertEquals(100, salary.getBalance());
		}
	}

	@Test
	public void noWithdrawForYoung() throws AccountException {
		try {
			young.withdraw(100);
			fail();
		} catch (AccountException e) {
			assertEquals(100, young.getBalance());
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
