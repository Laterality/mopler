package kr.latera.mopler;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jinwoo Shin on 2017-05-07.
 */

public class SettingManager
{
	private static final String TAG = "SETTING_MANAGER";
	private static final String PREFS_NAME = "mopler_setting";

	public static final String INT_SETTING_BUDGET = "budget";
	public static final String BOOL_SETTING_FIRST = "first";
	public static final String BOOL_HAS_SMS = "has_sms";
	public static final String INT_SMS_AMOUNT = "sms_amount";
	public static final String LONG_SMS_DATE = "sms_date";
	public static final String STRING_SMS_TITLE = "sms_title";

	public static final int INVALID = -1;

	private static SettingManager sm;
	private SharedPreferences prefs;

	public static SettingManager getInstance(Context context)
	{
		if(sm == null)
		{
			sm = new SettingManager(context);
		}
		return sm;
	}

	private  SettingManager(Context c)
	{
		prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	public boolean getBoolean(@NonNull String key)
	{
		if(key.equals(BOOL_SETTING_FIRST))
		{
			boolean ret = prefs.getBoolean(key, true);
			if(ret) {prefs.edit().putBoolean(BOOL_SETTING_FIRST,false).apply();}
			return ret;
		}
		else if(key.equals(BOOL_HAS_SMS))
		{
			return prefs.getBoolean(key, false);
		}
		return false;
	}

	public int getInt(@NonNull String key)
	{
		if(key.equals(INT_SETTING_BUDGET))
		{
			// TODO : change
			return 10000;
		}
		else if(key.equals(INT_SMS_AMOUNT))
		{
			Log.d(TAG, "setting man. return: " + prefs.getInt(key, INVALID));
			return prefs.getInt(key, INVALID);
		}
		return prefs.getInt(key, INVALID);
	}

	public long getLong(@NonNull String key)
	{
		if(key.equals(LONG_SMS_DATE))
		{
			return prefs.getLong(key, INVALID);
		}

		return INVALID;
	}

	public @Nullable String getString(@NonNull String key)
	{
		if(key.equals(STRING_SMS_TITLE))
		{
			return prefs.getString(key, null);
		}

		return null;
	}

	public void set(@NonNull String key, boolean value) { prefs.edit().putBoolean(key, value).apply(); }

	public void set(@NonNull String key, int value)
	{
		prefs.edit().putInt(key, value).apply();
	}

	public void set(@NonNull String key, long value) {prefs.edit().putLong(key, value).apply();}

	public void set(@NonNull String key, String value) { prefs.edit().putString(key, value).apply(); }
}
