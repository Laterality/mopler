package kr.latera.mopler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.latera.mopler.R;
import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.db.model.Pay;
import kr.latera.mopler.db.model.Person;

/**
 * Created by Jinwoo Shin on 2017-06-14.
 */

public class PayListAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater inflater;
	private List<Pay> item;
	private RealmResults<Pay> rItem;
	private String strMoneyUnit;
	private String strDefaultName;
	private String strReceived;
	private String strNotReceived;
	private boolean showPayed;
	private IDBManager dbm;

	public PayListAdapter(Context c)
	{
		mContext = c;
		inflater = LayoutInflater.from(c);
		item = new ArrayList<>();
		strMoneyUnit = c.getResources().getString(R.string.string_money_unit);
		strDefaultName = c.getResources().getString(R.string.string_name_default);
		showPayed = false;
	}

	public PayListAdapter(Context c, long dutchPayId, boolean showPayed, IDBManager dbm)
	{
		mContext = c;
		inflater = LayoutInflater.from(c);
		item = new ArrayList<>();
		strMoneyUnit = c.getResources().getString(R.string.string_money_unit);
		strDefaultName = c.getResources().getString(R.string.string_name_default);
		strReceived = c.getResources().getString(R.string.string_received);
		strNotReceived = c.getResources().getString(R.string.string_not_received);
		this.showPayed = showPayed;
		this.dbm = dbm;
		rItem = dbm.retrievePayByDutchPay(dutchPayId);
		rItem.addChangeListener(new RealmChangeListener<RealmResults<Pay>>()
		{
			@Override
			public void onChange(RealmResults<Pay> element)
			{
				item = rItem;
				notifyDataSetChanged();
			}
		});
		item = rItem;
	}

	public void setCount(int count)
	{
		int size = item.size();
		if(size == count){return;}
		else if(size > count)
		{
			// remove items
			int d = size - count;
			for(int i = 0; i < d; i++)
			{
				item.remove(item.size() - 1); // from end
			}
		}
		else
		{
			// add items
			int d = count - size;
			for(int i = 0 ; i < d; i++)
			{
				Pay p = new Pay();
				p.setName(strDefaultName);
				p.setQuotient(0);
				p.setPayed(false);
				item.add(p);
			}
		}
		Toast.makeText(mContext, "set Count : " + count, Toast.LENGTH_SHORT).show();
		notifyDataSetChanged();
	}

	public void setQuotient(int quotient)
	{
		for(Pay p : item)
		{
			if(p.getQuotient() == 0)
			{
				p.setQuotient(quotient);
			}
		}
		notifyDataSetChanged();
	}

	public void setQuotient(int position, int quotient)
	{
		if(item.size() <= position
				|| position < 0
				|| quotient < 0) {return;}
		else
		{
			item.get(position).setQuotient(quotient);
		}
		notifyDataSetChanged();
	}

	public void setName(int position, String name)
	{
		if(item.size() <= position
				|| position < 0
				|| name.length() == 0) {return;}
		else
		{
			item.get(position).setName(name);
		}
		notifyDataSetChanged();
	}


	@Override
	public int getCount()
	{
		return item.size();
	}

	@Override
	public Pay getItem(int position)
	{
		return item.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.item_list_pay, null);
			holder= new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		Pay p = item.get(position);
		holder.tvName.setText(p.getName());
		holder.tvQuotient.setText(p.getQuotient() + strMoneyUnit);
		holder.tvPayed.setText(p.isPayed() ? strReceived : strNotReceived);

		if(showPayed) {holder.tvPayed.setVisibility(View.VISIBLE);}
		else{holder.tvPayed.setVisibility(View.GONE);}


		return convertView;
	}

	class ViewHolder
	{
		@BindView(R.id.tv_item_list_pay_name)
		TextView tvName;
		@BindView(R.id.tv_item_list_pay_quotient)
		TextView tvQuotient;
		@BindView(R.id.tv_item_list_pay_payed)
		TextView tvPayed;

		public ViewHolder(View v)
		{
			ButterKnife.bind(this, v);
		}
	}
}
