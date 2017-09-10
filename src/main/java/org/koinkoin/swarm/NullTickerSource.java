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

import java.util.List;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.koinkoin.integration.TickerSource;

public class NullTickerSource implements TickerSource {

	@Override
	public boolean hasData() {
		return false;
	}

	@Override
	public List<Ticker> tickers() throws Exception {
		return null;
	}

}
