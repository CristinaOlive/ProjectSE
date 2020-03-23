package CLI;

import java.util.Scanner;

import CLI.exceptions.WrongNumberException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Main {

	public static void main(String[] args)
			throws SibsException, AccountException, OperationException, WrongNumberException {

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
//		case "mbway-split-bill":
//			controler.spltbill(input);
//			break first;
			}
		}
		controler.exit(input);
	}
}
