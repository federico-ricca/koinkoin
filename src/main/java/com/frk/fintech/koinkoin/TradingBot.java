package com.frk.fintech.koinkoin;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class TradingBot {
	private Fund fund;
	private List<Position> positions = new ArrayList<>();
	private List<Position> queuedPositions = new ArrayList<>();
	private MarketDataService marketDataService;

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
				fund.deposit(profitStrategy.getExpectedProfit(),
						position.getCurrency());
				System.out.println("Actual funds: " + fund.getAmount());
			} else if (position.lossAbove(tickers)) {
				System.out.println("Loss above " + position.getMaxLoss()
						+ " %, closing at " + position.currentValue(tickers));
				position.close(profitStrategy);
				fund.deposit(profitStrategy.getExpectedProfit(),
						position.getCurrency());
				System.out.println("Actual funds: " + fund.getAmount());
			} else {
				// hold position
			}
		}
	}

	private List<Ticker> fetchPrices(MarketDataService marketDataService) {
		List<Ticker> prices = new ArrayList<>();

		try {
			prices.add(marketDataService.getTicker(CurrencyPair.BTC_USD));
			prices.add(marketDataService.getTicker(CurrencyPair.XRP_BTC));
			prices.add(marketDataService.getTicker(CurrencyPair.XRP_USD));
			prices.add(marketDataService.getTicker(new CurrencyPair(
					Currency.XMR, Currency.USD)));
			prices.add(marketDataService.getTicker(new CurrencyPair("DASH",
					"USD")));
			prices.add(marketDataService.getTicker(new CurrencyPair("DASH",
					"BTC")));
			prices.add(marketDataService.getTicker(CurrencyPair.XMR_BTC));
		} catch (Exception e) {
			System.out.println("ERROR - " + e.getMessage());
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
}
