package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
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
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";
		when(services.checkAccount(targetIban)).thenReturn(true);
		when(services.checkAccount(sourceIban)).thenReturn(true);

		when(services.deposit(targetIban, 100)).thenThrow(AccountException.class);

		try {
			sibs.transfer(sourceIban, targetIban, 100);
			fail();
		} catch (SibsException e) {
			verify(services, never()).withdraw(sourceIban, 111);
			verify(services, times(4)).deposit(targetIban, 100);
			assertEquals(1, sibs.getNumberOfOperations());
			assertEquals("error", sibs.getOperation(0).getState());
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
