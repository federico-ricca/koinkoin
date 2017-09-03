package com.frk.fintech.koinkoin.trade;

import java.util.List;

import org.knowm.xchange.dto.marketdata.Ticker;

import com.frk.fintech.koinkoin.core.Fund;
import com.frk.fintech.koinkoin.core.InsufficientFundsException;
import com.frk.fintech.koinkoin.core.InvalidCurrency;

public class PercentageVariationTradingStrategy implements TradingStrategy {

	@Override
	public boolean trade(Fund fund, Position position, List<Ticker> tickers)
			throws InvalidCurrency, InsufficientFundsException {
		ProfitStrategy profitStrategy = position
				.calculateProfitStrategy(tickers);

		boolean closePosition = false;

		if (position.hasPercentageProfit(tickers)) {
			System.out.println("Closing position.");
			position.close(profitStrategy);
			fund.deposit(position.getAmount(), position.getCurrency());
			fund.deposit(profitStrategy.getExpectedProfit(),
					position.getCurrency());
			closePosition = true;
			System.out.println("Actual funds: " + fund.getAmount());
		} else if (position.lossAbove(tickers)) {
			System.out.println("Loss above " + position.getMaxLoss()
					+ " %, closing at " + position.currentValue(tickers));
			position.close(profitStrategy);
			fund.deposit(profitStrategy.getExpectedProfit(),
					position.getCurrency());
			closePosition = true;
			System.out.println("Actual funds: " + fund.getAmount());
		} else {
			// hold position
		}

		return closePosition;
	}

}
