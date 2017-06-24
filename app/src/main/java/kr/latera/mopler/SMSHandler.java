package kr.latera.mopler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.joda.time.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.latera.mopler.activities.MainActivity;

/**
 * Created by Jinwoo Shin on 2017-06-18.
 */

public class SMSHandler
{
	private static final int MESSAGE_ID = 1;

	public SMSHandler(Context c, String from, String message)
	{
		Tuple t = split(from, message);

		SettingManager sm = SettingManager.getInstance(c);
		sm.set(SettingManager.BOOL_HAS_SMS, true);
		sm.set(SettingManager.STRING_SMS_TITLE, t.title);
		sm.set(SettingManager.INT_SMS_AMOUNT, Integer.valueOf(t.amount));
		sm.set(SettingManager.LONG_SMS_DATE, new DateTime().getMillis());

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(c)
						.setSmallIcon(R.drawable.ic_app_icon_round)
						.setContentTitle(c.getResources().getString(R.string.text_notification_title_outgo))
						.setContentText(c.getResources().getString(R.string.text_notification_content_outgo));
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(c, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(MESSAGE_ID, mBuilder.build());
	}


	private Tuple split(String from, String msg)
	{
		Tuple t = new Tuple();
		t.title = "";
		t.amount = "";
		// shinhan card
		if(from.equals("15447200") ||
				from.equals("01075843788")) // TODO : only for test!! remove this line when release
		{
			String pattern = "[0-9,]*원\\s*";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(msg);
			String[] res = p.split(msg);
			t.title = res[1];
			m.find();
			t.amount = Utility.extractNumber(m.group(0));
		}

		return t;
	}

	private class Tuple
	{
		public String title;
		public String amount;
	}

}
