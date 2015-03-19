package com.threelogin.utils;

public class TokenUtil {

	public static long toExpiresTimeToMill(String expires_in) {
		return System.currentTimeMillis() + Long.parseLong(expires_in) * 1000;

	}

	public static long toMillToExpiresTime(Long mill) {
		return (mill - System.currentTimeMillis()) / 1000;
	}

	public static boolean isTokenFail(long mill) {
		return System.currentTimeMillis() - mill >= 0;
	}

}
