package kr.latera.mopler.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import kr.latera.mopler.R;
import kr.latera.mopler.Utility;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.TransactionCallback;
import kr.latera.mopler.db.model.Plan_Daily;
import kr.latera.mopler.db.model.Record_General;

/**
 * Created by Jinwoo Shin on 2017-05-11.
 */

public class SummaryListAdapter extends BaseExpandableListAdapter
		implements RealmChangeListener
{
	private static final int LIST_BASE_LENGTH = 20;
	private static final int LIST_BASE_LENGTH_PLANS = 32;
	private static final int TRANSACTION_RETRIEVE_RECORDS = 1;

	private boolean inProgress;
	private static String TAG;
	private Context mContext;
	private OnLoadListener mListener;

	private boolean usePrevLoad;
	private int prevLoadCount;
	private DateTime date;
	private RealmResults<Plan_Daily> plansCurrentMonth;
	private RealmResults<Plan_Daily> plansPrevMonth;
	private RealmResults<Record_General> resultsCurrentMonth;
	private RealmResults<Record_General> recordsPrevMonth;
	private List<List<Record_General>> recordsCurrentMonth;

	private String strYearFormat;
	private String strMonthFormat;
	private String strDayFormat;

	private LayoutInflater inflater;


	public SummaryListAdapter(Context context, DateTime d, OnLoadListener listener)
	{
		mListener = listener;
		init(context, false, d, 0);
	}

	public SummaryListAdapter(Context context, DateTime d, int prevLoadCount)
	{
		init(context, prevLoadCount > 0, d, prevLoadCount < 0 ? prevLoadCount : 0);
	}

	private void init(Context c, boolean usePrevLoad, DateTime d, int prevLoadCount)
	{
		TAG = getClass().getSimpleName();
		mContext = c;
		this.usePrevLoad = usePrevLoad;
		date = d;
		this.prevLoadCount = prevLoadCount;
		inProgress = true;
		inflater = LayoutInflater.from(c);

		strYearFormat = c.getResources().getString(R.string.string_format_year);
		strMonthFormat = c.getResources().getString(R.string.string_format_month);
		strDayFormat = c.getResources().getString(R.string.string_format_day);

		recordsCurrentMonth = new ArrayList<>(LIST_BASE_LENGTH_PLANS);

		if(mListener != null)
		{
			mListener.onLoadStart();
		}

		DBManager dbm = DBManager.getInstance(c);
		//date = d.monthOfYear().setCopy(5); // TODO : Only for dev. remove this line!!
		plansCurrentMonth = dbm.retrieveDailyPlanByMonth(date);
		resultsCurrentMonth = dbm.retrieveRecordByMonth(date);
		resultsCurrentMonth.addChangeListener(this);

//		resultsCurrentMonth.load();

//		Log.d(TAG, "Group: " + plansCurrentMonth.size() + ", Records: " + recordsCurrentMonth.size());

		setList();

	}

	@Override
	public int getGroupCount()
	{
		//Log.d(TAG, "Group count : " + plansCurrentMonth.size());
		return plansCurrentMonth.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		Log.d(TAG, "Results: " + recordsCurrentMonth.size());
		Log.d(TAG, "Children count : " + recordsCurrentMonth.get(groupPosition).size());
		return recordsCurrentMonth.get(groupPosition).size();
	}


	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		//Log.d(TAG, "Group view position : " + groupPosition);
		convertView = inflater.inflate(R.layout.item_group_summary, null);
//		TextView tvDate = (TextView) convertView.findViewById(R.id.tv_group_summary_date);
		TextView tvYear = (TextView) convertView.findViewById(R.id.tv_group_summary_date_year);
		TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_group_summary_date_month);
		TextView tvDay = (TextView) convertView.findViewById(R.id.tv_group_summary_date_day);
		TextView tvBudget = (TextView) convertView.findViewById(R.id.tv_group_summary_budget);
		TextView tvOutgo = (TextView) convertView.findViewById(R.id.tv_group_summary_outgo);
		TextView tvIncome = (TextView) convertView.findViewById(R.id.tv_group_summary_income);
		TextView tvSummary = (TextView) convertView.findViewById(R.id.tv_group_summary_summary);

		int budget = plansCurrentMonth.get(groupPosition).getBudget();
		int sumIncome = 0;
		int sumOutgo = 0;
		DateTime d = new DateTime(plansCurrentMonth.get(groupPosition).getDate());

		//Log.d(TAG, "getGroupView: " + groupPosition + "/" + recordsCurrentMonth.size());
		if(groupPosition < recordsCurrentMonth.size())
		{
			for(Record_General r : recordsCurrentMonth.get(groupPosition))
			{
				if(r.getAmount() > 0) {sumIncome += r.getAmount();}
				else {sumOutgo += r.getAmount();}
			}
		}

		// the first month of the year shows year
		if(groupPosition == 0){tvYear.setVisibility(View.VISIBLE);}
		else if(new DateTime(plansCurrentMonth.get(groupPosition - 1).getDate()).year().get() !=
				d.year().get())
		{tvYear.setVisibility(View.VISIBLE);}
		else {tvYear.setVisibility(View.GONE);}

		// the first day of the month shows month
		if(groupPosition == 0){tvMonth.setVisibility(View.VISIBLE);}
		else if(new DateTime(plansCurrentMonth.get(groupPosition - 1).getDate()).monthOfYear().get() !=
				d.monthOfYear().get())
		{tvMonth.setVisibility(View.VISIBLE);}
		else {tvMonth.setVisibility(View.GONE);}

		// if has no income, shows no income
