package com.frk.fintech.koinkoin;

import java.math.BigDecimal;
import java.math.MathContext;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

public class SellingPrice {

	private BigDecimal price;

	public SellingPrice(Currency currency, Ticker ticker)
			throws InvalidCurrency {
		CurrencyPair currencyPair = ticker.getCurrencyPair();

		if (currency.equals(currencyPair.counter)) {
			price = ticker.getBid();
		} else if (currency.equals(currencyPair.base)) {
			price = new BigDecimal(1.0f).divide(ticker.getBid(),
					MathContext.DECIMAL64);
		} else {
			throw new InvalidCurrency(currency);
		}
	}

	public BigDecimal getPrice() {
		return price;
	}
}
