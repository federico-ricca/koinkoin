package org.koinkoin.integration;

import org.knowm.xchange.service.marketdata.MarketDataService;

public class ServiceDescriptor {

	private String exchangeId;
	private MarketDataService marketDataService;

	public ServiceDescriptor(String serviceId, MarketDataService marketDataService) {
		setExchangeId(serviceId);
		setMarketDataService(marketDataService);
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

}
