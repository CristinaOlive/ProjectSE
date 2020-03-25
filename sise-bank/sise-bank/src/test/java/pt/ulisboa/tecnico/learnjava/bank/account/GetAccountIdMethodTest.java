package pt.ulisboa.tecnico.learnjava.bank.account;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.SalaryAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.SavingsAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class GetAccountIdMethodTest {
	private static final String OWNER_NAME = "SimÃ£o";

	private CheckingAccount checking;
	private SavingsAccount savings;
	private SalaryAccount salary;

	@Before
	public void setUp() throws AccountException, ClientException, BankException {
		Bank bank = new Bank("CGD");
		String[] personalInfo = new String[] {"José", "Manuel", "Street"};
		Client client = new Client(bank, personalInfo, "123456789", "987654321", 33);

		checking = new CheckingAccount(client, 100);
		savings = new SavingsAccount(client, 100, 10);
		salary = new SalaryAccount(client, 100, 1000);
	}

	@Test
	public void successForCheckingAccount() {
		assertTrue(checking.getAccountId().startsWith(AccountType.CHECKING.getPrefix()));
	}

	@Test
	public void successForSavingsAccount() {
		assertTrue(savings.getAccountId().startsWith(AccountType.SAVINGS.getPrefix()));
	}

	@Test
	public void successForSalaryAccount() {
		assertTrue(salary.getAccountId().startsWith(AccountType.SALARY.getPrefix()));
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
