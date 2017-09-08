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
package org.koinkoin.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knowm.xchange.service.marketdata.MarketDataService;

public class MarketPort {
	private List<ExchangeDescriptor> exchanges = new ArrayList<ExchangeDescriptor>();

	public ExchangeDescriptor addExchange(String exchangeId, MarketDataService marketDataService) {
		ExchangeDescriptor exchangeDescriptor = new ExchangeDescriptor(exchangeId, marketDataService);

		exchanges.add(exchangeDescriptor);

		return exchangeDescriptor;
	}

	public List<ExchangeDescriptor> getExchanges() {
		return Collections.unmodifiableList(exchanges);
	}
}
