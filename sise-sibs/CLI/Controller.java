package CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Controller {

	private MBWay mbwayconsole;
	private View view;

	public Controller(MBWay mbwayconsole, View view) {
		this.mbwayconsole = mbwayconsole;
		this.view = view;
	}

	/*
	 * Methods for: Start and end of input console interface
	 */

	/* Starts input console */
	public Scanner start() {
		Scanner input = this.view.start();
		return input;
	}

	/* Closes input from the console */
	public void exit(Scanner input) {
		this.view.exit(input);
	}

	/*-----------------------------------------------------------------------------------------------*/

	/*
	 * Action methods: Methods that act both ont the console and on the model itself
	 */

	/* Performs associate-MBWay operation */
	public void associate(String[] info) {
		int code = this.mbwayconsole.associateMBWay(info[2], info[1]);
		this.view.associateS(code);
	}

	/* Performs confirm-MBWay operation */
	public void confirm(String[] info) {
		int code = Integer.parseInt(info[1]);
		if (this.mbwayconsole.getTempX() == code) {
			this.mbwayconsole.confirmMBWay(code);
			this.view.confirmS();
		} else {
			this.view.confirmF();
		}
	}

	/* Performs transfer-MBWay operation */
	public void transfer(String[] info)
			throws NumberFormatException, SibsException, AccountException, OperationException {
		try {
			if (!(this.mbwayconsole.isNumRegist(info[2]))) {
				this.view.transferF1();
			}
			if (getBalanceByPhoneNumber(info[1]) < Integer.parseInt(info[3])) {
				this.view.transferF2();
			}
			this.mbwayconsole.transferMBWay(info[1], info[2], Integer.parseInt(info[3]));
			this.view.transferS();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/* Performs split-bill operation */

	public void splitBill(Scanner input) throws SibsException, AccountException, OperationException {
		String[] info = inputParser(input.nextLine());
		int numFriends = Integer.parseInt(info[1]);
		int total = Integer.parseInt(info[2]);
		List<Tuple<String, Integer>> instructions = getInstructions(input);
		if (splitIf1(instructions) && splitIf2(instructions, numFriends, total)) {
			try {
				this.mbwayconsole.splitBill(instructions);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	/*------------------------------------------------------------------------------------------------*/

	/*
	 * Auxiliary methods
	 */

	public boolean splitIf1(List<Tuple<String, Integer>> instructions) {
		for (Tuple<String, Integer> tuple : instructions) {
			String phoneNumber = tuple.x;
			String IBAN = this.mbwayconsole.getIBANByPhoneNumber(phoneNumber);
			if (!this.mbwayconsole.isNumRegist(phoneNumber)) {
				this.view.splitbillF1(phoneNumber);
				return false;
			}
			if (this.mbwayconsole.getBalanceByIBAN(IBAN) < tuple.y) {
				this.view.splitbillF2();
				return false;
			}
		}
		return true;
	}

	public boolean splitIf2(List<Tuple<String, Integer>> instructions, int numFriends, int total) {
		if (instructions.size() - 1 > numFriends) {
			this.view.splitbillF3();
			return false;
		}
		if (instructions.size() - 1 < numFriends) {
			this.view.splitbillF4();
			return false;
		}
		if (sumAllAccount(instructions) != total) {
			this.view.splitbillF5();
			return false;
		}
		return true;
	}

	/* Finds the total value of all payment instructions */
	public int sumAllAccount(List<Tuple<String, Integer>> instructions) {
		int total = 0;
		for (Tuple<String, Integer> tuple : instructions) {
			int amount = tuple.y;
			total += amount;
		}
		return total;
	}

	/*
	 * Uses the Key value PhoneNumber to get the IBAN in the DB - MBWay - and uses
	 * said IBAN o check the balance of the Account.
	 */
	public int getBalanceByPhoneNumber(String phoneNumber) {
		return this.mbwayconsole.getBalanceByIBAN(this.mbwayconsole.getIBANByPhoneNumber(phoneNumber));
	}

	/*
	 * Gets several input lines and creates a List object with all the instructions
	 */
	public List<Tuple<String, Integer>> getInstructions(Scanner input) {
		List<Tuple<String, Integer>> instructions = new ArrayList<Tuple<String, Integer>>();
		String[] info = { "" };
		while (!(info[0].equals("end"))) {
			info = inputParser(input.nextLine());
			Tuple<String, Integer> tuplo = new Tuple<String, Integer>(info[1], Integer.parseInt(info[2]));
			instructions.add(tuplo);
		}
		return instructions;
	}

	/*
	 * Transforms an inputLine into a cohesive & clean array of strings to be read
	 */
	public String[] inputParser(String string) {
		String delims = "[ <>]+";
		String[] info = string.split(delims);
		return info;
	}

}
