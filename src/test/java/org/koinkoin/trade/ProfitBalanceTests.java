package org.koinkoin.trade;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.koinkoin.core.ProfitBalance;

public class ProfitBalanceTests {

	@Test
	public void testStopLossEqualsExpectedProfit() {
		ProfitBalance profitBalance = new ProfitBalance(new BigDecimal(-2.5f));

		profitBalance.setExpectedProfit(new BigDecimal(-2.5f));

		Assert.assertTrue(profitBalance.reachedStopLoss());
		Assert.assertFalse(profitBalance.hasProfits());
	}

	@Test
	public void testStopLossGreaterThanExpectedProfit() {
		ProfitBalance profitBalance = new ProfitBalance(new BigDecimal(-1.5f));

		profitBalance.setExpectedProfit(new BigDecimal(-2.5f));

		Assert.assertTrue(profitBalance.reachedStopLoss());
		Assert.assertFalse(profitBalance.hasProfits());
	}

	@Test
	public void testHasProfits() {
		ProfitBalance profitBalance = new ProfitBalance(new BigDecimal(-2.5f));

		profitBalance.setExpectedProfit(new BigDecimal(2.5f));

		Assert.assertFalse(profitBalance.reachedStopLoss());
		Assert.assertTrue(profitBalance.hasProfits());
	}

}
