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

import java.util.List;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.core.Fund;
import org.koinkoin.core.InsufficientFundsException;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.trade.Position;
import org.koinkoin.trade.ProfitStrategy;
import org.koinkoin.trade.TradingStrategy;

public class PercentageVariationTradingStrategy implements TradingStrategy {

	@Override
	public boolean trade(Fund fund, Position position, List<Ticker> tickers)
			throws InvalidCurrency, InsufficientFundsException {
		ProfitStrategy profitStrategy = position.calculateProfitStrategy(tickers);

		boolean closePosition = false;

		if (position.hasPercentageProfit(tickers)) {
			System.out.println("Closing position.");
			position.close(profitStrategy);
			fund.deposit(position.getAmount(), position.getCurrency());
			fund.deposit(profitStrategy.getExpectedProfit(), position.getCurrency());
			closePosition = true;
			System.out.println("Actual funds: " + fund.getAmount());
		} else if (position.lossAbove(tickers)) {
			System.out.println(
					"Loss above " + position.getMaxLoss() + " %, closing at " + position.currentValue(tickers));
			position.close(profitStrategy);
			fund.deposit(profitStrategy.getExpectedProfit(), position.getCurrency());
			closePosition = true;
			System.out.println("Actual funds: " + fund.getAmount());
		} else {
			// hold position
		}

		return closePosition;
	}

}
