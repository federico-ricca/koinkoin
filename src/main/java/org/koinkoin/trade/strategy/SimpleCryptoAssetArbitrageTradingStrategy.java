package org.koinkoin.trade.strategy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.core.BuyingPrice;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.core.ProfitBalance;
import org.koinkoin.core.SellingPrice;
import org.koinkoin.trade.Position;

public class SimpleCryptoAssetArbitrageTradingStrategy implements TradingStrategy {

	@Override
	public ProfitBalance execute(Position position, List<Ticker> tickers) throws InvalidCurrency {
		BigDecimal stopLoss = position.getStopLoss();

		BigDecimal varGain = position.variation(tickers);

		BigDecimal minProfit = position.getMinProfit();

		ProfitBalance profitBalance = new ProfitBalance(stopLoss);

		// back-to-original-currency profit calculation
		if (varGain.compareTo(minProfit) >= 1) {
			profitBalance.addStep(position.getTargetCurrency());
			profitBalance.setExpectedProfit(varGain);
		} else {
			// check if there is a loss
			BigDecimal expectedLoss = position.calculateLoss(tickers, varGain);

			if (expectedLoss.compareTo(stopLoss) >= 1) {
				profitBalance.addStep(position.getTargetCurrency());
				profitBalance.setExpectedProfit(expectedLoss);
			} else {
				List<Ticker> txCurrencies = position.matchTickers(position.getTargetCurrency(),
						position.getSourceCurrency(), tickers);

				for (Ticker t : txCurrencies) {
					BuyingPrice intermediateBuyingPrice = new BuyingPrice(position.getTargetCurrency(), t);
					BigDecimal intermediatePrice = intermediateBuyingPrice.getPrice()
							.multiply(position.getTargetAmount(), MathContext.DECIMAL64);
					Ticker closingTicker = position.matchTicker(
							new CurrencyPair(intermediateBuyingPrice.getTargetCurrency(), position.getSourceCurrency()),
							tickers);
					SellingPrice closingPrice = new SellingPrice(position.getSourceCurrency(), closingTicker);
					BigDecimal endPrice = closingPrice.getPrice().multiply(intermediatePrice, MathContext.DECIMAL64);

					BigDecimal crossGain = endPrice.subtract(position.getSourceAmount(), MathContext.DECIMAL64);

					if (crossGain.compareTo(minProfit) >= 1) {
						profitBalance.addStep(intermediateBuyingPrice.getTargetCurrency());
						profitBalance.addStep(position.getTargetCurrency());
						profitBalance.setExpectedProfit(crossGain);
						break;
					}

				}

			}
		}

		return profitBalance;
	}

}
