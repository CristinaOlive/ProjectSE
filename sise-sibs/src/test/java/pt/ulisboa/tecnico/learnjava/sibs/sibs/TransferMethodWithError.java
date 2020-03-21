package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodWithError {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";

	@Test
	public void successRetry() throws BankException, AccountException, SibsException, OperationException, ClientException {
		Bank sourceBank= new Bank("CGD");
		Bank targetBank= new Bank("BPI");
		Client sourceClient= new Client(sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		Client targetClient= new Client(targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
		Services services = new Services();
		String sourceIban = sourceBank.createAccount(Bank.AccountType.CHECKING, sourceClient, 1000, 0);
		String targetIban = targetBank.createAccount(Bank.AccountType.CHECKING, targetClient, 1000, 0);
		Sibs sibs = mock(Sibs.class);
		sibs.transfer(sourceIban, targetIban, 100);
		sibs.transfer(sourceIban, targetIban, 100);
		when(sibs.transfer(sourceIban, targetIban, 100)).thenThrow(SibsException.class);
		try {
			sibs.transfer(sourceIban, targetIban, 100);
			fail();
		} catch (SibsException e) {
			verify(sibs, times(3)).transfer(sourceIban, targetIban, 100);
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
