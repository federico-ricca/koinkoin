package com.frk.fintech.koinkoin;

import java.math.BigDecimal;

import org.knowm.xchange.currency.Currency;

public class Fund {
	private BigDecimal amount;
	private Currency currency;
	private Object lock;

	public Fund(BigDecimal amount, Currency currency) {
		this.amount = amount;
		this.currency = currency;
		this.lock = new Object();
	}

	public Currency getCurrency() {
		return currency;
	}

	public void withdraw(BigDecimal withdrawalAmount)
			throws InsufficientFundsException {
		synchronized (lock) {
			if (amount.compareTo(withdrawalAmount) >= 0) {
				amount.subtract(withdrawalAmount);
				
				return;
			}
		}
		
		throw new InsufficientFundsException("Cannot withdraw " + withdrawalAmount + "; Actual funds: " + amount);
	}

}
