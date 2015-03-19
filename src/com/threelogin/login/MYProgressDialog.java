package com.threelogin.login;

import com.threelogin.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class MYProgressDialog extends Dialog {

	private TextView mMessage;

	public MYProgressDialog(Context context) {
		super(context, R.style.my_progress_dialog);
		init();
	}

	private void init() {
		getWindow().setGravity(Gravity.CENTER);
		setContentView(R.layout.my_progress_dialog);
		setCanceledOnTouchOutside(false);

		mMessage = (TextView) this.findViewById(R.id.dialog_message);
	}

	public void setMessage(String message) {
		mMessage.setText(message);
	}

	public void hideMessage() {
		mMessage.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
		}
	}

}
