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
package org.koinkoin.swarm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.bot.TradingBot;
import org.koinkoin.core.Fund;
import org.koinkoin.core.InsufficientFundsException;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.core.PriceData;
import org.koinkoin.data.TickerSource;
import org.koinkoin.integration.ExchangeDescriptor;
import org.koinkoin.integration.MarketPort;
import org.koinkoin.mode.TradingModeStrategy;
import org.koinkoin.mode.real.RealTradingModeStrategy;
import org.koinkoin.trade.Position;
import org.koinkoin.trade.TradingOperation;
import org.koinkoin.trade.strategy.SimpleCryptoAssetArbitrageTradingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class Swarm implements Runnable {
	private Map<String, List<TradingBot>> botsPerExchange = new HashMap<>();
	private SwarmLog swarmLog;
	private MarketPort marketPort;
	private AtomicBoolean running;
	private AtomicBoolean marketDataLogOpen;
	private TradingModeStrategy tradingModeStrategy;

	@Autowired
	public Swarm(MarketPort marketPort) {
		swarmLog = new SwarmLog();
		this.marketPort = marketPort;
		running = new AtomicBoolean(false);
		marketDataLogOpen = new AtomicBoolean(false);
	}

	public void add(String exchangeId, TradingBot tradingBot) {
		List<TradingBot> bots = botsPerExchange.get(exchangeId);

		if (bots == null) {
			bots = new ArrayList<>();
			botsPerExchange.put(exchangeId, bots);
		}

		bots.add(tradingBot);
	}

	public void trade(String exchangeId, List<Ticker> tickers) {
		List<TradingBot> bots = botsPerExchange.get(exchangeId);

		if (bots != null) {
			for (TradingBot bot : bots) {
				try {
					bot.trade(tickers);
				} catch (InvalidCurrency | InsufficientFundsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public PriceData getPrice(String exchangeId, String base, String counter) {
		Ticker ticker = swarmLog.peek(exchangeId, base, counter);

		PriceData priceData = new PriceData(ticker.getAsk(), ticker.getBid());
		priceData.setHigh(ticker.getHigh());
		priceData.setLow(ticker.getLow());

		return priceData;
	}

	public void stop() {
		running.set(false);
	}

	@Override
	public void run() {
		Collection<ExchangeDescriptor> exchanges = marketPort.getExchanges();

		running.set(true);

		Fund fund = new Fund(new BigDecimal(500), Currency.EUR);

		tradingModeStrategy = new RealTradingModeStrategy();
		// tradingModeStrategy = new BackTestingModeStrategy();

		TradingBot tradingBot = new TradingBot(fund, marketPort.getExchange("kraken"),
				new TradingOperation(new SimpleCryptoAssetArbitrageTradingStrategy()));
		tradingBot.queue(Position.builder().withAmount(100f).withCurrency(Currency.EUR)
				.withPair(new CurrencyPair("XBT", "EUR")).withPercentageMinProfit(0.5f / 100.0f)
				.withPercentageStopLoss(0.3f / 100.0f).sustained(true).create());

		this.add("kraken", tradingBot);

		while (running.get()) {
			for (ExchangeDescriptor desc : exchanges) {
				TickerSource source = tradingModeStrategy.newTickerSource(desc);

				if (source.hasData()) {
					try {
						List<Ticker> tickers = source.tickers();

						trade(desc.getExchangeId(), tickers);

						swarmLog.append(desc.getExchangeId(), tickers);

						// disable on backtesting
						if (marketDataLogOpen.get()) {
							desc.getMarketDataLog().add(tickers);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("ERROR - " + e.getMessage());
					}
				}
			}

			tradingModeStrategy.interval();
		}

		closeMarketDataLog();
	}

	public void openMarketDataLog() {
		marketDataLogOpen.set(true);
	}

	public void closeMarketDataLog() {
		if (marketDataLogOpen.get()) {
			marketDataLogOpen.set(false);

			Collection<ExchangeDescriptor> exchanges = marketPort.getExchanges();

			for (ExchangeDescriptor desc : exchanges) {
				desc.getMarketDataLog().close();
			}
		}
	}

}
