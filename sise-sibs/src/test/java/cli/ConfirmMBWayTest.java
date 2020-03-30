package cli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import CLI.MBWay;
import CLI.exceptions.CodeConfirmationException;

public class ConfirmMBWayTest {

	private static final String PHONENUMBER = "111";
	private static final String IBAN = "SSS";
	private static MBWay mbwayTest = new MBWay();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = NullPointerException.class)
	public void testConfirmMBWay3() throws CodeConfirmationException {
		mbwayTest.confirmMBWay(0);
	}

	@Test(expected = CodeConfirmationException.class)
	public void testConfirmMBWay() throws CodeConfirmationException {
		mbwayTest.associateMBWay(PHONENUMBER, IBAN);
		mbwayTest.confirmMBWay(0);
	}

	@Test
	public void testConfirmMBWay2() throws CodeConfirmationException {
		int code = mbwayTest.associateMBWay(PHONENUMBER, IBAN);
		mbwayTest.confirmMBWay(code);
		assertTrue(mbwayTest.hasPhoneNumber(PHONENUMBER));
		assertSame(IBAN, mbwayTest.getIBANByPhoneNumber(PHONENUMBER));
		assertNotNull(mbwayTest.getMBWay());
	}

}
