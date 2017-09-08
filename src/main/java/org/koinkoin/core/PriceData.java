package org.koinkoin.core;

import java.math.BigDecimal;

public class PriceData {
	private BigDecimal askPrice;
	private BigDecimal bidPrice;
	private BigDecimal high;
	private BigDecimal low;

	public PriceData(BigDecimal askPrice, BigDecimal bidPrice) {
		setAskPrice(askPrice);
		setBidPrice(bidPrice);
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	public BigDecimal getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}
}
