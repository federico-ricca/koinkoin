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
			BigDecimal currentValue = position.currentValue(tickers);

			if (currentValue.compareTo(stopLoss) >= 1) {
				profitBalance.addStep(position.getTargetCurrency());
				profitBalance.setExpectedProfit(varGain);
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
