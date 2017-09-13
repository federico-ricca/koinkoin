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
package org.koinkoin.trade;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.core.BuyingPrice;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.core.SellingPrice;

public class Position {

	private Currency sourceCurrency;
	private Currency targetCurrency;
	private BuyingPrice buyingPrice;
	private BigDecimal sourceAmount;
	private BigDecimal targetAmount;
	private BigDecimal minProfit;
	private BigDecimal maxLoss;
	private CurrencyPair pair;
	private boolean open = false;
	private boolean sustained = false;

	public Position(BigDecimal amount, Currency currency, CurrencyPair pair) {
		this.sourceCurrency = currency;
		this.targetCurrency = currency.equals(pair.base) ? pair.counter : pair.base;
		this.sourceAmount = amount;
		this.pair = pair;
	}

	public void open(List<Ticker> tickers) throws InvalidCurrency {
		open = true;

		// open position on exchage (buy)
		System.out.println("Opening position on " + sourceCurrency + " " + sourceAmount + " amount.");

		Ticker openingTicker = matchTicker(pair, tickers);

		buyingPrice = new BuyingPrice(sourceCurrency, openingTicker);

		targetAmount = buyingPrice.getPrice().multiply(sourceAmount, MathContext.DECIMAL64);

		System.out.println("Buying " + targetAmount + " " + targetCurrency + " at " + openingTicker.getAsk() + " "
				+ sourceCurrency);
	}

