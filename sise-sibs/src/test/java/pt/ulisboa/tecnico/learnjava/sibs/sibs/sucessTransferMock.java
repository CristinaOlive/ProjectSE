package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class sucessTransferMock {

	@Test
	public void success() throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";
		when(services.checkAccount(targetIban)).thenReturn(true);
		when(services.checkAccount(sourceIban)).thenReturn(true);

		sibs.transfer(sourceIban, targetIban, 100);

		verify(services).withdraw(sourceIban,111);
		verify(services).deposit(targetIban, 100);
		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
	}

	@Test
	public void successMockOnly() throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";

		when(services.checkAccount(targetIban)).thenReturn(true);

		try {
			sibs.transfer(sourceIban, targetIban, 100);
			fail();
		} catch(SibsException e) {
			verify(services, never()).deposit(targetIban, 100);
			verify(services, never()).withdraw(sourceIban, 100);
		}
	}

	@Test
	public void targetIbanDoesNotExist() throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";

		when(services.checkAccount(sourceIban)).thenReturn(true);

		try {
			sibs.transfer(sourceIban, targetIban, 100);
			fail();
		} catch(SibsException e) {
			verify(services, never()).deposit(targetIban, 100);
			verify(services, never()).withdraw(sourceIban, 100);
		}
	}

	@Test
	public void checkSameBank() throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";

		when(services.checkAccount(targetIban)).thenReturn(true);
		when(services.checkAccount(sourceIban)).thenReturn(true);
		when(services.checkSameBank(sourceIban, targetIban)).thenReturn(true);

		sibs.transfer(sourceIban, targetIban, 100);

		verify(services).withdraw(sourceIban,100);
		verify(services).deposit(targetIban, 100);
		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertTrue(services.checkSameBank(sourceIban, targetIban));
	}

	@Test
	public void checkDifferentBank() throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);
		String sourceIban = "CDG123";
		String targetIban = "NBB345";

		when(services.checkAccount(targetIban)).thenReturn(true);
		when(services.checkAccount(sourceIban)).thenReturn(true);
		when(services.checkSameBank(sourceIban, targetIban)).thenReturn(false);

		sibs.transfer(sourceIban, targetIban, 100);

		verify(services).withdraw(sourceIban,111);
		verify(services).deposit(targetIban, 100);
		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertFalse(services.checkSameBank(sourceIban, targetIban));
	}
}