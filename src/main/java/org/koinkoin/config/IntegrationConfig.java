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
package org.koinkoin.config;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.koinkoin.integration.ExchangeDescriptor;
import org.koinkoin.integration.MarketPort;
import org.koinkoin.integration.kraken.KrakenTickerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationConfig {

	@Bean
	public MarketPort getMarketPort() {
		// Use the factory to get Bitstamp exchange API using default settings
		Exchange kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());

		// Interested in the public market data feed (no authentication)
		MarketDataService krakenMarketDataService = kraken.getMarketDataService();

		MarketPort marketPort = new MarketPort();

		ExchangeDescriptor exchange = marketPort.addExchange("kraken", krakenMarketDataService);
		exchange.addCurrencyPair("XBT", "EUR");
		exchange.addCurrencyPair("DASH", "XBT");
		exchange.addCurrencyPair("DASH", "EUR");
		exchange.addCurrencyPair("XRP", "EUR");
		exchange.addCurrencyPair("XRP", "XBT");
		exchange.addCurrencyPair("XMR", "XBT");
		exchange.addCurrencyPair("XMR", "EUR");
		exchange.setTickerFactory(new KrakenTickerFactory());
		
		return marketPort;
	}
}
