package org.koinkoin.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knowm.xchange.service.marketdata.MarketDataService;

public class MarketPort {
	private List<ServiceDescriptor> services = new ArrayList<ServiceDescriptor>();

	public void addExchange(String exchangeId, MarketDataService marketDataService) {
		services.add(new ServiceDescriptor(exchangeId, marketDataService));
	}

	public List<ServiceDescriptor> getServices() {
		return Collections.unmodifiableList(services);
	}

	public void getMarketData() {
		// TODO Auto-generated method stub

	}

}
