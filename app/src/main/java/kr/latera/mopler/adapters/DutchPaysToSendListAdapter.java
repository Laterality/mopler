package kr.latera.mopler.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import kr.latera.mopler.db.model.DutchPayToSend;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class DutchPaysToSendListAdapter extends BaseAdapter
	implements RealmChangeListener<RealmResults<DutchPayToSend>>
{
	private Context mContext;
	private LayoutInflater inflater;
	private RealmResults<DutchPayToSend> item;
	private IDBManager dbm;

	private final String strSent;
	private final String strNotSent;

	public DutchPaysToSendListAdapter(@NonNull Context context, IDBManager dbm)
	{
		mContext = context;
		inflater = LayoutInflater.from(context);
		this.dbm = dbm;

		item = dbm.retrieveDutchPayToSend();
		item.addChangeListener(this);

		strSent = context.getResources().getString(R.string.string_sent);
		strNotSent = context.getResources().getString(R.string.string_not_sent);
	}


	@Override
	public int getCount()
	{
		return item == null ? 0 : item.size();
	}

	@Override
	public DutchPayToSend getItem(int position)
	{
		return item == null ? null : item.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return item == null ? 0 :
				(position < item.size() ?
						item.get(position).getId() : 0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.item_dutchpay_to_send, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		DutchPayToSend dp = item.get(position);

		holder.tvTitle.setText(dp.getTitle());
		holder.tvAmount.setText(String.valueOf(dp.getAmount()));
		holder.tvSent.setText(dp.isPayed() ? strSent : strNotSent);

		return convertView;
	}

	@Override
	public void onChange(RealmResults<DutchPayToSend> element)
	{
		notifyDataSetChanged();
	}

	class ViewHolder
	{
		@BindView(R.id.tv_item_dutchpay_to_send_title)
		TextView tvTitle;
		@BindView(R.id.tv_item_dutchpay_to_send_amount)
		TextView tvAmount;
		@BindView(R.id.tv_item_dutchpay_to_send_sent)
		TextView tvSent;

		ViewHolder(View v)
		{
			ButterKnife.bind(this, v);
		}
	}
}
