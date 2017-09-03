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
	private BigDecimal minProfit = BigDecimal.ZERO;
	private BigDecimal maxLoss = BigDecimal.ZERO;
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

	public PositionBuilder withMinProfit(float minProfit) {
		this.minProfit = new BigDecimal(minProfit);
		return this;
	}

	public PositionBuilder withMaxLoss(float maxLoss) {
		this.maxLoss = new BigDecimal(maxLoss);
		return this;
	}

	public PositionBuilder sustained(boolean bSustained) {
		this.bSustained = bSustained;
		return this;
	}

	public Position create() {
		Position p = new Position(amount, currency, pair);
		p.setMinProfit(minProfit);
		p.setMaxLoss(maxLoss);
		p.setSustained(bSustained);

		return p;
	}

}
