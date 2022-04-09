package com.pp.arithmo.utils;

public class CommonUtils {
	public static int genRandom(int min, int max) {
		return (int)Math.floor(Math.random()*(max-min+1)+min);
	}
}
