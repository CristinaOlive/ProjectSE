package CLI;

import java.util.Scanner;

public class View {

	public View() {
	}

	/*
	 * Start and exit methods
	 *
	 */

	/* Starts a scanner console for input */
	public Scanner start() {
		Scanner input = new Scanner(System.in);
		return input;
	}

	/* Closes input console */
	public void exit(Scanner input) {
		input.close();
	}

	/*
	 *
	 */

	// Outputs for associate method
	public void associateS(int code) {
		System.out.println("Code: " + code + " (don't share it with anyone)");
	}

	// Outputs for confirm method
	public void confirmS() {
		System.out.println("MBWAY association confirmed successfully!");
	}

	public void confirmF() {
		System.out.println("Wrong confirmation code. Try association again");
	}

	// Outputs for transfer method

	public void transferS() {
		System.out.println("Transfer performed successfully!");
	}

	public void transferF1() {
		System.out.println("Wrong phone number.");
	}

	public void transferF2() {
		System.out.println("Not enough money on the source account.");
	}

	// Outputs for splitbill method

	public void splitbillS() {
		System.out.println("Bill payed successfully");
	}

	public void splitbillF1(String phoneNumber) {
		System.out.println("Friend " + phoneNumber + "is not registered");
	}

	public void splitbillF2() {
		System.out.println("Oh no! One friend does not have money to pay");
	}

	public void splitbillF3() {
		System.out.println("Oh no! Too many friends!");
	}

	public void splitbillF4() {
		System.out.println("Oh no! (At least) One friend is missing");
	}

	public void splitbillF5() {
		System.out.println("Something went wrong. Did you set the bill amount right?");
	}
}
