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

import org.knowm.xchange.currency.Currency;

public class Fund {
	private BigDecimal amount;
	private Currency currency;
	private Object lock;

	public Fund(BigDecimal amount, Currency currency) {
		this.amount = amount;
		this.currency = currency;
		this.lock = new Object();
	}

	public Currency getCurrency() {
		return currency;
	}

	public void withdraw(BigDecimal withdrawalAmount, Currency currency)
			throws InsufficientFundsException, InvalidCurrency {
		if (!this.getCurrency().equals(currency)) {
			throw new InvalidCurrency(currency);
		}

		synchronized (lock) {
			if (amount.compareTo(withdrawalAmount) >= 0) {
				amount = amount.subtract(withdrawalAmount);

				return;
			}
		}

		throw new InsufficientFundsException("Cannot withdraw "
				+ withdrawalAmount + "; Actual funds: " + amount);
	}

	public void deposit(BigDecimal expectedProfit, Currency currency)
			throws InvalidCurrency, InsufficientFundsException {
		if (!this.getCurrency().equals(currency)) {
			throw new InvalidCurrency(currency);
		}

		synchronized (lock) {
			if (expectedProfit.compareTo(BigDecimal.ZERO) >= 0) {
				amount = amount.add(expectedProfit);
				return;
			}
		}

		this.withdraw(expectedProfit, currency);
	}

	public BigDecimal getAmount() {
		synchronized (lock) {
			return amount;
		}
	}
}
