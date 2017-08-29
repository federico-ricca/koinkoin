package com.frk.fintech.koinkoin.core;

import java.math.BigDecimal;
import java.math.MathContext;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

public class BuyingPrice {

	public static final BigDecimal TX_FEE = new BigDecimal(0.36f);

	private BigDecimal price;
	private Currency targetCurrency;

	public BuyingPrice(Currency currency, Ticker ticker) throws InvalidCurrency {
		CurrencyPair currencyPair = ticker.getCurrencyPair();

		BigDecimal askPrice = ticker.getAsk();
	
		askPrice.subtract(askPrice.multiply(TX_FEE, MathContext.DECIMAL64));
		
		if (currency.equals(currencyPair.base)) {
			price = askPrice;
			targetCurrency = currencyPair.counter;
		} else if (currency.equals(currencyPair.counter)) {
			price = new BigDecimal(1.0f).divide(askPrice,
					MathContext.DECIMAL64);
			targetCurrency = currencyPair.base;
		} else {
			throw new InvalidCurrency(currency);
		}
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Currency getTargetCurrency() {
		return targetCurrency;
	}
}
