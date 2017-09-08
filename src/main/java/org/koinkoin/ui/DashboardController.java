package org.koinkoin.ui;

import org.koinkoin.integration.TickerSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
class DashboardController {
	private TickerSource tickerSource;

	@Autowired
	public DashboardController(TickerSource tickerSource) {
		this.tickerSource = tickerSource;
	}

	@MessageMapping("/ticker")
	@SendTo("/topic/ticker")
	public TickerResponse fetchTicker(TickerRequest tickerRequest) throws Exception {
		return new TickerResponse(tickerSource.getPrice(tickerRequest.getExchangeId(), tickerRequest.getBase(), tickerRequest.getCounter()));
	}

}
