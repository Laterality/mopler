package kr.latera.mopler;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Date;

import io.realm.RealmResults;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.TransactionCallback;
import kr.latera.mopler.db.model.Plan_Daily;

/**
 * Created by Jinwoo Shin on 2017-05-14.
 */

public class MockDataLoader implements TransactionCallback
{
	private static String TAG;
	private static final int TRANSACTION_CREATE_DAILY_PLAN = 1;
	private static final int TRANSACTION_CREATE_GENERAL_RECORD = 2;

	private static final int FINISH_COUNT = 10;

	private static MockDataLoader mdl;
	private static int countLoaded;


	public static void Load(Context c)
	{
		if(mdl == null)
		{
			mdl = new MockDataLoader(c);
		}
	}

	private MockDataLoader(final Context c)
	{
		TAG = this.getClass().getSimpleName();
		final DBManager dbm = DBManager.getInstance(c);
		SettingManager sm = SettingManager.getInstance(c);

		int budget = sm.getInt(SettingManager.INT_SETTING_BUDGET);
		countLoaded = 0;

		dbm.createDailyPlan(new DateTime(2017, 5, 10, 12, 0), budget, TRANSACTION_CREATE_DAILY_PLAN, new TransactionCallback<Plan_Daily>()
		{
			@Override
			public void onTransactionCommitted(int transactionCode, RealmResults<Plan_Daily> results)
			{
				dbm.createGeneralRecord(c, "한솥", "칠리포크", -3600, new DateTime(2017, 5, 10, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
				dbm.createGeneralRecord(c, "엽기떡볶이", "엽떡", -4000, new DateTime(2017, 5, 10, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
				dbm.createGeneralRecord(c, "당구장", "당구", -3000, new DateTime(2017, 5, 10, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
				dbm.createGeneralRecord(c, "볼링장", "볼링", -10000, new DateTime(2017, 5, 10, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
			}

			@Override
			public void onTransactionCanceled(int transactionCode, Throwable error)
			{
				Logger.onError(error);
			}
		});
		dbm.createDailyPlan(new DateTime(2017, 5, 11, 12, 0), budget, TRANSACTION_CREATE_DAILY_PLAN, new TransactionCallback<Plan_Daily>()
		{
			@Override
			public void onTransactionCommitted(int transactionCode, RealmResults<Plan_Daily> results)
			{
				dbm.createGeneralRecord(c, "삼겹살집", "고기", -12500, new DateTime(2017, 5, 11, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
				dbm.createGeneralRecord(c, "쥬씨", "자파", -2000, new DateTime(2017, 5, 11, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
			}

			@Override
			public void onTransactionCanceled(int transactionCode, Throwable error)
			{
				Logger.onError(error);
			}
		});
		dbm.createDailyPlan(new DateTime(2017, 5, 12, 12, 0), budget, TRANSACTION_CREATE_DAILY_PLAN, new TransactionCallback<Plan_Daily>()
		{
			@Override
			public void onTransactionCommitted(int transactionCode, RealmResults<Plan_Daily> results)
			{
				dbm.createGeneralRecord(c, "터미널", "시외버스", -10200, new DateTime(2017, 5, 12, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
				dbm.createGeneralRecord(c, "CU", "커피", -1000, new DateTime(2017, 5, 12, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
			}

			@Override
			public void onTransactionCanceled(int transactionCode, Throwable error)
			{
				Logger.onError(error);
			}
		});

		dbm.createDailyPlan(new DateTime(2017, 5, 14, 12, 0), budget, TRANSACTION_CREATE_DAILY_PLAN, new TransactionCallback<Plan_Daily>()
		{
			@Override
			public void onTransactionCommitted(int transactionCode, RealmResults<Plan_Daily> results)
			{
				dbm.createGeneralRecord(c, "터미널", "시외버스", -10200, new DateTime(2017, 5, 14, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
				dbm.createGeneralRecord(c, "다이소", "잡화", -3000, new DateTime(2017, 5, 14, 12, 0), TRANSACTION_CREATE_GENERAL_RECORD, MockDataLoader.this);
			}

			@Override
			public void onTransactionCanceled(int transactionCode, Throwable error)
			{
				Logger.onError(error);
			}
		});

	}

	@Override
	public void onTransactionCommitted(int transactionCode, RealmResults results)
	{
		countLoaded++;
		Log.d(TAG, "Mock data loaded, Code " + transactionCode);
		Log.d(TAG, "loaded : " + countLoaded);
	}

	@Override
	public void onTransactionCanceled(int transactionCode, Throwable error)
	{
		Logger.onError(error);
	}
}
