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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.bot.TradingBot;
import org.koinkoin.core.InsufficientFundsException;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.core.PriceData;
import org.koinkoin.integration.ExchangeDescriptor;
import org.koinkoin.integration.MarketPort;
import org.koinkoin.integration.TickerSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class Swarm implements Runnable {
	private Map<String, List<TradingBot>> botsPerExchange = new HashMap<>();
	private SwarmLog swarmLog;
	private MarketPort marketPort;

	@Autowired
	public Swarm(MarketPort marketPort) {
		swarmLog = new SwarmLog();
		this.marketPort = marketPort;
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
		System.out.println(ticker);

		PriceData priceData = new PriceData(ticker.getAsk(), ticker.getBid());
		priceData.setHigh(ticker.getHigh());
		priceData.setLow(ticker.getLow());

		return priceData;
	}

	@Override
	public void run() {
		while (true) {
			List<ExchangeDescriptor> exchanges = marketPort.getExchanges();

			for (ExchangeDescriptor desc : exchanges) {
				TickerSource source = new TickerSource(desc);

				if (source.hasData()) {
					try {
						List<Ticker> tickers = source.tickers();

						trade(desc.getExchangeId(), tickers);

						swarmLog.append(desc.getExchangeId(), tickers);
					} catch (Exception e) {
						System.out.println("ERROR - " + e.getMessage());
					}
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
