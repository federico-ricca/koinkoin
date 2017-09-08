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

import java.util.HashSet;
import java.util.Set;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class ExchangeDescriptor {

	private String exchangeId;
	private MarketDataService marketDataService;
	private Set<CurrencyPair> tradingCurrencyPairs;

	public ExchangeDescriptor(String serviceId, MarketDataService marketDataService) {
		setExchangeId(serviceId);
		setMarketDataService(marketDataService);
		tradingCurrencyPairs = new HashSet<>();
	}

	public String getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(String exchangeId) {
		this.exchangeId = exchangeId;
	}

	public MarketDataService getMarketDataService() {
		return marketDataService;
	}

	public void setMarketDataService(MarketDataService marketDataService) {
		this.marketDataService = marketDataService;
	}

	public void addCurrencyPair(String base, String counter) {
		CurrencyPair pair = new CurrencyPair(base, counter);
		tradingCurrencyPairs.add(pair);
	}

	public Set<CurrencyPair> getTradingCurrencyPairs() {
		return tradingCurrencyPairs;
	}

}
