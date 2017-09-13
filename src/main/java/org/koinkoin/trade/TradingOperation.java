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

import java.util.List;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.core.Fund;
import org.koinkoin.core.InsufficientFundsException;
import org.koinkoin.core.InvalidCurrency;
import org.koinkoin.core.ProfitBalance;
import org.koinkoin.trade.strategy.TradingStrategy;

public class TradingOperation {
	private TradingStrategy tradingStrategy;

	public TradingOperation(TradingStrategy tradingStrategy) {
		this.tradingStrategy = tradingStrategy;
	}

	public boolean trade(Fund fund, Position position, List<Ticker> tickers)
			throws InvalidCurrency, InsufficientFundsException {
		ProfitBalance profitBalance = tradingStrategy.execute(position, tickers);

		boolean closePosition = false;

		if (profitBalance.hasProfits()) {
			System.out.println("Closing position.");
			position.close(profitBalance);
			fund.deposit(position.getSourceAmount(), position.getSourceCurrency());
			fund.deposit(profitBalance.getExpectedProfit(), position.getSourceCurrency());
			closePosition = true;
			System.out.println("Actual funds: " + fund.getAmount());
		} else if (profitBalance.reachedStopLoss()) {
			System.out
					.println("Loss above " + position.getPercentageStopLoss() + " %, closing at " + position.currentValue(tickers)
							+ "; loss=" + profitBalance.getExpectedProfit() + " " + position.getSourceCurrency());
			position.close(profitBalance);
			fund.deposit(profitBalance.getExpectedProfit(), position.getSourceCurrency());
			closePosition = true;
			System.out.println("Actual funds: " + fund.getAmount());
		} else {
			// hold position
		}

		return closePosition;
	}

}
