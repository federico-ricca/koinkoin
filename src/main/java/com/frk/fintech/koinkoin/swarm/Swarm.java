package com.frk.fintech.koinkoin.swarm;

import java.util.ArrayList;
import java.util.List;

import com.frk.fintech.koinkoin.bot.TradingBot;
import com.frk.fintech.koinkoin.core.InsufficientFundsException;
import com.frk.fintech.koinkoin.core.InvalidCurrency;

public class Swarm {
	private List<TradingBot> bots = new ArrayList<>();

	public void add(TradingBot tradingBot) {
		bots.add(tradingBot);
	}

	public void trade() {
		for (TradingBot bot : bots) {
			try {
				bot.trade();
			} catch (InvalidCurrency | InsufficientFundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
