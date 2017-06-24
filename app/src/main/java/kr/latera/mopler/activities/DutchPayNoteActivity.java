package kr.latera.mopler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.flyco.tablayout.SlidingTabLayout;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.R;
import kr.latera.mopler.adapters.DutchPayNotePagerAdapter;
import kr.latera.mopler.db.DBManager;

/**
 * Created by Jinwoo Shin on 2017-05-30.
 */

public class DutchPayNoteActivity extends AppCompatActivity implements View.OnClickListener
{

	@BindView(R.id.tb_dutchpay)
	Toolbar tb;
	@BindView(R.id.ibtn_dutchpay_add_dutchpay)
	ImageButton ibtnAddDutchpay;
	@BindView(R.id.stl_dutchpay_tab)
	SlidingTabLayout stl;
	@BindView(R.id.vp_dutchpay_dutchpays)
	ViewPager vpDutchpays;
	@BindString(R.string.string_to_receive)
	String strToReceive;
	@BindString(R.string.string_to_send)
	String strToSend;

	private DutchPayNotePagerAdapter mAdapter;
	private DBManager dbm;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dutchpay);

		setSupportActionBar(tb);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);

		ButterKnife.bind(this);
		dbm = DBManager.getInstance(this);

		ibtnAddDutchpay.setOnClickListener(this);

		String[] titles = new String[]{strToReceive, strToSend};
		mAdapter = new DutchPayNotePagerAdapter(getFragmentManager(), dbm);
		vpDutchpays.setAdapter(mAdapter);
		stl.setViewPager(vpDutchpays, titles);
	}

	@Override
	public void onClick(View v)
	{
		if(v.equals(ibtnAddDutchpay))
		{
			Intent i = new Intent(this, AddDutchPayActivity.class);
			startActivity(i);
		}
	}
}
