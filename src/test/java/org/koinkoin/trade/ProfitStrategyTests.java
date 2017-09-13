package org.koinkoin.trade;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class ProfitStrategyTests {

	@Test
	public void testStopLossEqualsExpectedProfit() {
		ProfitStrategy profitStrategy = new ProfitStrategy(new BigDecimal(-2.5f));

		profitStrategy.setExpectedProfit(new BigDecimal(-2.5f));

		Assert.assertTrue(profitStrategy.reachedStopLoss());
		Assert.assertFalse(profitStrategy.hasProfits());
	}

	@Test
	public void testStopLossGreaterThanExpectedProfit() {
		ProfitStrategy profitStrategy = new ProfitStrategy(new BigDecimal(-1.5f));

		profitStrategy.setExpectedProfit(new BigDecimal(-2.5f));

		Assert.assertTrue(profitStrategy.reachedStopLoss());
		Assert.assertFalse(profitStrategy.hasProfits());
	}
	
	@Test
	public void testHasProfits() {
		ProfitStrategy profitStrategy = new ProfitStrategy(new BigDecimal(-2.5f));

		profitStrategy.setExpectedProfit(new BigDecimal(2.5f));

		Assert.assertFalse(profitStrategy.reachedStopLoss());
		Assert.assertTrue(profitStrategy.hasProfits());
	}

}
