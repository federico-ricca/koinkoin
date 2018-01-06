package org.koinkoin.trade.strategy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.core.ProfitBalance;
import org.koinkoin.trade.Position;

public class SimpleCryptoAssetArbitrageTradingStrategyTests {

	@Test
	public void testPositionUnchanged() throws InvalidCurrency {
		SimpleCryptoAssetArbitrageTradingStrategy strategy = new SimpleCryptoAssetArbitrageTradingStrategy();

		CurrencyPair pair = CurrencyPair.BTC_EUR;
		BigDecimal bidBefore = new BigDecimal(3200);
		BigDecimal bidAfter = new BigDecimal(3200);
		BigDecimal askBefore = new BigDecimal(3200);
		BigDecimal askAfter = new BigDecimal(3200);
		BigDecimal expectedConversion = new BigDecimal();

		List<Ticker> tickersBefore = Arrays
				.asList(new Ticker.Builder().bid(bidBefore).ask(askBefore).currencyPair(pair).build());
		List<Ticker> tickersAfter = Arrays
				.asList(new Ticker.Builder().bid(bidAfter).ask(askAfter).currencyPair(pair).build());

		Position position = Position.builder().withAmount(100f).withCurrency(Currency.EUR)
				.withPair(new CurrencyPair("XBT", "EUR")).withPercentageMinProfit(0.5f / 100.0f)
				.withPercentageStopLoss(0.3f / 100.0f).sustained(true).create();

		position.open(tickersBefore);

		ProfitBalance balance = strategy.execute(position, tickersAfter);

		Assert.assertFalse(balance.hasProfits());
		Assert.assertFalse(balance.reachedStopLoss());
		Assert.AssertTrue(expectedConversion.equals(balance.exitValue()));
	}
}
