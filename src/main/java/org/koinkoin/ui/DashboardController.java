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
package org.koinkoin.ui;

import org.koinkoin.swarm.Swarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class DashboardController {
	private Swarm swarm;

	@Autowired
	public DashboardController(Swarm swarm) {
		this.swarm = swarm;
	}

	@MessageMapping("/ticker")
	@SendTo("/topic/ticker")
	public TickerResponse fetchTicker(TickerRequest tickerRequest) throws Exception {
		return new TickerResponse(
				swarm.getPrice(tickerRequest.getExchangeId(), tickerRequest.getBase(), tickerRequest.getCounter()));
	}

	@RequestMapping("/market-data/log/{state}")
	public MarketDataLogStatus recordMarketDataLog(@PathVariable("state") String state) {
		MarketDataLogStatus status = new MarketDataLogStatus();

		if (state.toLowerCase().equals("open")) {
			status.setOpen(true);
			swarm.openMarketDataLog();
		} else if (state.toLowerCase().equals("close")) {
			status.setOpen(false);
			swarm.closeMarketDataLog();
		}

		return status;
	}
}
