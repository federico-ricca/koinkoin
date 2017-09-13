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

import org.knowm.xchange.currency.Currency;

public class ProfitStrategy {

	private BigDecimal expectedProfit;
	private BigDecimal stopLoss;

	public ProfitStrategy(BigDecimal stopLoss) {
		this.expectedProfit = BigDecimal.ZERO;
		this.stopLoss = stopLoss;
	}

	public void addStep(Currency currency) {
	}

	public void setExpectedProfit(BigDecimal profit) {
		if (profit == null) {
			return;
		}

		expectedProfit = profit;
	}

	public BigDecimal getExpectedProfit() {
		return expectedProfit;
	}

	public boolean hasProfits() {
		return (expectedProfit.compareTo(BigDecimal.ZERO) >= 1);
	}

	public boolean reachedStopLoss() {
		return (expectedProfit.compareTo(stopLoss) <= 0);
	}
}
