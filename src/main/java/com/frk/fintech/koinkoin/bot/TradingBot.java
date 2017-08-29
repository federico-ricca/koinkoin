package com.frk.fintech.koinkoin.bot;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

import com.frk.fintech.koinkoin.core.Fund;
import com.frk.fintech.koinkoin.core.InsufficientFundsException;
import com.frk.fintech.koinkoin.core.InvalidCurrency;
import com.frk.fintech.koinkoin.trade.Position;
import com.frk.fintech.koinkoin.trade.ProfitStrategy;

public class TradingBot {
	private Fund fund;
	private List<Position> positions = new ArrayList<>();
	private List<Position> queuedPositions = new ArrayList<>();
	private List<Position> closedPositions = new ArrayList<>();
	private MarketDataService marketDataService;
	private List<CurrencyPair> tradingCurrencyPairs = new ArrayList<CurrencyPair>();

	public TradingBot(Fund fund, MarketDataService marketDataService) {
		this.fund = fund;
		this.marketDataService = marketDataService;
	}

	public void tradePercentage() throws InvalidCurrency,
			InsufficientFundsException {

		List<Ticker> tickers = fetchPrices(marketDataService);

		if (tickers.isEmpty()) {
			return;
		}

		if (!queuedPositions.isEmpty()) {
			for (Position position : queuedPositions) {
				open(position, tickers);
				System.out.println("Actual funds: " + fund.getAmount());
			}

			queuedPositions.clear();
		}

		for (Position position : positions) {
			showPosition(position, tickers);

			ProfitStrategy profitStrategy = position
					.calculateProfitStrategy(tickers);

			if (position.hasPercentageProfit(tickers)) {
				System.out.println("Closing position.");
				position.close(profitStrategy);
				fund.deposit(position.getAmount(), position.getCurrency());
				fund.deposit(profitStrategy.getExpectedProfit(),
						position.getCurrency());
				closedPositions.add(position);
				System.out.println("Actual funds: " + fund.getAmount());
			} else if (position.lossAbove(tickers)) {
				System.out.println("Loss above " + position.getMaxLoss()
						+ " %, closing at " + position.currentValue(tickers));
				position.close(profitStrategy);
				fund.deposit(profitStrategy.getExpectedProfit(),
						position.getCurrency());
				closedPositions.add(position);
				System.out.println("Actual funds: " + fund.getAmount());
			} else {
				// hold position
			}
		}

		List<Position> openPositions = new ArrayList<>();
		for (Position p : positions) {
			if (p.isOpen()) {
				openPositions.add(p);
			}
		}

		positions = openPositions;

		// purge close positions
		for (Position p : closedPositions) {
			if (p.isSustained()) {
				this.queue(p);
			}
		}

		closedPositions.clear();
	}

	private List<Ticker> fetchPrices(MarketDataService marketDataService) {
		List<Ticker> prices = new ArrayList<>();

		try {
			for (CurrencyPair p : tradingCurrencyPairs) {
				prices.add(marketDataService.getTicker(p));
			}
		} catch (Exception e) {
			System.out.println("ERROR - " + e.getMessage());
			prices = new ArrayList<>();
		}

		return prices;
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

		fund.withdraw(position.getAmount(), position.getCurrency());
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

	public void queue(Position position) {
		queuedPositions.add(position);
	}

	public void tradeWith(CurrencyPair... pairs) {
		tradingCurrencyPairs = new ArrayList<CurrencyPair>();

		for (CurrencyPair p : pairs) {
			tradingCurrencyPairs.add(p);
		}

	}
}
