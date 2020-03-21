package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodMock {
	@Test
	public void transferFails() throws BankException, OperationException, ClientException, AccountException, SibsException {
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
			verify(services, times(1)).deposit(targetIban, 100);
			assertEquals(1, sibs.getNumberOfOperations());
		}
	}

	@Test
	public void transferFailsSameBank() throws BankException, SibsException, OperationException, ClientException, AccountException {
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";

		when(services.checkAccount(targetIban)).thenReturn(true);
		when(services.checkAccount(sourceIban)).thenReturn(true);
		when(services.checkSameBank(sourceIban, targetIban)).thenReturn(true);

		when(services.deposit(targetIban, 100)).thenThrow(AccountException.class);

		try {
			sibs.transfer(sourceIban, targetIban, 100);
			fail();
		} catch (SibsException e) {
			verify(services, never()).withdraw(sourceIban, 100);
			verify(services, times(1)).deposit(targetIban, 100);
			assertEquals(1, sibs.getNumberOfOperations());
		}
	}
}
