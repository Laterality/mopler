package kr.latera.mopler;

import org.joda.time.DateTime;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jinwoo Shin on 2017-04-25.
 */

public class Utility
{
	public static final String REGEX_NUMBER = "^[0-9]+$";
	public static final Locale LOCALE = Locale.KOREAN;


	public static String getDateString(DateTime date)
	{
		return String.format(LOCALE, "%d년 %d월 %d일", date.year().get(), date.monthOfYear().get(), date.dayOfMonth().get());
	}

	public static boolean checkNumber(String str)
	{
		return Pattern.matches(REGEX_NUMBER, str);
	}

	public static String extractNumber(String str)
	{
		return str.replaceAll("\\D", "");
	}

	public static String sanitizeMoneyString(int money)
	{
		String ret = String.valueOf(money);

		// TODO : implement here
//		for(int i = ret.length() - 1 ; i >= 0; i--)
//		{
//		}
		return ret;
	}
}
