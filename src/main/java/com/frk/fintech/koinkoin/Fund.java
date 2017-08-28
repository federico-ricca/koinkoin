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

	public void withdraw(BigDecimal withdrawalAmount, Currency currency)
			throws InsufficientFundsException, InvalidCurrency {
		if (!this.getCurrency().equals(currency)) {
			throw new InvalidCurrency(currency);
		}

		synchronized (lock) {
			if (amount.compareTo(withdrawalAmount) >= 0) {
				amount = amount.subtract(withdrawalAmount);

				return;
			}
		}

		throw new InsufficientFundsException("Cannot withdraw "
				+ withdrawalAmount + "; Actual funds: " + amount);
	}

	public void deposit(BigDecimal expectedProfit, Currency currency)
			throws InvalidCurrency, InsufficientFundsException {
		if (!this.getCurrency().equals(currency)) {
			throw new InvalidCurrency(currency);
		}

		synchronized (lock) {
			if (expectedProfit.compareTo(BigDecimal.ZERO) >= 0) {
				amount = amount.add(expectedProfit);
				return;
			}
		}

		this.withdraw(expectedProfit, currency);
	}

	public BigDecimal getAmount() {
		synchronized (lock) {
			return amount;
		}
	}
}
