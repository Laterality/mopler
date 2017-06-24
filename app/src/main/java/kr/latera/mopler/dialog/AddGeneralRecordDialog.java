package kr.latera.mopler.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import kr.latera.mopler.Logger;
import kr.latera.mopler.R;
import kr.latera.mopler.SettingManager;
import kr.latera.mopler.Utility;
import kr.latera.mopler.db.DBManager;
import kr.latera.mopler.db.TransactionCallback;
import kr.latera.mopler.db.model.Plan_Daily;
import kr.latera.mopler.db.model.Record_General;

/**
 * Created by Jinwoo Shin on 2017-04-25.
 */

public class AddGeneralRecordDialog extends Dialog implements View.OnClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener
{
	private static String TAG;

	private static final int TRANSACTION_INSERTION_GENERAL_RECORD = 1;
	private static final int TRANSACTION_INSERTION_DAILY_PLAN = 2;

	@BindView(R.id.ll_dlg_add_general_record_where_err)
	LinearLayout llErrWhere;
	@BindView(R.id.ll_dlg_add_general_record_amount_err)
	LinearLayout llErrAmount;
	@BindView(R.id.btn_dlg_add_general_record_ok)
	Button btnOk;
	@BindView(R.id.btn_dlg_add_general_record_cancel)
	Button btnCancel;
	@BindView(R.id.btn_dlg_add_general_record_set_date)
	ImageButton btnSetDate;
	@BindView(R.id.tv_dlg_add_general_record)
	TextView tvDate;
//	@BindView(R.id.tv_dlg_add_general_record_where)
//	TextView tvWhere;
	@BindView(R.id.rb_dlg_add_general_record_income)
	AppCompatRadioButton rbIncome;
	@BindView(R.id.rb_dlg_add_general_record_outgo)
	AppCompatRadioButton rbOutgo;
	@BindView(R.id.rg_dlg_add_general_record_income_outgo)
	RadioGroup rgSelection;
	@BindView(R.id.et_dlg_add_general_record_where)
	EditText etWhere;
	@BindView(R.id.et_dlg_add_general_record_content)
	EditText etContent;
	@BindView(R.id.et_dlg_add_general_record_amount)
	EditText etAmount;

	private Animation animBlinkError;
	private Animation animAppearing;
	//
	@BindString(R.string.string_where_gain)
	String strWhereIncome;
	@BindString(R.string.string_where_spent)
	String strWhereOutgo;
	@BindString(R.string.text_msg_has_been_input)
	String strHasInput;
	@BindString(R.string.text_msg_error_occurred)
	String strHasError;
	@BindString(R.string.string_dialog_add_record_title)
	String strTitle;
	private boolean isIncome;

	private final String initTitle;
	private final int initAmount;
	private final long initDate;


	private DateTime date;

	private AppCompatActivity mActivity;

	public AddGeneralRecordDialog(@NonNull Context context)
	{
		super(context, R.style.DialogTheme);
		initTitle = "";
		initAmount = 0;
		initDate = 0;
		init(context);
	}

	public AddGeneralRecordDialog(@NonNull Context context, String initTitle, int initAmount,
	                              long date)
	{
		super(context, R.style.DialogTheme);
		this.initTitle = initTitle;
		this.initAmount = initAmount;
		this.initDate = date;
		init(context);
	}

	private void init(@NonNull Context context)
	{
		isIncome = false;
		mActivity = context instanceof AppCompatActivity ? (AppCompatActivity) context : null;
		animBlinkError = AnimationUtils.loadAnimation(context, R.anim.anim_blink);
		animAppearing = AnimationUtils.loadAnimation(context, R.anim.anim_appering);
	}

	/**
	 * Check validity of Input values
	 *
	 * @return true if All Inputs are valid, else false
	 */
	private boolean validityCheck()
	{
		boolean ret = true;

		if(etWhere.getText().length() == 0)
		{
			ret = false;
			llErrWhere.startAnimation(animAppearing);
			llErrWhere.setVisibility(View.VISIBLE);
			etWhere.startAnimation(animBlinkError);
		}
		else
		{
			llErrWhere.setVisibility(View.GONE);
		}

//		if(etContent.getText().length() == 0)
//		{
//			ret = false;
//			etContent.startAnimation(animBlinkError);
//		}

		if(etAmount.getText().length() == 0)
		{
			ret = false;
			llErrAmount.setVisibility(View. VISIBLE);
			etAmount.startAnimation(animBlinkError);
		}
		else if(Utility.checkNumber(etAmount.getText().toString()))
		{
			// when the input is not number
		}
		else
		{
			llErrAmount.setVisibility(View.GONE);
		}

		return ret;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_add_general_record);

