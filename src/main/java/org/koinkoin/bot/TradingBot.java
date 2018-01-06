/*************************************************************************** 
   Copyright 2017 Federico Ricca
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/
package org.koinkoin.bot;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.koinkoin.core.Fund;
import org.koinkoin.core.InsufficientFundsException;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.trade.Position;
import org.koinkoin.trade.TradingOperation;

public class TradingBot {
	private Fund fund;
	private List<Position> positions = new ArrayList<>();
	private List<Position> queuedPositions = new ArrayList<>();
	private List<Position> closedPositions = new ArrayList<>();
	private MarketDataService marketDataService;
	private List<CurrencyPair> tradingCurrencyPairs = new ArrayList<CurrencyPair>();
	private TradingOperation tradingOperation;

	public TradingBot(Fund fund, MarketDataService marketDataService, TradingOperation tradingOperation) {
		this.fund = fund;
		this.marketDataService = marketDataService;
		this.tradingOperation = tradingOperation;
	}

	public void trade(List<Ticker> tickers) throws InvalidCurrency, InsufficientFundsException {
		if (tickers == null || tickers.isEmpty()) {
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
			// showPosition(position, tickers);

			boolean closePosition = tradingOperation.trade(fund, position, tickers);

			if (closePosition) {
				closedPositions.add(position);
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

	private void showPosition(Position position, List<Ticker> tickers) throws InvalidCurrency {
		System.out.println("Current position: " + position.currentValue(tickers) + " " + position.getSourceCurrency()
				+ ", variation=" + position.variation(tickers) + " " + position.getSourceCurrency());
	}

	public void open(Position position, List<Ticker> tickers) throws InvalidCurrency, InsufficientFundsException {

		fund.withdraw(position.getSourceAmount(), position.getSourceCurrency());
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
