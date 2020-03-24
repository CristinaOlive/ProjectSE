package CLI;

import java.util.List;
import java.util.Scanner;

import CLI.exceptions.BillAmountException;
import CLI.exceptions.CodeConfirmationException;
import CLI.exceptions.OverdraftException;
import CLI.exceptions.TooFriendlyException;
import CLI.exceptions.TooShyException;
import CLI.exceptions.UnregisteredNumberException;
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
	 * Action methods: Methods that act both on the console and on the model itself
	 */

	/* Performs associate-MBWay operation */
	public void associate(String[] info) {
		int code = this.mbwayconsole.associateMBWay(info[2], info[1]);
		this.view.associateS(code);
	}

	/* Performs confirm-MBWay operation */
	public void confirm(String[] info) throws CodeConfirmationException {
		int code = Integer.parseInt(info[1]);
		try {
			this.mbwayconsole.confirmMBWay(code);
			this.view.confirmS();
		} catch (CodeConfirmationException ex) {
			this.view.confirmF();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/* Performs transfer-MBWay operation */
	public void transfer(String[] info)
			throws NumberFormatException, SibsException, AccountException, OperationException {
		try {
			this.mbwayconsole.transferMBWay(info[1], info[2], Integer.parseInt(info[3]));
			this.view.transferS();
		} catch (UnregisteredNumberException ex) {
			this.view.transferF1();
		} catch (OverdraftException ax) {
			this.view.transferF2();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/* Performs split-bill operation */

	public void splitBill(List<Tuple<String, Integer>> instructions, int numFriends, int total)
			throws SibsException, AccountException, OperationException {
		try {
			this.mbwayconsole.splitBill(instructions, numFriends, total);
			this.view.splitbillS();
		} catch (UnregisteredNumberException ex) {
			this.view.splitbillF1(ex.getMessage());
		} catch (OverdraftException ex) {
			this.view.splitbillF2();
		} catch (TooFriendlyException ex) {
			this.view.splitbillF3();
		} catch (TooShyException ex) {
			this.view.splitbillF4();
		} catch (BillAmountException ex) {
			this.view.splitbillF5();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
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
