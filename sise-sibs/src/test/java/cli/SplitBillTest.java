package cli;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import CLI.MBWay;
import CLI.Tuple;
import CLI.exceptions.BillAmountException;
import CLI.exceptions.OverdraftException;
import CLI.exceptions.TooFriendlyException;
import CLI.exceptions.TooShyException;
import CLI.exceptions.UnregisteredNumberException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

@RunWith(MockitoJUnitRunner.class)
public class SplitBillTest {

	private List<Tuple<String, Integer>> instructions = new ArrayList<Tuple<String, Integer>>();
	private MBWay mockMBWay = new MBWay("999", "pt50");
	private int[] totals = { 2, 10 };

	@Before
	public void setUp() {
		Tuple<String, Integer> tuple1 = new Tuple(111, 3);
		Tuple<String, Integer> tuple2 = new Tuple(222, 2);
		Tuple<String, Integer> tuple3 = new Tuple(333, 5);
		this.instructions.add(tuple1);
		this.instructions.add(tuple2);
		this.instructions.add(tuple3);
	}

	// Test for OverdraftException
	@Test(expected = TooFriendlyException.class)
	public void transferMBWayTestTooFriendly()
			throws AccountException, OverdraftException, UnregisteredNumberException, SibsException, OperationException,
			TooFriendlyException, BillAmountException, TooShyException, AccountException {
		this.totals[0] = 1;
		this.mockMBWay.splitBill(this.instructions, this.totals);
	}

	@Test(expected = TooShyException.class)
	public void transferMBWayTooShy() throws AccountException, OverdraftException, UnregisteredNumberException,
			SibsException, OperationException, TooFriendlyException, BillAmountException, TooShyException {
		this.totals[0] = 3;
		this.mockMBWay.splitBill(this.instructions, this.totals);
	}

	@Test(expected = BillAmountException.class)
	public void transferMBWayBillAmount() throws AccountException, OverdraftException, UnregisteredNumberException,
			SibsException, OperationException, TooFriendlyException, BillAmountException, TooShyException {
		this.totals[1] = 13;
		this.mockMBWay.splitBill(this.instructions, this.totals);
	}

}
