package kr.latera.mopler.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.latera.mopler.R;
import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.db.model.DutchPayToReceive;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class DutchPaysToReceiveListAdapter extends BaseAdapter
		implements RealmChangeListener<RealmResults<DutchPayToReceive>>
{
	private Context mContext;
	private LayoutInflater inflater;
	private IDBManager dbm;
	private RealmResults<DutchPayToReceive> item;

	public DutchPaysToReceiveListAdapter(@NonNull Context context, IDBManager dbm)
	{
		mContext = context;
		inflater = LayoutInflater.from(context);
		this.dbm = dbm;

		// Load data from db
		item = dbm.retrieveDutchPayToReceive();
		item.addChangeListener(this);
	}


	@Override
	public int getCount()
	{
		return item == null ? 0 : item.size();
	}

	@Override
	public DutchPayToReceive getItem(int position)
	{
		return item == null ? null : item.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		if(item == null) {return -1;}
		else if(position >= item.size()) {return -1;}
		else
		{
			return item.get(position).getId();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.item_dutchpay_to_receive, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		DutchPayToReceive dp = item.get(position);

		holder.tvTitle.setText(dp.getTitle());
		holder.tvAmount.setText(String.valueOf(dp.getTotal()));

		return convertView;
	}

	@Override
	public void onChange(RealmResults<DutchPayToReceive> element)
	{
		notifyDataSetChanged();
	}


	class ViewHolder
	{
		@BindView(R.id.tv_item_dutchpay_to_receive_title)
		TextView tvTitle;
		@BindView(R.id.tv_item_dutchpay_to_receive_amount)
		TextView tvAmount;

		public ViewHolder(View source)
		{
			ButterKnife.bind(this, source);
		}
	}
}
