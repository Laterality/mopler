package kr.latera.mopler.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.R;
import kr.latera.mopler.adapters.DutchPaysToReceiveListAdapter;
import kr.latera.mopler.adapters.DutchPaysToSendListAdapter;
import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.db.model.DutchPayToSend;
import kr.latera.mopler.dialog.DutchPayToSendEditDialog;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class DutchPaysToSendFragment extends Fragment
	implements AdapterView.OnItemClickListener
{

	private Activity mActivity;
	private IDBManager dbm;
	private DutchPaysToSendListAdapter mAdapter;
	private long currentId;

	@BindView(R.id.lv_fragment_dutchpays_to_send_list)
	ListView lvDutchPays;

	public static DutchPaysToSendFragment newInstance(IDBManager dbm)
	{
		DutchPaysToSendFragment fragment = new DutchPaysToSendFragment();
		fragment.setDBManager(dbm);

		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_dutchpays_to_send, null);

		ButterKnife.bind(this, rootView);

		mActivity = getActivity();

		mAdapter = new DutchPaysToSendListAdapter(mActivity, dbm);

		lvDutchPays.setAdapter(mAdapter);
		lvDutchPays.setOnItemClickListener(this);

		return rootView;
	}

	private void setDBManager(IDBManager dbm)
	{
		this.dbm = dbm;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		DutchPayToSend dp = mAdapter.getItem(position);
		currentId = dp.getId();
		DutchPayToSendEditDialog d = new DutchPayToSendEditDialog(mActivity,
				dp.getTitle(), dp.getAmount(), dp.isPayed());
		d.setDialogListener(new DutchPayToSendEditDialog.DialogListener()
		{
			@Override
			public void onOk(String title, int amount, boolean sent)
			{
				dbm.updateDutchPayToSend(currentId, title, amount, sent);
			}

			@Override
			public void onCancel()
			{

			}
		});
		d.show();
	}
}
