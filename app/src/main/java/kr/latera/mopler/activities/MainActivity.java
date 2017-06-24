package kr.latera.mopler.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.Logger;
import kr.latera.mopler.MockDataLoader;
import kr.latera.mopler.R;
import kr.latera.mopler.SettingManager;
import kr.latera.mopler.Utility;
import kr.latera.mopler.adapters.SummaryListAdapter;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.dialog.AddGeneralRecordDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private static String TAG;
	@BindView(R.id.tb_main)
	Toolbar tbMain;
	@BindView(R.id.elv_main_records)
	ExpandableListView elvSummary;
	@BindView(R.id.ll_main_menu_button_group)
	LinearLayout llBtnGroup;
	@BindView(R.id.ibtn_main_add_record)
	ImageButton ibtnAddRecord;
	@BindView(R.id.ibtn_main_view_records)
	ImageButton ibtnViewRecords;
	@BindView(R.id.ibtn_main_view_statistics)
	ImageButton ibtnStatistics;
	@BindView(R.id.ibtn_main_dutch_pay_note)
	ImageButton ibtnDutchPayNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    TAG = getClass().getSimpleName();

	    ButterKnife.bind(this);

	    setSupportActionBar(tbMain);
	    getSupportActionBar().setDisplayShowTitleEnabled(false);

	    SettingManager sm = SettingManager.getInstance(this);

	    if(sm.getBoolean(SettingManager.BOOL_SETTING_FIRST))
	    {
//		    Log.d(TAG, "First run...Load Mock data");
//		    MockDataLoader.Load(this);
	    }
	    if(sm.getBoolean(SettingManager.BOOL_HAS_SMS))
	    {
		    		AddGeneralRecordDialog d = new AddGeneralRecordDialog(this,
						    sm.getString(SettingManager.STRING_SMS_TITLE),
		    				sm.getInt(SettingManager.INT_SMS_AMOUNT),
						    sm.getLong(SettingManager.LONG_SMS_DATE));
		    		d.show();
		    sm.set(SettingManager.BOOL_HAS_SMS, false);
	    }



	    DateTime d = new DateTime();
	    Logger.onReport("now: " + d.toString());
	    final ProgressDialog pd = new ProgressDialog(this);
	    pd.setCancelable(false);
	    pd.setMessage("불러오는 중");
	    final SummaryListAdapter mAdapter = new SummaryListAdapter(this, d,
	    new SummaryListAdapter.OnLoadListener()
	    {
		    @Override
		    public void onLoadStart()
		    {
			    pd.show();
		    }

		    @Override
		    public void onLoadEnd()
		    {
			    pd.dismiss();
		    }
	    });

	    elvSummary.setAdapter(mAdapter);
//		elvSummary.addHeaderView(llBtnGroup);

	    ibtnAddRecord.setOnClickListener(this);
	    ibtnViewRecords.setOnClickListener(this);
	    ibtnStatistics.setOnClickListener(this);
	    ibtnDutchPayNote.setOnClickListener(this);

	    DBManager dbm = DBManager.getInstance(this);
	    dbm.reportCounts();

//	    elvSummary.addHeaderView(llBtnGroup);
    }

	@Override
	public void onClick(View v)
	{
		if(v.equals(ibtnAddRecord))
		{
			final AddGeneralRecordDialog d = new AddGeneralRecordDialog(this);
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
		else if(v.equals(ibtnViewRecords))
		{
			Toast.makeText(this, "미구현 기능입니다", Toast.LENGTH_LONG).show();
		}
		else if(v.equals(ibtnStatistics))
		{
			Toast.makeText(this, "미구현 기능입니다", Toast.LENGTH_LONG).show();
		}
		else if(v.equals(ibtnDutchPayNote))
		{
			// TODO : Enter into dutchpay note
			startActivity(new Intent(this, DutchPayNoteActivity.class));
		}
	}
}