		TAG = this.getClass().getSimpleName();

		ButterKnife.bind(this);
		setTitle(strTitle);
		if(!initTitle.isEmpty()) {etWhere.setText(initTitle);}
		if(initAmount != 0) {etAmount.setText(String.valueOf(initAmount));}

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSetDate.setOnClickListener(this);
		rgSelection.setOnCheckedChangeListener(this);

		if(initDate == 0) {date = new DateTime();}
		else{date = new DateTime(initDate);}
		tvDate.setText(Utility.getDateString(date));
//		Log.d(TAG, "Add Record Dialog opened, Date : " + Utility.getDateString(date));
	}

	@Override
	public void onClick(View v)
	{
		if(v.equals(btnOk))
		{
			if(validityCheck())
			{
				final int amount = Integer.valueOf(etAmount.getText().toString());
				final DBManager dbm = DBManager.getInstance(mActivity);
				if(dbm.retrieveDailyPlan(date) == null)
				{
					dbm.createDailyPlan(date, SettingManager.getInstance(mActivity).getInt(SettingManager.INT_SETTING_BUDGET), TRANSACTION_INSERTION_DAILY_PLAN, new TransactionCallback<Plan_Daily>()
					{
						@Override
						public void onTransactionCommitted(int transactionCode, RealmResults<Plan_Daily> results)
						{
							dbm.createGeneralRecord(mActivity,
									etWhere.getText().toString(), etContent.getText().toString(), isIncome ? amount : -amount, date, TRANSACTION_INSERTION_GENERAL_RECORD, new TransactionCallback<Record_General>()
									{
										@Override
										public void onTransactionCommitted(int transactionCode, RealmResults<Record_General> results)
										{
											Toast.makeText(mActivity, strHasInput, Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onTransactionCanceled(int transactionCode, Throwable error)
										{
											Toast.makeText(mActivity, strHasError, Toast.LENGTH_SHORT).show();
											Logger.onError(error);
										}
									});
							dismiss();
						}

						@Override
						public void onTransactionCanceled(int transactionCode, Throwable error)
						{
							Logger.onError(error);
						}
					});
				}
				else
				{
					dbm.createGeneralRecord(mActivity,
							etWhere.getText().toString(), etContent.getText().toString(), isIncome ? amount : -amount, date, TRANSACTION_INSERTION_GENERAL_RECORD, new TransactionCallback<Record_General>()
							{
								@Override
								public void onTransactionCommitted(int transactionCode, RealmResults<Record_General> results)
								{
									Toast.makeText(mActivity, strHasInput, Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onTransactionCanceled(int transactionCode, Throwable error)
								{
									Toast.makeText(mActivity, strHasError, Toast.LENGTH_SHORT).show();
									Logger.onError(error);
								}
							});
					dismiss();
				}
			}
		}
		else if(v.equals(btnCancel))
		{
			cancel();
		}
		else if(v.equals(btnSetDate))
		{
			if(mActivity == null) {return;}
			DatePickerDialogFactory d = new DatePickerDialogFactory();
			d.setListener(new DatePickerDialog.OnDateSetListener()
			{
				@Override
				public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
				{
					date = date.withDate(year, month + 1, dayOfMonth);
					tvDate.setText(Utility.getDateString(date));
				}
			});
			d.show(mActivity.getSupportFragmentManager(), "datePicker");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{

	}

	@Override
	public void afterTextChanged(Editable s)
	{

	}

	@Override
	public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
	{
		if(checkedId == rbIncome.getId())
		{
			isIncome = true;
			etWhere.setHint(strWhereIncome);
		}
		else if(checkedId == rbOutgo.getId())
		{
			isIncome = false;
			etWhere.setHint(strWhereOutgo);
		}
	}
}
