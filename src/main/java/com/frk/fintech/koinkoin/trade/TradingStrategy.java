package com.frk.fintech.koinkoin.trade;

import java.util.List;

import org.knowm.xchange.dto.marketdata.Ticker;

import com.frk.fintech.koinkoin.core.Fund;
import com.frk.fintech.koinkoin.core.InsufficientFundsException;
import com.frk.fintech.koinkoin.core.InvalidCurrency;

public interface TradingStrategy {

	public boolean trade(Fund fund, Position position, List<Ticker> tickers)
			throws InvalidCurrency, InsufficientFundsException;
}
