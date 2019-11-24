package ie.gmit.ds.utils;



/**
 * Author: Ethan Horrigan
 * A Utilities class to store constant variables to avoid the use of 'magic' numbers.
 * 
 * Initial : Added Password Length & Sleep Time
 * 24/11/2019 : Added ISO Constant
 * 24/11/2019 : Added Response Messages
 */
public class Constants {
	
	public final static int PASSWORD_LENGTH = 5;
	
	public static final int SLEEP_TIME = 3;
	
	// Encoding Types
	public final static String ISO = "ISO-8859-1";
	
	/*
	 * Response Messages
	 * HTTP Status Codes: https://www.restapitutorial.com/httpstatuscodes.html
	 */
	public final static String USER_NOT_FOUND = "[404] USER NOT FOUND ID: ";
	public final static String USER_TAKEN = "[409] USER ALREADY IN USE: ";
	public final static String LOGIN_SUCCESS = "[200] LOGIN SUCCESSFUL FOR USER: ";
	public final static String LOGIN_FAILED = "[400] LOGIN FAILED FOR USER: ";
	
	private Constants() {
	}

}
