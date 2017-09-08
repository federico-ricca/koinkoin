package org.koinkoin.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.koinkoin.core.PriceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class TickerSource implements Runnable {

	private MarketPort marketPort;
	private Map<String, Set<CurrencyPair>> currencyPairs;
	private Map<String, List<Ticker>> tickersPerExchange;
	private AtomicBoolean readAllowed;

	@Autowired
	public TickerSource(MarketPort marketPort) {
		this.marketPort = marketPort;
		this.currencyPairs = new HashMap<String, Set<CurrencyPair>>();
		this.tickersPerExchange = new HashMap<String, List<Ticker>>();

		this.addCurrencyPair("kraken", "BTC", "EUR");

		readAllowed = new AtomicBoolean(false);
	}

	public void addCurrencyPair(String exchangeId, String base, String counter) {
		Set<CurrencyPair> set = currencyPairs.get(exchangeId);

		if (set == null) {
			set = new HashSet<>();
			currencyPairs.put(exchangeId, set);
		}

		CurrencyPair pair = new CurrencyPair(base, counter);

		if (!set.contains(pair)) {
			set.add(pair);
		}

		List<Ticker> tickers = tickersPerExchange.get(exchangeId);

		if (tickers == null) {
			tickersPerExchange.put(exchangeId, new ArrayList<>());
		}
	}

	public PriceData getPrice(String exchangeId, String base, String counter) {
		while (!readAllowed.get())
			;

		List<Ticker> tickers = tickersPerExchange.get(exchangeId);

		if (tickers == null) {
			return null;
		}

		Ticker ticker = matchTicker(base, counter, tickers);
		System.out.println(ticker);

		PriceData priceData = new PriceData(ticker.getAsk(), ticker.getBid());
		priceData.setHigh(ticker.getHigh());
		priceData.setLow(ticker.getLow());

		return priceData;
	}

	private Ticker matchTicker(String base, String counter, List<Ticker> tickers) {
		CurrencyPair pair = new CurrencyPair(base, counter);

		for (Ticker t : tickers) {
			if (t.getCurrencyPair().equals(pair)) {
				return t;
			}
		}

		System.out.println("Warning: no ticker found for " + pair);
		System.out.println("All tickers:");
		tickers.forEach((t) -> {
			System.out.println(t);
		});
		return null;
	}

	@Override
	public void run() {
		while (true) {
			List<ServiceDescriptor> services = marketPort.getServices();

			for (ServiceDescriptor desc : services) {
				Set<CurrencyPair> tradingCurrencyPairs = currencyPairs.get(desc.getExchangeId());

				MarketDataService marketDataService = desc.getMarketDataService();

				List<Ticker> newPrices = new ArrayList<Ticker>();

				List<Ticker> prices = tickersPerExchange.get(desc.getExchangeId());

				if (prices != null) {
					try {
						for (CurrencyPair p : tradingCurrencyPairs) {
							Ticker ticker = marketDataService.getTicker(p);
							newPrices.add(ticker);
						}

						readAllowed.set(false);

						tickersPerExchange.put(desc.getExchangeId(), newPrices);

						readAllowed.set(true);
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
