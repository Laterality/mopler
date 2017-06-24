package kr.latera.mopler.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;

import butterknife.BindString;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.latera.mopler.Logger;
import kr.latera.mopler.R;
import kr.latera.mopler.SettingManager;
import kr.latera.mopler.Utility;
import kr.latera.mopler.activities.MonthlyCalendarActivity;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.db.TransactionCallback;
import kr.latera.mopler.db.model.Plan_Daily;
import kr.latera.mopler.db.model.Record_General;
import kr.latera.mopler.dialog.AddGeneralRecordDialog;

/**
 * Created by Jinwoo Shin on 2017-05-09.
 */

public class SummaryFragment extends Fragment implements View.OnClickListener, RealmChangeListener
{
	private static String TAG;
	private Activity mActivity;

	private static final long INVALID = -1;
	public static final String PARAM_DATE = "param_date";
	private static final int TRANSACTION_CREATE_DAILY_PLAN = 1;

//	@BindView(R.id.main_btn_add_general_record)
	Button btnAddGeneralRecord;
//	@BindView(R.id.main_btn_add_repetition_record)
	Button btnAddRepetitionRecord;
//	@BindView(R.id.main_btn_monthly_calendar)
	Button btnMonthly;
//	@BindView(R.id.tv_main_budget)
	TextView tvBudget;
//	@BindView(R.id.tv_main_income)
	TextView tvIncome;
//	@BindView(R.id.tv_main_outgo)
	TextView tvOutgo;
//	@BindView(R.id.tv_main_summary)
	TextView tvSummary;
	@BindString(R.string.string_money_unit)
	String moneyUnit;

	private RealmResults<Record_General> recordsToday;
	private DateTime currentDate;
	private IDBManager dbm;


	public static SummaryFragment newInstance(long date, IDBManager dbm)
	{
		SummaryFragment sf = new SummaryFragment();
		Bundle args = new Bundle();
		sf.setDate(new DateTime(date));

		return sf;
	}

	public SummaryFragment()
	{
		TAG = getClass().getSimpleName();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "create view...");
		View mView = inflater.inflate(R.layout.item_group_summary, null);
		mActivity = getActivity();
		ButterKnife.bind(this, mView);

		Log.d(TAG, "init...");

		btnAddGeneralRecord.setOnClickListener(this);
		btnAddRepetitionRecord.setOnClickListener(this);
		btnMonthly.setOnClickListener(this);

		recordsToday = dbm.retrieveRecordByMonth(currentDate);

		recordsToday.addChangeListener(this);

		setAggregation();

		return mView;
	}


	@Override
	public void onClick(View v)
	{
		if(v.equals(btnAddGeneralRecord))
		{
			final AddGeneralRecordDialog d = new AddGeneralRecordDialog(mActivity);
			d.setOnShowListener(new DialogInterface.OnShowListener()
			{
				@Override
				public void onShow(DialogInterface dialog)
				{
					d.setCancelable(false);
				}
			});
			d.show();
		}
		else if(v.equals(btnMonthly))
		{
			startActivity(new Intent(mActivity, MonthlyCalendarActivity.class));
		}
	}

	private void setAggregation()
	{
		DBManager dbm = DBManager.getInstance(mActivity);
		Plan_Daily pd = dbm.retrieveDailyPlan(new DateTime());
		if(pd == null)
		{
			// If daily plan record does not exist, create new one
			dbm.createDailyPlan(new DateTime(), SettingManager.getInstance(mActivity).
							getInt(SettingManager.INT_SETTING_BUDGET),
					TRANSACTION_CREATE_DAILY_PLAN, new TransactionCallback<Plan_Daily>()
					{
						@Override
						public void onTransactionCommitted(int transactionCode, RealmResults<Plan_Daily> results)
						{
							int budget = SettingManager.getInstance(mActivity).getInt(SettingManager.INT_SETTING_BUDGET);
							int sumIncome = 0;
							int sumOutgo = 0;

							for(Record_General r : recordsToday)
							{
								if(r.getAmount() > 0) {sumIncome += r.getAmount();}
								else {sumOutgo += r.getAmount();}
							}
							tvBudget.setText(String.format(Utility.LOCALE, "%d%s", budget, moneyUnit));
							tvIncome.setText(String.format(Utility.LOCALE, "%d%s", sumIncome, moneyUnit));
							tvOutgo.setText(String.format(Utility.LOCALE, "%d%s", -sumOutgo, moneyUnit));
							tvSummary.setText(String.format(Utility.LOCALE, "%d%s", budget + sumIncome + sumOutgo, moneyUnit));
						}

						@Override
						public void onTransactionCanceled(int transactionCode, Throwable error)
						{
							Logger.onError(error);
						}
					});
		}
		else
		{
			int budget = SettingManager.getInstance(mActivity).getInt(SettingManager.INT_SETTING_BUDGET);
			int sumIncome = 0;
			int sumOutgo = 0;

			for(Record_General r : recordsToday)
			{
				if(r.getAmount() > 0) {sumIncome += r.getAmount();}
				else {sumOutgo += r.getAmount();}
			}
			tvBudget.setText(String.format(Utility.LOCALE, "%d%s", budget, moneyUnit));
			tvIncome.setText(String.format(Utility.LOCALE, "%d%s", sumIncome, moneyUnit));
			tvOutgo.setText(String.format(Utility.LOCALE, "%d%s", -sumOutgo, moneyUnit));
			tvSummary.setText(String.format(Utility.LOCALE, "%d%s", budget + sumIncome + sumOutgo, moneyUnit));
		}
	}

	@Override
	public void onChange(Object element)
	{
		setAggregation();
	}

	public void setDate(DateTime d) {this.currentDate = d;}
	public void setDBManager(IDBManager dbm){this.dbm = dbm;}
	public DateTime getDate() {return currentDate;}
}
