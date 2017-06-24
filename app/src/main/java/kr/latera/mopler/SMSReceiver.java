package kr.latera.mopler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.regex.Pattern;

/**
 * Created by Jinwoo Shin on 2017-06-17.
 */

public class SMSReceiver extends BroadcastReceiver
{
	private static final String TAG = "RECEIVER";

	@Override
	public void onReceive(Context context, @NonNull Intent intent)
	{
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
		{
			Log.d(TAG, "SMS Received");
			Bundle b = intent.getExtras();
			String format = intent.getStringExtra("format");
			Object[] messages = (Object[]) b.get("pdus");
			SmsMessage[] smsMessages = new SmsMessage[messages.length];

			for(int i = 0 ; i < messages.length; i++)
			{
				if(Build.VERSION.SDK_INT >= 23)
				{
					smsMessages[i] = SmsMessage.createFromPdu((byte[])messages[i], format);
				}
				else
				{
					smsMessages[i] = SmsMessage.createFromPdu((byte[])messages[i]);
				}
			}

			Log.d(TAG, "Received Time: " + new DateTime(smsMessages[0].getTimestampMillis()).toString());
			Log.d(TAG, "From: " + smsMessages[0].getDisplayOriginatingAddress());
			Log.d(TAG, "Content: " + smsMessages[0].getMessageBody());
			new SMSHandler(context,
					smsMessages[0].getDisplayOriginatingAddress(),
					smsMessages[0].getMessageBody());
		}
	}
}
