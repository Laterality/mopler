package kr.latera.mopler.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import kr.latera.mopler.Logger;
import kr.latera.mopler.R;
import kr.latera.mopler.Utility;
import kr.latera.mopler.adapters.PayListAdapter;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.IDBManager;
import kr.latera.mopler.db.TransactionCallback;
import kr.latera.mopler.db.model.DutchPayToReceive;
import kr.latera.mopler.db.model.DutchPayToSend;
import kr.latera.mopler.db.model.Pay;
import kr.latera.mopler.dialog.DatePickerDialogFactory;
import kr.latera.mopler.dialog.EditPayDialog;

/**
 * Created by Jinwoo Shin on 2017-05-30.
 */

public class AddDutchPayActivity extends AppCompatActivity
		implements RadioGroup.OnCheckedChangeListener, View.OnClickListener
{
	private static final int TRANSACTION_CREATE_SEND = 1;
	private static final int TRANSACTION_CREATE_RECEIVE = 2;
	private static final int TRANSACTION_CREATE_PAY = 3;

	private static String TAG;

	@BindView(R.id.ll_dlg_add_dutchpay_group_num_of_people)
	LinearLayout llNumOfPeople;
	@BindView(R.id.ll_dlg_add_dutchpay_group_quotient)
	LinearLayout llQuotient;
	@BindView(R.id.cl_dlg_add_dutchpay_list_expander)
	ConstraintLayout clExpander;
	@BindView(R.id.lv_dlg_add_dutchpay_people)
	ListView lvPeople;
	@BindView(R.id.rg_dlg_add_dutchpay_send_receive)
	RadioGroup rgSendReceive;
	@BindView(R.id.rb_dlg_add_dutchpay_send)
	RadioButton rbSend;
	@BindView(R.id.rb_dlg_add_dutchpay_receive)
	RadioButton rbReceive;
	@BindView(R.id.et_dlg_add_dutchpay_title)
	EditText etTitle;
	@BindView(R.id.et_dlg_add_dutchpay_amount_total)
	EditText etAmount;
	@BindView(R.id.ibtn_dlg_add_dutchpay_set_date)
	ImageButton btnSetDate;
	@BindView(R.id.et_dlg_add_dutchpay_num_of_people)
	EditText etNumOfPeople;
	@BindView(R.id.et_dlg_add_dutchpay_quotient)
	EditText etQuotient;
	@BindView(R.id.btn_dlg_add_dutchpay_ok)
	Button btnOk;
	@BindView(R.id.btn_dlg_add_dutchpay_cancel)
	Button btnCancel;
	@BindView(R.id.tv_dlg_add_dutchpay_current_date)
	TextView tvCurrentDate;
//	@BindView(R.id.tv_dlg_add_dutchpay_amount_total)
//	TextView tvAmountTotal;
	@BindView(R.id.tv_dlg_add_dutchpay_title_error)
	TextView tvErrTitle;
	@BindView(R.id.tv_dlg_add_dutchpay_amount_error)
	TextView tvErrAmount;
	@BindView(R.id.tv_dlg_add_dutchpay_num_of_people_err)
	TextView tvErrNop;
	@BindView(R.id.tv_dlg_add_dutchpay_quotient_err)
	TextView tvErrQuotient;
	@BindView(R.id.iv_dlg_add_dutchpay_list_expander_icon)
	ImageView ivExpanderIcon;

	@BindDrawable(R.drawable.ic_keyboard_arrow_down_black_48dp)
	Drawable drwDown;
	@BindDrawable(R.drawable.ic_keyboard_arrow_up_black_48dp)
	Drawable drwUp;

	@BindString(R.string.text_msg_invalid_input)
	String msgInvalid;

	private AppCompatActivity mActivity;
	private boolean isSend;
	private boolean expanded;
	private DateTime date;
	private IDBManager dbm;
	private PayListAdapter mAdapter;


//	public AddDutchPayActivity(@NonNull Context context, @NonNull IDBManager dbm)
//	{
//		super(context);
//		init(context, dbm);
//	}


	private void init(Context c, IDBManager dbm)
	{
		TAG = getClass().getSimpleName();
		this.dbm = dbm;
		date = new DateTime();
		mActivity = c instanceof AppCompatActivity ? (AppCompatActivity) c : null;
		isSend = true;
		expanded = false;
		mAdapter = new PayListAdapter(c);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_dutchpay);

		ButterKnife.bind(this);

		init(this, DBManager.getInstance(this));

		lvPeople.setAdapter(mAdapter);
		lvPeople.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
			{
				Pay p = mAdapter.getItem(position);
				EditPayDialog dlg = new EditPayDialog(mActivity, p.getName(), p.getQuotient());
				dlg.setDialogListener(new EditPayDialog.DialogListener()
				{
					@Override
					public void onOk(String name, int amount, boolean received, boolean update)
					{
						if(update)
						{

						}
						else
						{
							mAdapter.setQuotient(position, amount);
							mAdapter.setName(position, name);
						}
					}

					@Override
					public void onCancel()
					{

					}
				});
				dlg.show();
			}
		});
		rgSendReceive.setOnCheckedChangeListener(this);
		etNumOfPeople.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if (validityCheck(false))
				{
					int total = Integer.valueOf(etAmount.getText().toString());
					int nop = Integer.valueOf(etNumOfPeople.getText().toString());
					int quotient = (total / nop) / 10 * 10;
					llQuotient.setVisibility(View.VISIBLE);
					etQuotient.setText(String.valueOf(quotient));
					mAdapter.setCount(nop);
					mAdapter.setQuotient(quotient);
					setExpandedState(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});

		tvCurrentDate.setText(Utility.getDateString(date));
		btnCancel.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnSetDate.setOnClickListener(this);
		clExpander.setOnClickListener(this);
	}

	private boolean validityCheck(boolean checkQuotient)
	{
		boolean ret = true;
		// TODO : check validity of user input
		if (etTitle.length() == 0)
		{
			tvErrTitle.setVisibility(View.VISIBLE);
			ret = false;
			Log.d(TAG, "validity:false-title");
		} else
		{
			tvErrTitle.setVisibility(View.GONE);
		}
		if (!Utility.checkNumber(etAmount.getText().toString()))
		{
			tvErrAmount.setVisibility(View.VISIBLE);
			ret = false;
			Log.d(TAG, "validity:false-amount");
		} else
		{
			tvErrAmount.setVisibility(View.GONE);
		}

		if (!isSend)
		{
			if (!Utility.checkNumber(etNumOfPeople.getText().toString()))
			{
				//				tvErrNop.setVisibility(View.VISIBLE);
				ret = false;
				Log.d(TAG, "validity:false-nop");
			} else
			{
				tvErrNop.setVisibility(View.GONE);
			}
			if (!Utility.checkNumber(etQuotient.getText().toString()) && checkQuotient)
			{
				tvErrQuotient.setVisibility(View.VISIBLE);
				ret = false;
				Log.d(TAG, "validity:false-quotient");
			} else
			{
				tvErrQuotient.setVisibility(View.GONE);
			}
		}
		//		Toast.makeText(mActivity, "validity: " + ret, Toast.LENGTH_SHORT).show();
		return ret;
	}


	/**
	 * It doesn't check validity of input values, they should be checked before called
	 */
	private void addDutchpay()
	{
		if (isSend)
		{
			dbm.createDutchPayToSend(etTitle.getText().toString(), date.getMillis(), Integer.valueOf(etAmount.getText().toString()), TRANSACTION_CREATE_SEND, new TransactionCallback<DutchPayToSend>()
			{
				@Override
				public void onTransactionCommitted(int transactionCode, RealmResults<DutchPayToSend> results)
				{
					Toast.makeText(mActivity, "추가되었습니다", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onTransactionCanceled(int transactionCode, Throwable error)
				{

				}
			});
		} else
		{
			dbm.createDutchPayToReceive(etTitle.getText().toString(), date.getMillis(),
					Integer.valueOf(etAmount.getText().toString()),
					TRANSACTION_CREATE_RECEIVE,
					new TransactionCallback<DutchPayToReceive>()
			{
				@Override
				public void onTransactionCommitted(int transactionCode, RealmResults<DutchPayToReceive> results)
				{
					TransactionCallback cb = new TransactionCallback<Pay>()
					{
						@Override
						public void onTransactionCommitted(int transactionCode, RealmResults<Pay> results)
						{
							Log.d(TAG, "Pay added");
						}

						@Override
						public void onTransactionCanceled(int transactionCode, Throwable error)
						{
							Logger.onError(error);
						}
					};
					Log.d(TAG, "Add pay: " + mAdapter.getCount());
					for (int i = 0; i < mAdapter.getCount(); i++)
					{
						Pay p = mAdapter.getItem(i);
						dbm.createPay(results.get(0).getId(), p.getName(), p.getQuotient(), TRANSACTION_CREATE_PAY, cb);
					}
					Toast.makeText(mActivity, "추가되었습니다", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onTransactionCanceled(int transactionCode, Throwable error)
				{

				}
			});
		}
	}


	@Override
	public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
	{
		if (checkedId == rbSend.getId())
		{
			llNumOfPeople.setVisibility(View.GONE);
			lvPeople.setVisibility(View.GONE);
			clExpander.setVisibility(View.GONE);
			isSend = true;
		} else if (checkedId == rbReceive.getId())
		{
			llNumOfPeople.setVisibility(View.VISIBLE);
			clExpander.setVisibility(View.VISIBLE);
			if (expanded)
			{
				lvPeople.setVisibility(View.VISIBLE);
			}
			isSend = false;
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.equals(btnSetDate))
		{
			if (mActivity == null)
			{
				return;
			}
			DatePickerDialogFactory d = new DatePickerDialogFactory();
			d.setListener(new DatePickerDialog.OnDateSetListener()
			{
				@Override
				public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
				{
					date = date.withDate(year, month + 1, dayOfMonth);
					tvCurrentDate.setText(Utility.getDateString(date));
				}
			});
			d.show(mActivity.getSupportFragmentManager(), "datePicker");
		} else if (v.equals(btnOk))
		{
			if (validityCheck(true))
			{
				addDutchpay();
				finish();
			}
		} else if (v.equals(btnCancel))
		{
			finish();
		} else if (v.equals(clExpander))
		{
			setExpandedState(!expanded);
		}
	} // onClick

	private void setExpandedState(boolean expanded)
	{
		this.expanded = expanded;

		if (expanded)
		{
			lvPeople.setVisibility(View.VISIBLE);
			ivExpanderIcon.setImageDrawable(drwUp);
		} else
		{
			lvPeople.setVisibility(View.GONE);
			ivExpanderIcon.setImageDrawable(drwDown);
		}

	}

}
