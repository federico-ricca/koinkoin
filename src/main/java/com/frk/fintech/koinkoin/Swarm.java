package com.frk.fintech.koinkoin;

import java.util.ArrayList;
import java.util.List;

public class Swarm {
	private List<TradingBot> bots = new ArrayList<>();

	public void add(TradingBot tradingBot) {
		bots.add(tradingBot);
	}

	public void trade() {
		for (TradingBot bot : bots) {
			try {
				bot.tradePercentage();
			} catch (InvalidCurrency | InsufficientFundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