//		if(sumIncome == 0) {tvIncome.setVisibility(View.GONE);}
//		else{tvIncome.setVisibility(View.VISIBLE);}


//		tvDate.setText(d.toString());
		tvYear.setText(String.format(Utility.LOCALE, strYearFormat, d.getYear()));
		tvMonth.setText(String.format(Utility.LOCALE, strMonthFormat, d.monthOfYear().get()));
		tvDay.setText(String.format(Utility.LOCALE, strDayFormat, d.getDayOfMonth()));
		tvBudget.setText(Utility.sanitizeMoneyString(plansCurrentMonth.get(groupPosition).getBudget()));
		tvOutgo.setText(Utility.sanitizeMoneyString(-sumOutgo));
		tvIncome.setText(Utility.sanitizeMoneyString(sumIncome));
		tvSummary.setText(Utility.sanitizeMoneyString(budget + (sumIncome + sumOutgo)));


		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		//Log.d(TAG, "Children view position : " + childPosition);
		convertView = inflater.inflate(R.layout.item_child_summary_record, null);
		TextView tvContent = (TextView) convertView.findViewById(R.id.tv_child_summary_record_content);
		TextView tvWhere = (TextView) convertView.findViewById(R.id.tv_child_summary_record_where);
		TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_child_summary_record_amount);

		Record_General r = recordsCurrentMonth.get(groupPosition).get(childPosition);

		tvContent.setText(r.getContent());
		tvWhere.setText(r.getWhere());
		tvAmount.setText(Utility.sanitizeMoneyString(r.getAmount()));

		return convertView;
	}

	@Override
	public Plan_Daily getGroup(int groupPosition)
	{
		return plansCurrentMonth.get(groupPosition);
	}

	@Override
	public Record_General getChild(int groupPosition, int childPosition)
	{
		return recordsCurrentMonth.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return 0;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return false;
	}


	@Override
	public void onChange(Object element)
	{
		setList();
		notifyDataSetChanged();
		Log.d(TAG, "data changed");
		Log.d(TAG, "Group: " + plansCurrentMonth.size() + ", Records: " + recordsCurrentMonth.size());
	}



	private void setList()
	{
		Log.d(TAG, "set list...group: " + plansCurrentMonth.size() + ", child: " + recordsCurrentMonth.size());
		resultsCurrentMonth.sort("date", Sort.ASCENDING);
		recordsCurrentMonth.clear();
		int idxDay = 0;
		for(Plan_Daily p : plansCurrentMonth)
		{
			long from = new DateTime(p.getDate()).withTimeAtStartOfDay().getMillis();
			long to = new DateTime(from).plusDays(1).getMillis();

			recordsCurrentMonth.add(new ArrayList<Record_General>(LIST_BASE_LENGTH));

			for(Record_General r : resultsCurrentMonth)
			{

				if(r.getDate() >= from && r.getDate() < to)
				{
					recordsCurrentMonth.get(idxDay).add(r);
				}
//				else if(r.getDate() >= to) { break; }

			}
			Log.d(TAG, "Records :" + recordsCurrentMonth.get(idxDay).size());
			idxDay++;
		}
		if(inProgress)
		{
			if(mListener != null)
			{
				mListener.onLoadEnd();
			}
			inProgress = false;
		}
	}


	public interface OnLoadListener
	{
		public void onLoadStart();
		public void onLoadEnd();
	}
}
