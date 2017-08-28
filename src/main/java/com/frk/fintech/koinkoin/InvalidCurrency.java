package com.frk.fintech.koinkoin;

import org.knowm.xchange.currency.Currency;

public class InvalidCurrency extends Exception {

	public InvalidCurrency(Currency currency) {
		super("Invalid currency " + currency.toString());
	}
}
