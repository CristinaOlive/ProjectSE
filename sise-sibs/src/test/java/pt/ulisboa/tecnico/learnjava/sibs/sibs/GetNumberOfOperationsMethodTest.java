package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class GetNumberOfOperationsMethodTest {
	private static final String TARGET_IBAN = "TargetIban";
	private static final int VALUE = 100;

	private Sibs sibs;

	@Before
	public void setUp() throws OperationException, SibsException {
		sibs = new Sibs(3, new Services());
		sibs.addOperation(Operation.OPERATION_PAYMENT, null, TARGET_IBAN, VALUE, "completed");
	}

	@Test
	public void success() throws SibsException, OperationException {
		sibs.addOperation(Operation.OPERATION_PAYMENT, null, TARGET_IBAN, VALUE, "completed");

		assertEquals(2, sibs.getNumberOfOperations());
	}

	@After
	public void tearDown() {
		sibs = null;
	}

}