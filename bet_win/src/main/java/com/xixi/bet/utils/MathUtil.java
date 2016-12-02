package com.xixi.bet.utils;

import java.math.BigDecimal;

public class MathUtil {

	public static BigDecimal randomPointNum(){
		return new BigDecimal(Math.random()).setScale(Double.valueOf(14+Math.random()*4).intValue(), BigDecimal.ROUND_HALF_UP);
	}
}
