package kr.latera.mopler;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Jinwoo Shin on 2017-05-07.
 */

public class Logger
{
	private static final String TAG = "LATERA_ERROR_HANDLER";

	public static void onReport(String msg)
	{
		Log.d(TAG, "REPORT");
		Log.d(TAG, " [MESSAGE]");
		Log.d(TAG, msg);
	}

	public static void onError(String msg)
	{
		Log.d(TAG, "ERROR OCCURRED");
		Log.d(TAG, " [MESSAGE]");
		Log.d(TAG, msg);
	}

	public static void onError(Throwable error)
	{
		Log.d(TAG, "ERROR OCCURRED");
		Log.d(TAG, "  [MESSAGE]");
		Log.d(TAG, error.getMessage());
		Log.d(TAG, "  [STACK TRACE]");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		error.printStackTrace(pw);
		Log.d(TAG, sw.toString());
	}


}
