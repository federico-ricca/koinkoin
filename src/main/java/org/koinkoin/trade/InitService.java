package org.koinkoin.trade;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.koinkoin.integration.TickerSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitService {
	private TickerSource tickerSource;

	@Autowired
	public InitService(TickerSource tickerSource) {
		this.tickerSource = tickerSource;
	}

	@PostConstruct
	public void initInfra() {
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

		System.out.println("executing " + tickerSource);
		singleThreadExecutor.execute(tickerSource);
	}
}
