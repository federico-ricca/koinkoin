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
package org.koinkoin.core;

import java.math.BigDecimal;
import java.math.MathContext;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

public class BuyingPrice {

	public static final BigDecimal TX_FEE = new BigDecimal(0.36f);

	private BigDecimal price;
	private Currency targetCurrency;

	public BuyingPrice(Currency currency, Ticker ticker) throws InvalidCurrency {
		CurrencyPair currencyPair = ticker.getCurrencyPair();

		BigDecimal askPrice = ticker.getAsk();
	
		askPrice.subtract(askPrice.multiply(TX_FEE, MathContext.DECIMAL64));
		
		if (currency.equals(currencyPair.base)) {
			price = askPrice;
			targetCurrency = currencyPair.counter;
		} else if (currency.equals(currencyPair.counter)) {
			price = new BigDecimal(1.0f).divide(askPrice,
					MathContext.DECIMAL64);
			targetCurrency = currencyPair.base;
		} else {
			throw new InvalidCurrency(currency);
		}
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Currency getTargetCurrency() {
		return targetCurrency;
	}
}
