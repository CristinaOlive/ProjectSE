package CLI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CLI.exceptions.BillAmountException;
import CLI.exceptions.CodeConfirmationException;
import CLI.exceptions.OverdraftException;
import CLI.exceptions.TooFriendlyException;
import CLI.exceptions.TooShyException;
import CLI.exceptions.UnregisteredNumberException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MBWay {

	private String phoneNumber;
	private String IBAN;
	private Services services;
	private Sibs sibs = new Sibs(100, this.services);
	private static Map<String, String> MBWay = new HashMap<String, String>(); // MBWAY -> phoneNumber, IBAN
	private Tuple<Integer, MBWay> temp; // code, MBWay

	public MBWay() {
	}

	public MBWay(String phoneNumber, String IBAN) {
		this.phoneNumber = phoneNumber;
		this.IBAN = IBAN;
		MBWay.put(phoneNumber, IBAN);
	}

	/*
	 * Associates a number with an IBAN (this serves exclusively to build the DB of
	 * associated numbers, as the current user is assumed to be allready loggedin
	 */
	public int associateMBWay(String phoneNumber, String IBAN) {
		int code = (int) (Math.random() * (999999));
		MBWay newMB = new MBWay(phoneNumber, IBAN);
		this.temp = new Tuple<Integer, MBWay>(code, newMB);
		return code;
	}

	/*
	 * Confirms the last inserted association with an input with the following
	 * style: confirm-mbway <Code>
	 */
	public void confirmMBWay(int code) throws CodeConfirmationException {
		if (!(getTempX() == code)) {
			throw new CodeConfirmationException();
		}
		MBWay.put(this.temp.y.phoneNumber, this.temp.y.IBAN);
		this.temp.x = null;
		this.temp.y = null;
	}

	/*
	 * Transfers money from one account to the other, throws two important
	 * exceptions that help the splitbill method not be overloaded with if
	 * statements; numbers[0] -> sourceNumber; numbers[1] -> targetNumber
	 */
	public void transferMBWay(String[] numbers, int amount) throws AccountException, OverdraftException,
			UnregisteredNumberException, SibsException, OperationException {
		String sourceIBAN = MBWay.get(numbers[0]);
		String targetIBAN = MBWay.get(numbers[1]);
		if (!isNumRegist(numbers[0]) || !isNumRegist(numbers[1])) {
			throw new UnregisteredNumberException();
		}
		if (getBalanceByIBAN(sourceIBAN) < amount) {
			throw new OverdraftException();
		}
		this.sibs.transfer(sourceIBAN, targetIBAN, amount);
	}

	/*
	 * Performs split bill assuming the following instruction correspondence:
	 * mbway-split-bil <NUMBER_OF_FRIENDS> <AMOUNT> || friend <PHONE_NUMBER>
	 * <AMOUNT> || friend <PHONE_NUMBER> <AMOUNT> || friend <PHONE_NUMBER> <AMOUNT>
	 * || (..) || end
	 *
	 * corresponds to: decisiontest <numberoffriends> <totalbillamount> ||
	 * decisiontest <billowner/target phone number> <amount to be paid by
	 * loggedinuser> || decisiontest <sourceNumber-friend that is paying> <amount to
	 * be paid by current friend> || decisiontest <sourceNumber-friend that is
	 * paying> <amount to be paid by current friend> || (...) || end --> totals[0] =
	 * total number of friends -> totals[1] = total bill amount; --> TransferMBWay
	 * throws remaining exceptions
	 */
	public void splitBill(List<Tuple<String, Integer>> instructions, int[] totals)
			throws UnregisteredNumberException, AccountException, OverdraftException, TooFriendlyException,
			SibsException, OperationException, TooShyException, BillAmountException {
		if (instructions.size() - 1 > totals[0]) {
			throw new TooFriendlyException();
		}
		if (instructions.size() - 1 < totals[0]) {
			throw new TooShyException();
		}
		if (sumAllAccount(instructions) != totals[1]) {
			throw new BillAmountException();
		}
		String[] numbers = { this.phoneNumber, instructions.get(0).x };
		int amountuser1 = instructions.get(0).y;
		transferMBWay(numbers, amountuser1);
		for (int i = 1; i < instructions.size(); i++) {
			numbers[0] = instructions.get(i).x;
			int amountPerUser = instructions.get(i).y;
			transferMBWay(numbers, amountPerUser);
		}
	}

	/* Auxiliary methods: */

	/* ------------------------------------------------------- */

	/* Finds the total value of all payment instructions */
	public int sumAllAccount(List<Tuple<String, Integer>> instructions) {
		int total = 0;
		for (Tuple<String, Integer> tuple : instructions) {
			int amount = tuple.y;
			total += amount;
		}
		return total;
	}

	// Getters & Setters & Verifiers below:

	public String getIBAN() {
		return this.IBAN;
	}

	public void setIBAN(String iBAN) {
		this.IBAN = iBAN;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getTempX() {
		return this.temp.x;
	}

	public boolean isNumRegist(String phoneNumber) {
		return MBWay.containsKey(phoneNumber);
	}

	public int getBalanceByIBAN(String IBAN) throws AccountException {
		int balance = this.services.getAccountByIban(IBAN).getBalance();
		return balance;
	}

	public String getIBANByPhoneNumber(String phoneNumber) {
		return MBWay.get(phoneNumber);
	}

}
