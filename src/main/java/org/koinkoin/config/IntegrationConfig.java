package org.koinkoin.config;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.koinkoin.integration.MarketPort;
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

		marketPort.addExchange("kraken", krakenMarketDataService);

		
		return marketPort;
	}
}
