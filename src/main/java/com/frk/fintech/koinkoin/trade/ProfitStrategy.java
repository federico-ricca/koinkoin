package com.frk.fintech.koinkoin.trade;

import java.math.BigDecimal;

import org.knowm.xchange.currency.Currency;

public class ProfitStrategy {

	private BigDecimal expectedProfit;

	public void addStep(Currency currency) {
	}

	public void setExpectedProfit(BigDecimal profit) {
		expectedProfit = profit;
	}

	public BigDecimal getExpectedProfit() {
		return expectedProfit;
	}

}
