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
package org.koinkoin.mode.backtesting;

import java.io.IOException;

import org.koinkoin.data.NullTickerSource;
import org.koinkoin.data.TickerSource;
import org.koinkoin.integration.ExchangeDescriptor;
import org.koinkoin.mode.TradingModeStrategy;

public class BackTestingModeStrategy implements TradingModeStrategy {
	private TickerSource backTestingTickerSource;

	public BackTestingModeStrategy() {
		try {
			backTestingTickerSource = new BackTestingTickerSource("kraken-XBT_EUR-2017-09-10-15-50-56.csv", "BTC",
					"EUR");
		} catch (IOException e) {
			e.printStackTrace();
			backTestingTickerSource = new NullTickerSource();
		}
	}

	@Override
	public void interval() {
/*		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public TickerSource newTickerSource(ExchangeDescriptor desc) {
		return backTestingTickerSource;
	}

}
