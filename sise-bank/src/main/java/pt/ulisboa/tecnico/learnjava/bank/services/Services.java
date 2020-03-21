package pt.ulisboa.tecnico.learnjava.bank.services;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;

public class Services {
	public boolean deposit(String iban, int amount) throws AccountException {
		Account account = getAccountByIban(iban);
		account.deposit(amount);
		return false;
	}

	public boolean withdraw(String iban, int amount) throws AccountException {
		Account account = getAccountByIban(iban);
		account.withdraw(amount);
		return false;
	}

	public Account getAccountByIban(String iban) throws AccountException {
		String code = iban.substring(0, 3);
		String accountId = iban.substring(3);

		Bank bank = Bank.getBankByCode(code);
		Account account = bank.getAccountByAccountId(accountId);

		return account;
	}

	public boolean checkAccount(String iban) throws AccountException {
		if(getAccountByIban(iban) == null) {
			return false;
		}
		return true;
	}

	public boolean checkSameBank(String sourceIban, String targetBank) {
		String sourceCode = sourceIban.substring(0, 3);
		String targetCode = targetBank.substring(0, 3);
		if(!sourceCode.equals(targetCode)) {
			return false;
		}
		return true;
	}
}
