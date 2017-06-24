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

/**
 * Created by Jinwoo Shin on 2017-06-16.
 */

public class DutchPayToSendEditDialog extends Dialog
	implements View.OnClickListener
{
	private DialogListener mListener;

	@BindView(R.id.et_dlg_edit_dutchpay_to_send_name)
	EditText etName;
	@BindView(R.id.et_dlg_edit_dutchpay_to_send_amount)
	EditText etAmount;
	@BindView(R.id.btn_dlg_edit_dutchpay_to_send_ok)
	Button btnOk;
	@BindView(R.id.btn_dlg_edit_dutchpay_to_send_cancel)
	Button btnCancel;
	@BindView(R.id.cb_dlg_edit_dutchpay_to_send_received)
	CheckBox cbSent;

	private final String initName;
	private final int initAmount;
	private final boolean initSent;

	public DutchPayToSendEditDialog(@NonNull Context context, String name, int amount, boolean sent)
	{
		super(context, R.style.DialogTheme);
		initName = name;
		initAmount = amount;
		initSent = sent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit_dutchpay_to_send);
		ButterKnife.bind(this);

		etName.setText(initName);
		etAmount.setText(String.valueOf(initAmount));
		cbSent.setChecked(initSent);

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		if(v.equals(btnOk))
		{
			if(mListener != null)
			{
				// TODO : check validity of input values first
				mListener.onOk(etName.getText().toString(),
						Integer.valueOf(etAmount.getText().toString()),
						cbSent.isChecked());
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
		public void onOk(String title, int amount, boolean sent);
		public void onCancel();
	}

}
