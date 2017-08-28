package com.frk.fintech.koinkoin;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.dto.marketdata.Ticker;

public class TradingBot {
	private Fund fund;
	private List<Position> positions = new ArrayList<>();

	public TradingBot(Fund fund) {
		this.fund = fund;
	}

	public void tradePercentage(float minProfit, float maxLoss, List<Ticker> tickers)
			throws InvalidCurrency {
		for (Position position : positions) {
			showPosition(position, tickers);

			ProfitStrategy profitStrategy = position.calculateProfitStrategy(minProfit, maxLoss, tickers);
			
			if (position.hasPercentageProfit(minProfit, tickers)) {
				System.out.println("Closing position.");
				position.close();
			} else if (position.lossAbove(maxLoss, tickers)) {
				System.out.println("Loss above " + maxLoss + " %, closing at "
						+ position.currentValue(tickers));
				position.close();
			} else {
				// hold position
			}
		}
	}

	private void showPosition(Position position, List<Ticker> tickers)
			throws InvalidCurrency {
		System.out.println("Current position: "
				+ position.currentValue(tickers) + " " + position.getCurrency()
				+ ", variation=" + position.variation(tickers) + " "
				+ position.getCurrency());
	}

	public void open(Position position, List<Ticker> tickers)
			throws InvalidCurrency, InsufficientFundsException {
		if (!fund.getCurrency().equals(position.getCurrency())) {
			throw new InvalidCurrency(position.getCurrency());
		}
		
		fund.withdraw(position.getAmount());
		positions.add(position);

		position.open(tickers);
	}

	public boolean hasOpenPositions() {
		for (Position p : positions) {
			if (p.isOpen()) {
				return true;
			}
		}

		return false;
	}

}
