package CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import CLI.exceptions.CodeConfirmationException;
import CLI.exceptions.OverdraftException;
import CLI.exceptions.UnregisteredNumberException;
import CLI.exceptions.WrongNumberException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Main {

	public static void main(String[] args) throws SibsException, AccountException, OperationException,
			WrongNumberException, CodeConfirmationException, UnregisteredNumberException, OverdraftException {

		MBWay mbway = new MBWay("999", "pt50");
		View view = new View();
		Controller controler = new Controller(mbway, view);
		Scanner input = controler.start();
		String[] info = { "" };
		while (!(info[0].equals("exit"))) {
			info = controler.inputParser(input.nextLine());
			switch (info[0]) {
			case "associate-mbway":
				controler.associate(info);
				break;
			case "confirm-mbway":
				controler.confirm(info);
				break;
			case "mbway-transfer":
				controler.transfer(info);
				break;
			case "mbway-split-bill":
				List<Tuple<String, Integer>> instructions = new ArrayList<Tuple<String, Integer>>();
				int numFriends = Integer.parseInt(info[1]);
				int total = Integer.parseInt(info[2]);
				while (!(info[0].equals("end"))) {
					info = controler.inputParser(input.nextLine());
					Tuple<String, Integer> tuplo = new Tuple<String, Integer>(info[1], Integer.parseInt(info[2]));
					instructions.add(tuplo);
				}
				controler.splitBill(instructions, numFriends, total);
				break;
			}
		}
		controler.exit(input);
	}
}
