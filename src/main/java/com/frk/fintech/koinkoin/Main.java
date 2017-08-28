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
	public static final float MIN_PROFIT = 0.15f / 100.0f;

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

		TradingBot tradingBot = new TradingBot(fund, krakenMarketDataService);
		TradingBot poloniexBot = new TradingBot(fund, poloniexMarketDataService);

		tradingBot.queue(Position.builder().withAmount(500f)
				.withCurrency(Currency.USD)
				.withPair(new CurrencyPair("BTC", "USD"))
				.withMinProfit(MIN_PROFIT).withMaxLoss(0.1f).sustained(true)
				.create());

		poloniexBot.queue(Position.builder().withAmount(500f)
				.withCurrency(Currency.USD)
				.withPair(new CurrencyPair("DASH", "USD"))
				.withMinProfit(MIN_PROFIT).withMaxLoss(0.1f).create());

		Swarm swarm = new Swarm();

		swarm.add(tradingBot);

		do {
			swarm.trade();

			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (true);
		// raw((KrakenMarketDataServiceRaw) marketDataService);
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
