package com.frk.fintech.koinkoin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.kraken.dto.marketdata.KrakenTicker;
import org.knowm.xchange.kraken.service.KrakenMarketDataServiceRaw;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class Main {
	/***
	 * Can I trade digital currencies other than Bitcoin?
	 * 
	 * Yes. You can trade Bitcoin(XBT), Ethereum (ETH), Monero (XMR), Dash
	 * (DASH), Litecoin (LTC), Ripple (XRP), Stellar/Lumens (XLM), Ethereum
	 * Classic (ETC), Augur REP tokens (REP), ICONOMI (ICN), Melon (MLN), Zcash
	 * (ZEC), Dogecoin (XDG), Tether (USDT), Gnosis (GNO), and EOS (EOS).
	 * 
	 */
	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	public void run() throws Exception {
		// Use the factory to get Bitstamp exchange API using default settings
		Exchange kraken = ExchangeFactory.INSTANCE
				.createExchange(KrakenExchange.class.getName());
		Exchange poloniex = ExchangeFactory.INSTANCE
				.createExchange(PoloniexExchange.class.getName());

		// Interested in the public market data feed (no authentication)
		MarketDataService krakenMarketDataService = kraken
				.getMarketDataService();
		MarketDataService poloniexMarketDataService = poloniex
				.getMarketDataService();

		Fund fund = new Fund(new BigDecimal(800), Currency.USD);
		
		TradingBot tradingBot = new TradingBot(fund);
		TradingBot poloniexBot = new TradingBot(fund);

		do {
			List<Ticker> krakenTickers = fetchPrices(krakenMarketDataService);
			List<Ticker> poloniexTickers = fetchPrices(poloniexMarketDataService);

			if (!krakenTickers.isEmpty()) {
				showPrices("Kraken", krakenTickers);

				if (!tradingBot.hasOpenPositions()) {
					tradingBot.open(new Position(100f, Currency.USD,
							new CurrencyPair("DASH", "USD")), krakenTickers);
				}

				float maxLoss = 1.0f;
				float minProfit = 0.1f;

				tradingBot.tradePercentage(minProfit, maxLoss, krakenTickers);
			}

			if (!poloniexTickers.isEmpty()) {
				showPrices("Poloniex", poloniexTickers);

				if (!poloniexBot.hasOpenPositions()) {
					poloniexBot.open(new Position(500f, Currency.USD,
							CurrencyPair.BTC_USD), poloniexTickers);
				}

				float maxLoss = 0.0008f;
				float minProfit = 0.0005f;

				poloniexBot.tradePercentage(minProfit, maxLoss, poloniexTickers);
			}

			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (tradingBot.hasOpenPositions() && poloniexBot.hasOpenPositions());
		// raw((KrakenMarketDataServiceRaw) marketDataService);
	}

	private List<Ticker> fetchPrices(MarketDataService marketDataService) {
		List<Ticker> prices = new ArrayList<>();

		try {
			prices.add(marketDataService.getTicker(CurrencyPair.BTC_USD));
			prices.add(marketDataService.getTicker(CurrencyPair.XRP_BTC));
			prices.add(marketDataService.getTicker(CurrencyPair.XRP_USD));
			prices.add(marketDataService.getTicker(new CurrencyPair(
					Currency.XMR, Currency.USD)));
			prices.add(marketDataService.getTicker(new CurrencyPair("DASH",
					"USD")));
			prices.add(marketDataService.getTicker(new CurrencyPair("DASH",
					"BTC")));
			prices.add(marketDataService.getTicker(CurrencyPair.XMR_BTC));
		} catch (Exception e) {
			System.out.println("ERROR - " + e.getMessage());
		}

		return prices;
	}

	private void showPrices(String exchange, List<Ticker> tickers) {
		StringBuilder builder = new StringBuilder();
		builder.append(exchange);
		builder.append(": ");
		builder.append(GregorianCalendar.getInstance().getTime());
		builder.append(" | ");

		for (Ticker ticker : tickers) {
			builder.append(ticker.getCurrencyPair());
			builder.append(": ");
			builder.append(ticker.getAsk());
			builder.append(", ");
			builder.append(ticker.getBid());

			builder.append(" | ");
		}

		System.out.println(builder.toString());
	}

	private static void raw(KrakenMarketDataServiceRaw marketDataService)
			throws IOException {

		KrakenTicker krakenTicker = marketDataService
				.getKrakenTicker(CurrencyPair.BTC_EUR);

		System.out.println(krakenTicker.toString());
	}
}
