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
package org.koinkoin.integration.kraken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.kraken.KrakenAdapters;
import org.knowm.xchange.kraken.dto.marketdata.KrakenTicker;
import org.knowm.xchange.kraken.dto.marketdata.results.KrakenTickerResult;
import org.koinkoin.integration.TickerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class KrakenTickerFactory implements TickerFactory {
	private static final String GET_TICKER_INFORMATION = "https://api.kraken.com/0/public/Ticker?pair={pairs}";

	@Override
	public List<Ticker> getTickers(Set<CurrencyPair> currencyPairs) {
		RestTemplate restTemplate = new RestTemplate();

		StringBuilder builder = new StringBuilder();

		for (CurrencyPair p : currencyPairs) {
			builder.append(p.base);
			builder.append(p.counter);
			builder.append(",");
		}

		String pairs = builder.toString();
		pairs = pairs.substring(0, pairs.length() - 1);

		ResponseEntity<KrakenTickerResult> response;

		try {
			response = restTemplate.getForEntity(GET_TICKER_INFORMATION, KrakenTickerResult.class, pairs);
		} catch (RuntimeException e) {
			return Collections.emptyList();
		}

		List<Ticker> tickers = new ArrayList<>();

		KrakenTickerResult tickerResult = response.getBody();

		for (Entry<String, KrakenTicker> entry : tickerResult.getResult().entrySet()) {
			String pairName = entry.getKey();
			String base;
			String counter;

			if (pairName.startsWith("DASH")) {
				base = "DASH";
				counter = pairName.substring(base.length());
			} else {
				base = pairName.substring(1, 4);
				counter = pairName.substring(5);
			}

			CurrencyPair pair = new CurrencyPair(base, counter);
			tickers.add(KrakenAdapters.adaptTicker(entry.getValue(), pair));
		}

		return tickers;
	}

}
