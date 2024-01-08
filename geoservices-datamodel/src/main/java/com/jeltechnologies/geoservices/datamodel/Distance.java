package com.jeltechnologies.geoservices.datamodel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Distance(double exact, int kilometrs, int hectometres, int metres) {

    private static final BigDecimal TEN = new BigDecimal(10);
    private static final BigDecimal THOUSAND = new BigDecimal(1000);

    public Distance(double exact) {
	this(
		exact, 
		new BigDecimal(exact).setScale(0, RoundingMode.HALF_UP).intValue(),
		new BigDecimal(exact).multiply(TEN).setScale(0, RoundingMode.HALF_UP).intValue(), 
		new BigDecimal(exact).multiply(THOUSAND).setScale(0, RoundingMode.HALF_UP).intValue());
    }
}
