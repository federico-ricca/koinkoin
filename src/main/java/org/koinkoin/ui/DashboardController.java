package org.koinkoin.ui;

import org.koinkoin.swarm.Greeting;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
class DashboardController {
	@MessageMapping("/ticker")
	@SendTo("/topic/ticker")
	public Greeting fetchTicker(TickerRequest tickerRequest) throws Exception {
		return new Greeting("Hello, " + tickerRequest.getBase() + "!");
	}

}
