package pt.ulisboa.tecnico.learnjava.bank.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public abstract class Account {
	protected static int counter;

	private final String accountId;
	private final Client client;
	private int balance;
	private boolean inactive;

	public Account(Client client) throws AccountException, ClientException {
		this(client, 0);
	}

	public Account(Client client, int amount) throws AccountException, ClientException {
		if (client == null || amount < 0) {
			throw new AccountException();
		}

		checkClientAge(client);

		accountId = getNextAcccountId();
		this.client = client;
		balance = amount;

		client.addAccount(this);
	}

	protected abstract String getNextAcccountId();

	protected void checkClientAge(Client client) throws AccountException {
		if (client.getAge() < 18) {
			throw new AccountException();
		}
	}

	public int getBalance() {
		return balance;
	}

	public void deposit(int amount) throws AccountException {
		if (inactive) {
			throw new AccountException(amount);
		}

		if (amount <= 0) {
			throw new AccountException(amount);
		}
		balance = balance + amount;
	}

	public void withdraw(int amount) throws AccountException {
		if (inactive) {
			throw new AccountException(amount);
		}

		if (amount <= 0) {
			throw new AccountException();
		}

		balance = balance - amount;
	}

	public void inactive(CheckingAccount checking) throws AccountException {
		if (this.isInactive()) {
			throw new AccountException();
		}

		if (checking != null && client != checking.getClient()) {
			throw new AccountException();
		}

		if (balance > 0) {
			checking.deposit(balance);
		} else if (balance < 0) {
			checking.withdraw(-balance);
		}

		inactive = true;
		balance = 0;
	}

	public String getAccountId() {
		return accountId;
	}

	public Client getClient() {
		return client;
	}

	public boolean isInactive() {
		return inactive;
	}

}
