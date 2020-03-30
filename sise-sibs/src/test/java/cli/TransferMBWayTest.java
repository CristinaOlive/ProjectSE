package cli;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import CLI.MBWay;
import CLI.exceptions.OverdraftException;
import CLI.exceptions.UnregisteredNumberException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

@RunWith(MockitoJUnitRunner.class)
public class TransferMBWayTest {

	@Before
	public void setUp() {

	}

	@Spy
	MBWay mockMBWay = new MBWay("111", "111");

	// Test for OverdraftException
	@Test
	public void transferMBWayTestOverdraft() throws AccountException, OverdraftException, UnregisteredNumberException,
			SibsException, OperationException {

		String[] numbers = { "111", "111" };
		Mockito.doReturn(50).when(this.mockMBWay).getBalanceByIBAN(anyString());
		Mockito.doReturn(true).when(this.mockMBWay).isNumRegist(anyString());
		try {
			this.mockMBWay.transferMBWay(numbers, 75);
		} catch (OverdraftException ex) {
			verify(this.mockMBWay, times(2)).isNumRegist(any());
			verify(this.mockMBWay, times(1)).getBalanceByIBAN(any());
		}
	}

	@Test
	public void transferMBWayTestNumRegist() throws AccountException, OverdraftException, UnregisteredNumberException,
			SibsException, OperationException {

		String[] numbers = { "111", "111" };
		Mockito.doReturn(false).when(this.mockMBWay).isNumRegist(anyString());
		try {
			this.mockMBWay.transferMBWay(numbers, 75);
		} catch (UnregisteredNumberException ex) {
			verify(this.mockMBWay, times(0)).getBalanceByIBAN(any());
			verify(this.mockMBWay, times(1)).isNumRegist(any());
		}

	}

	@After
	public void tearDown() {

	}

}
