package kr.latera.mopler.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.R;
import kr.latera.mopler.activities.DutchPayToReceiveDetailActivity;
import kr.latera.mopler.adapters.DutchPaysToReceiveListAdapter;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.IDBManager;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class DutchPaysToReceiveFragment extends Fragment
{
	private Activity mActivity;
	private IDBManager dbm;
	private DutchPaysToReceiveListAdapter mAdapter;

	@BindView(R.id.lv_fragment_dutchpays_to_receive_list)
	ListView lvDutchPays;

	public static DutchPaysToReceiveFragment newInstance(IDBManager dbm)
	{
		DutchPaysToReceiveFragment fragment = new DutchPaysToReceiveFragment();
		fragment.setDBManager(dbm);

		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_dutchpays_to_receive, null);

		ButterKnife.bind(this, rootView);

		mActivity = getActivity();

		mAdapter = new DutchPaysToReceiveListAdapter(mActivity, dbm);

		lvDutchPays.setAdapter(mAdapter);
		lvDutchPays.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent i = new Intent(mActivity, DutchPayToReceiveDetailActivity.class);
				i.putExtra(DutchPayToReceiveDetailActivity.ARG_DUTCHPAY_ID, id);
				startActivity(i);
			}
		});

		return rootView;
	}

	private void setDBManager(IDBManager dbm)
	{
		this.dbm = dbm;
	}

}
