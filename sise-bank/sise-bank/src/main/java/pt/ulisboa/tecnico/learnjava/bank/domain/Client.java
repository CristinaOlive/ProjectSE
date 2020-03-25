package pt.ulisboa.tecnico.learnjava.bank.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class Client {
	private final Set<Account> accounts = new HashSet<Account>();

	private final Bank bank;
	private final String firstName;
	private final String lastName;
	private final String nif;
	private final String phoneNumber;
	private final String address;
	private int age;

	public Client(Bank bank, String [] personalInfo, String nif, String phoneNumber, int age)
			throws ClientException {
		checkParameters(bank, nif, phoneNumber, age);

		this.bank = bank;
		firstName = personalInfo[0];
		lastName = personalInfo[1];
		this.nif = nif;
		this.phoneNumber = phoneNumber;
		address = personalInfo[2];
		this.age = age;

		bank.addClient(this);
	}

	private void checkParameters(Bank bank, String nif, String phoneNumber, int age) throws ClientException {
		if (age < 0) {
			throw new ClientException();
		}

		if (nif.length() != 9 || !nif.matches("[0-9]+")) {
			throw new ClientException();
		}

		if (phoneNumber.length() != 9 || !phoneNumber.matches("[0-9]+")) {
			throw new ClientException();
		}

		if (bank.getClientByNif(nif) != null) {
			throw new ClientException();
		}
	}

	public void addAccount(Account account) throws ClientException {
		if (accounts.size() == 5) {
			throw new ClientException();
		}

		accounts.add(account);
	}

	public void deleteAccount(Account account) {
		accounts.remove(account);
	}

	public boolean hasAccount(Account account) {
		return accounts.contains(account);
	}

	public int getNumberOfAccounts() {
		return accounts.size();
	}

	public Stream<Account> getAccounts() {
		return accounts.stream();
	}

	public void happyBirthDay() throws BankException, AccountException, ClientException {
		age++;

		if (age == 18) {
			Set<Account> accounts = new HashSet<Account>(this.accounts);
			for (Account account : accounts) {
				YoungAccount youngAccount = (YoungAccount) account;
				youngAccount.upgrade();
			}
		}
	}

	public boolean isInactive() {
		return accounts.stream().allMatch(a -> a.isInactive());
	}

	public int numberOfInactiveAccounts() {
		return (int) accounts.stream().filter(a -> a.isInactive()).count();
	}

	public Bank getBank() {
		return bank;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getNif() {
		return nif;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
