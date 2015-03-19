package com.threelogin.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.threelogin.login.ThirdWeiXinLoginApi;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ThirdWeiXinLoginApi.getWXAPI(getApplicationContext()).handleIntent(
				getIntent(), this);
	}

	@Override
	public void onReq(com.tencent.mm.sdk.modelbase.BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {

		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
			String code = ((SendAuth.Resp) resp).code;
			if (code != null) {
				Log.v("code:", code);
				Intent action = new Intent();
				action.setAction("ACTION_WX_LOGIN_SUCEESS");
				action.putExtra("code", code);

				LocalBroadcastManager.getInstance(this).sendBroadcast(action);
				finish();
				return;
			}
		}
		
		
		finish();
		
	}
}