	private Ticker matchTicker(CurrencyPair pair, List<Ticker> tickers) {
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

	private List<Ticker> matchTickers(Currency includeCurrency, Currency excludeCurrency, List<Ticker> tickers) {
		List<Ticker> matching = new ArrayList<>();

		for (Ticker t : tickers) {
			if (t.getCurrencyPair().contains(includeCurrency) && !t.getCurrencyPair().contains(excludeCurrency)) {
				matching.add(t);
			}
		}

		return matching;
	}

	public ProfitStrategy calculateProfitStrategy(List<Ticker> tickers) throws InvalidCurrency {
		BigDecimal stopLoss = sourceAmount.subtract(sourceAmount.multiply(maxLoss, MathContext.DECIMAL64),
				MathContext.DECIMAL64);

		BigDecimal varGain = variation(tickers);

		BigDecimal desiredProfit = sourceAmount.multiply(this.minProfit, MathContext.DECIMAL64);

		ProfitStrategy profitStrategy = new ProfitStrategy(stopLoss);

		// back-to-original-currency profit calculation
		if (varGain.compareTo(desiredProfit) >= 1) {
			profitStrategy.addStep(targetCurrency);
			profitStrategy.setExpectedProfit(varGain);
		} else {
			// check if there is a loss
			BigDecimal expectedLoss = calculateLoss(tickers, varGain);

			if (expectedLoss.compareTo(stopLoss) >= 1) {
				profitStrategy.addStep(targetCurrency);
				profitStrategy.setExpectedProfit(expectedLoss);
			} else {
				List<Ticker> txCurrencies = matchTickers(targetCurrency, sourceCurrency, tickers);

				for (Ticker t : txCurrencies) {
					BuyingPrice intermediateBuyingPrice = new BuyingPrice(targetCurrency, t);
					BigDecimal intermediatePrice = intermediateBuyingPrice.getPrice().multiply(targetAmount,
							MathContext.DECIMAL64);
					Ticker closingTicker = matchTicker(
							new CurrencyPair(intermediateBuyingPrice.getTargetCurrency(), sourceCurrency), tickers);
					SellingPrice closingPrice = new SellingPrice(sourceCurrency, closingTicker);
					BigDecimal endPrice = closingPrice.getPrice().multiply(intermediatePrice, MathContext.DECIMAL64);

					BigDecimal crossGain = endPrice.subtract(sourceAmount, MathContext.DECIMAL64);

					if (crossGain.compareTo(desiredProfit) >= 1) {
						profitStrategy.addStep(intermediateBuyingPrice.getTargetCurrency());
						profitStrategy.addStep(targetCurrency);
						profitStrategy.setExpectedProfit(crossGain);
						break;
					}

				}

			}
		}

		return profitStrategy;
	}

	public boolean hasPercentageProfit(List<Ticker> tickers) throws InvalidCurrency {
		BigDecimal varGain = variation(tickers);

		BigDecimal desiredProfit = sourceAmount.multiply(minProfit, MathContext.DECIMAL64);

		// back-to-original-currency profit calculation
		if (varGain.compareTo(desiredProfit) >= 1) {
			return true;
		}

		List<Ticker> txCurrencies = matchTickers(targetCurrency, sourceCurrency, tickers);

		for (Ticker t : txCurrencies) {
			BuyingPrice intermediateBuyingPrice = new BuyingPrice(targetCurrency, t);
			BigDecimal intermediatePrice = intermediateBuyingPrice.getPrice().multiply(targetAmount,
					MathContext.DECIMAL64);
			Ticker closingTicker = matchTicker(
					new CurrencyPair(intermediateBuyingPrice.getTargetCurrency(), sourceCurrency), tickers);
			SellingPrice closingPrice = new SellingPrice(sourceCurrency, closingTicker);
			BigDecimal endPrice = closingPrice.getPrice().multiply(intermediatePrice, MathContext.DECIMAL64);

			BigDecimal crossGain = endPrice.subtract(sourceAmount, MathContext.DECIMAL64);
			/*
			 * System.out.println(">> gain (through " +
			 * intermediateBuyingPrice.getTargetCurrency() + ") is " + crossGain + " " +
			 * sourceCurrency);
			 */
			if (crossGain.compareTo(desiredProfit) >= 1) {
				return true;
			}

		}

		return false;
	}

	public BigDecimal calculateLoss(List<Ticker> tickers, BigDecimal diff) throws InvalidCurrency {
		BigDecimal varLoss = diff.negate();
		BigDecimal estimatedLoss = sourceAmount.subtract(varLoss, MathContext.DECIMAL64);

		return estimatedLoss;
	}

	public boolean lossAbove(List<Ticker> tickers) throws InvalidCurrency {
		BigDecimal loss = sourceAmount.multiply(maxLoss, MathContext.DECIMAL64);
		BigDecimal toleratedLoss = sourceAmount.subtract(loss, MathContext.DECIMAL64);

		BigDecimal varLoss = variation(tickers).negate();
		BigDecimal estimatedLoss = sourceAmount.subtract(varLoss, MathContext.DECIMAL64);

		if (toleratedLoss.compareTo(estimatedLoss) >= 1) {
			return true;
		}

		return false;
	}

	public void close(ProfitStrategy profitStrategy) {
		// close position on exchange (sell)
		open = false;
		// update funds from Exchange
	}

	public BigDecimal variation(List<Ticker> tickers) throws InvalidCurrency {
		BigDecimal currentAmount = currentValue(tickers);

		return currentAmount.subtract(sourceAmount, MathContext.DECIMAL64);
	}

	public BigDecimal currentValue(List<Ticker> tickers) throws InvalidCurrency {
		Ticker closingTicker = matchTicker(pair, tickers);
		return new SellingPrice(sourceCurrency, closingTicker).getPrice().multiply(targetAmount, MathContext.DECIMAL64);
	}

	public Currency getCurrency() {
		return sourceCurrency;
	}

	public boolean isOpen() {
		return open;
	}

	public BigDecimal getAmount() {
		return sourceAmount;
	}

	public BigDecimal getMinProfit() {
		return minProfit;
	}

	public void setMinProfit(BigDecimal minProfit) {
		this.minProfit = minProfit;
	}

	public BigDecimal getMaxLoss() {
		return maxLoss;
	}

	public void setMaxLoss(BigDecimal maxLoss) {
		this.maxLoss = maxLoss;
	}

	public static PositionBuilder builder() {
		return new PositionBuilder();
	}

	public void setSustained(boolean sustained) {
		this.sustained = sustained;
	}

	public boolean isSustained() {
		return sustained;
	}

}
