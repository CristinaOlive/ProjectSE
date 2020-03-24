package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public interface State {
	String process(setState wrapper, TransferOperation wrapper2, Services services) throws AccountException, SibsException, OperationException;
}

class setState {
	private State currentState;
	private int tries;

	public setState(String state) {
		tries = 2;
		if(state.isEmpty()) {
			currentState = new Registered();
		}
		if(state.equals("registered")) {
			currentState = new Deposited();
		}
		if(state.equals("deposited")) {
			currentState = new Withdraw();
		}
		if(state.equals("withdrawn")) {
			currentState = new Completed();
		}
	}

	public void setCurrentstate(String state) {
		if(state.isEmpty()) {
			currentState = new Registered();
		}
		if(state.equals("registered")) {
			currentState = new Deposited();
		}
		if(state.equals("deposited")) {
			currentState = new Withdraw();
		}
		if(state.equals("withdrawn")) {
			currentState = new Completed();
		}
	}

	public void set_state(State s) {
		currentState = s;
	}

	public String pull(TransferOperation wrapper2, Services services) throws AccountException, SibsException, OperationException {
		String state;
		try {
			state = currentState.process(this, wrapper2, services);
			return state;
		} catch(AccountException e) {
			throw new OperationException();
		}
	}

	public String retry() throws AccountException, SibsException {
		String state;
		if(0 < tries) {
			state = "retry";
			tries--;
		} else {
			state = "error";
		}
		return state;
	}
}

class Registered implements State {
	@Override
	public String process(setState wrapper, TransferOperation wrapper2, Services services) {
		wrapper.set_state(new Deposited());
		return "registered";
	}
}

class Deposited implements State {
	@Override
	public String process(setState wrapper, TransferOperation wrapper2, Services services) throws AccountException, SibsException, OperationException {
		try {
			if(!services.deposit(wrapper2.getTargetIban(), wrapper2.getValue())) {
				wrapper.set_state(new Withdraw());
			} 
			return "deposited";
		} catch (AccountException e) {
			throw new OperationException();
		}
	}
}

class Withdraw implements State {
	@Override
	public String process(setState wrapper, TransferOperation wrapper2, Services services) throws AccountException, SibsException, OperationException {
		try {
			if(services.checkSameBank(wrapper2.getSourceIban(), wrapper2.getTargetIban())) {
				services.withdraw(wrapper2.getSourceIban(), wrapper2.getValue());
			} else {
				services.withdraw(wrapper2.getSourceIban(), wrapper2.getValue()+wrapper2.commission());
			}
			wrapper.set_state(new Completed());
			return "withdrawn";
		}catch (AccountException e) {
			services.deposit(wrapper2.getTargetIban(), wrapper2.getValue());
			throw new OperationException();
		}
	}
}

class Completed implements State {
	@Override
	public String process(setState wrapper, TransferOperation wrapper2, Services services) {
		wrapper.set_state(new Completed());
		return "completed";
	}
}