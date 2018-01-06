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
import org.knowm.xchange.currency.CurrencyPair;

public class PositionBuilder {
	private Currency currency;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal percentageMinProfit = BigDecimal.ZERO;
	private BigDecimal percentageStopLoss = BigDecimal.ZERO;
	private CurrencyPair pair;
	private boolean bSustained = false;

	public PositionBuilder withAmount(float amount) {
		this.amount = new BigDecimal(amount);
		return this;
	}

	public PositionBuilder withCurrency(Currency currency) {
		this.currency = currency;
		return this;
	}

	public PositionBuilder withPair(CurrencyPair currencyPair) {
		this.pair = currencyPair;
		return this;
	}

	public PositionBuilder withPercentageMinProfit(float percentageMinProfit) {
		this.percentageMinProfit = new BigDecimal(percentageMinProfit);
		return this;
	}

	public PositionBuilder withPercentageStopLoss(float percentageStopLoss) {
		this.percentageStopLoss = new BigDecimal(percentageStopLoss);
		return this;
	}

	public PositionBuilder sustained(boolean bSustained) {
		this.bSustained = bSustained;
		return this;
	}

	public Position create() {
		Position p = new Position(amount, currency, pair, percentageStopLoss);
		p.setPercentageMinProfit(percentageMinProfit);
		p.setSustained(bSustained);

		return p;
	}

}
