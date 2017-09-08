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
package org.koinkoin.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class TickerSource {

	private MarketPort marketPort;
	private Map<String, Set<CurrencyPair>> currencyPairs;
	private Map<String, List<Ticker>> tickersPerExchange;
	private ExchangeDescriptor descriptor;

	public TickerSource(ExchangeDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public boolean hasData() {
		return descriptor.getTradingCurrencyPairs() != null && !descriptor.getTradingCurrencyPairs().isEmpty();
	}

	public List<Ticker> tickers() throws Exception {
		List<Ticker> prices = new ArrayList<Ticker>();

		MarketDataService marketDataService = descriptor.getMarketDataService();

		for (CurrencyPair p : descriptor.getTradingCurrencyPairs()) {
			Ticker ticker = marketDataService.getTicker(p);
			prices.add(ticker);
		}

		return prices;
	}

}
