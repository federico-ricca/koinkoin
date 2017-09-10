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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.integration.TickerSource;

public class BackTestingTickerSource implements TickerSource {
	private BufferedReader reader;
	private String line;
	private String base;
	private String counter;

	public BackTestingTickerSource(String filename, String base, String counter) throws IOException {
		this.reader = new BufferedReader(new FileReader(new File(filename)));
		this.base = base;
		this.counter = counter;
		line = "";
	}

	@Override
	public boolean hasData() {
		try {
			line = reader.readLine();
		} catch (IOException e) {
			line = null;
		}

		return line != null;
	}

	@Override
	public List<Ticker> tickers() throws Exception {
		if (line == null || line.trim().isEmpty()) {
			return Collections.emptyList();
		}

		List<Ticker> tickers = new ArrayList<>();

		String[] tokens = line.split(",");

		BigDecimal bid = new BigDecimal(Double.parseDouble(tokens[1]));
		BigDecimal ask = new BigDecimal(Double.parseDouble(tokens[2]));
		CurrencyPair pair = new CurrencyPair(base, counter);
		
		Ticker ticker = new Ticker.Builder().bid(bid).ask(ask).currencyPair(pair).build();

		tickers.add(ticker);

		return tickers;
	}

}
