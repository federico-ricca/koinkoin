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
package org.koinkoin.swarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

public class TickerMap {
	private Map<String, Ticker> allTickers = new HashMap<>();

	public void addAll(List<Ticker> tickers) {
		for (Ticker t : tickers) {
			String pairName = t.getCurrencyPair().toString();

			allTickers.put(pairName, t);
		}
	}

	public Ticker reduce(String base, String counter) {
		String pairName = new CurrencyPair(base, counter).toString();

		return allTickers.get(pairName);
	}
}
