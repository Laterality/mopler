package kr.latera.mopler.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.latera.mopler.R;
import kr.latera.mopler.Utility;

/**
 * Created by Jinwoo Shin on 2017-06-15.
 */

public class EditPayDialog extends Dialog implements View.OnClickListener
{

	private DialogListener mListener;

	@BindView(R.id.et_dlg_edit_pay_name)
	EditText etName;
	@BindView(R.id.et_dlg_edit_pay_amount)
	EditText etAmount;
	@BindView(R.id.btn_dlg_edit_pay_ok)
	Button btnOk;
	@BindView(R.id.btn_dlg_edit_pay_cancel)
	Button btnCancel;
	@BindView(R.id.cb_dlg_edit_pay_received)
	CheckBox cbReceived;

	private String initName;
	private int initAmount;
	private boolean showCheckbox;
	private boolean received;


	public EditPayDialog(@NonNull Context context, String name, int amount)
	{
		super(context, R.style.DialogTheme);
		initName = name;
		initAmount = amount;
		showCheckbox = false;
		received = false;
	}

	public EditPayDialog(@NonNull Context context, String name, int amount,
	                     boolean received, boolean showCheckbox)
	{
		super(context, R.style.DialogTheme);
		initName = name;
		initAmount = amount;
		this.showCheckbox = showCheckbox;
		this.received = received;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit_pay);

		ButterKnife.bind(this);

		if(showCheckbox){cbReceived.setVisibility(View.VISIBLE);}
		cbReceived.setChecked(received);

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		etName.setText(initName);
		etAmount.setText(String.valueOf(initAmount));
	}

	private boolean validityCheck()
	{
		boolean ret = true;
		if(!Utility.checkNumber(etAmount.getText().toString()))
		{
			ret = false;
		}

		return ret;
	}

	@Override
	public void onClick(View v)
	{
		if(v.equals(btnOk) && validityCheck())
		{
			if(mListener != null)
			{
				mListener.onOk(etName.getText().toString(),
						Integer.valueOf(etAmount.getText().toString()), cbReceived.isChecked(),
						showCheckbox);
			}
			dismiss();
		}
		else if(v.equals(btnCancel))
		{
			if(mListener != null)
			{
				mListener.onCancel();
			}
			cancel();
		}
	}

	public void setDialogListener(DialogListener listener)
	{
		mListener = listener;
	}

	public interface DialogListener
	{
		public void onOk(String name, int amount, boolean received, boolean update);
		public void onCancel();
	}
}
