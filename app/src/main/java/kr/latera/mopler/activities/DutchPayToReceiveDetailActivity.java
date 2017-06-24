package kr.latera.mopler.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.R;
import kr.latera.mopler.adapters.PayListAdapter;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.db.model.DutchPayToReceive;
import kr.latera.mopler.db.model.Pay;
import kr.latera.mopler.dialog.EditPayDialog;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class DutchPayToReceiveDetailActivity extends AppCompatActivity
	implements AdapterView.OnItemClickListener
{
	public static final String ARG_DUTCHPAY_ID = "dutchPayId";

	private static String TAG;

	@BindString(R.string.string_money_unit)
	String strMoneyUnit;
	@BindString(R.string.string_people_unit)
	String strPeopleUnit;
	@BindView(R.id.tv_dutchpay_to_receive_detail_title)
	TextView tvTitle;
	@BindView(R.id.tv_dutchpay_to_receive_detail_amount)
	TextView tvAmount;
	@BindView(R.id.tv_dutchpay_to_receive_detail_nop)
	TextView tvNop;
	@BindView(R.id.lv_dutchpay_to_receive_detail_pays)
	ListView lvPays;

	private IDBManager dbm;
	private PayListAdapter mAdapter;
	private DutchPayToReceive dp;
	private long currentPayId;




	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dutchpay_to_receive_detail);
		ButterKnife.bind(this);
		currentPayId = 0;
		TAG = getClass().getSimpleName();
		dbm = DBManager.getInstance(this);
		long id = getIntent().getLongExtra(ARG_DUTCHPAY_ID, 0);
		mAdapter = new PayListAdapter(this, id, true, DBManager.getInstance(this));

		Log.d(TAG, "DutchPayId: " + id);
		dp = dbm.retrieveDutchPayById(id);
		int nop = dbm.retrievePayByDutchPay(id).size();

		if(dp != null)
		{
			tvTitle.setText(dp.getTitle());
			tvAmount.setText(dp.getTotal() + strMoneyUnit);
			tvNop.setText(nop + strPeopleUnit);

		}

		lvPays.setAdapter(mAdapter);
		lvPays.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Pay p = mAdapter.getItem(position);
		currentPayId = p.getId();
		EditPayDialog d = new EditPayDialog(this, p.getName(), p.getQuotient(), p.isPayed(), true);
		d.setDialogListener(new EditPayDialog.DialogListener()
		{
			@Override
			public void onOk(String name, int amount, boolean received, boolean update)
			{
				Log.d(TAG, "update pay: " + name + ", " + amount + ", " + received);
				dbm.updatePay(currentPayId, name, amount, received);
			}

			@Override
			public void onCancel()
			{

			}
		});
		d.show();

	}
}
