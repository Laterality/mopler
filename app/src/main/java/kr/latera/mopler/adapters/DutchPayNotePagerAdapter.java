package kr.latera.mopler.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.fragments.DutchPaysToReceiveFragment;
import kr.latera.mopler.fragments.DutchPaysToSendFragment;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class DutchPayNotePagerAdapter extends FragmentStatePagerAdapter
{
	private static final int FRAGMENT_COUNT = 2;
	private Fragment[] fragments;
	private IDBManager dbm;

	public DutchPayNotePagerAdapter(FragmentManager fm, IDBManager dbm)
	{
		super(fm);
		this.dbm = dbm;
		fragments = new Fragment[FRAGMENT_COUNT];
		fragments[0] = DutchPaysToReceiveFragment.newInstance(dbm);
		fragments[1] = DutchPaysToSendFragment.newInstance(dbm);
	}

	@Override
	public Fragment getItem(int position)
	{
		return fragments[position];
	}

	@Override
	public int getCount()
	{
		return fragments.length;
	}
}
