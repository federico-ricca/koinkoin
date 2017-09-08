package org.koinkoin.ui;

import org.koinkoin.core.PriceData;

public class TickerResponse {
	private PriceData priceData;

	public TickerResponse(PriceData priceData) {
		setPriceData(priceData);
	}

	public PriceData getPriceData() {
		return priceData;
	}

	public void setPriceData(PriceData priceData) {
		this.priceData = priceData;
	}

}
