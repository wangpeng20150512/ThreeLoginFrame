package com.threelogin.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.threelogin.R;
import com.threelogin.bean.AuthToken;
import com.threelogin.bean.AuthUser;
import com.threelogin.bean.QQToken;
import com.threelogin.bean.QQUserInfo;
import com.threelogin.bean.WeiBoToken;
import com.threelogin.bean.WeiBoUserInfo;
import com.threelogin.bean.WeiXinUserInfo;
import com.threelogin.login.MYProgressDialog;
import com.threelogin.login.OauthListener;
import com.threelogin.login.OauthLoginListener;
import com.threelogin.login.ThirdQQLoginApi;
import com.threelogin.login.ThirdWeiBoLoginApi;
import com.threelogin.login.ThirdWeiXinLoginApi;
import com.threelogin.utils.LoginPlatForm;

public class ThirdLoginActivity extends FragmentActivity {
	private MyReceiver mReceiver;
	public MYProgressDialog pd;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pd = new MYProgressDialog(this);
		if (mReceiver != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					mReceiver);
			mReceiver = null;
		}
		mReceiver = new MyReceiver();
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
				new IntentFilter("ACTION_WX_LOGIN_SUCEESS"));

		ViewInit();

	}

	private void ViewInit() {
		Button qq = (Button) findViewById(R.id.qq);
		Button wb = (Button) findViewById(R.id.weibo);
		Button wx = (Button) findViewById(R.id.weixin);
		qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				QQInit();
			}
		});
		wx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WeiXinInit();
			}

		});

		wb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WeiBoInit();
			}
		});
	}

	private void WeiXinInit() {
		ThirdWeiXinLoginApi.getWXAPI(getApplicationContext());
		ThirdWeiXinLoginApi.login(getApplicationContext());
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		mReceiver = null;
		super.onDestroy();
	}

	private void QQInit() {
		ThirdQQLoginApi.getTencent(getApplicationContext());
		ThirdQQLoginApi.login(this, oauth, oauthlogin);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ThirdQQLoginApi.getTencent(this) != null) {
			ThirdQQLoginApi.getTencent(this).onActivityResult(requestCode,
					resultCode, data);
		}

		if (ThirdWeiBoLoginApi.getSsoHandler(this) != null) {
			ThirdWeiBoLoginApi.getSsoHandler(this).authorizeCallBack(
					requestCode, resultCode, data);
		}
	}

	public void WeiBoInit() {
		ThirdWeiBoLoginApi.getSsoHandler(this);
		ThirdWeiBoLoginApi.login(this, oauth, oauthlogin);

	}

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if ("ACTION_WX_LOGIN_SUCEESS".equals(intent.getAction())) {
				final String code = intent.getStringExtra("code");
				pd.setMessage("正在为你登录");
				pd.show();
				new Thread(new Runnable() {

					@Override
					public void run() {
						ThirdWeiXinLoginApi.getOauthAcces(code, oauthlogin);
					}
				}).start();
			}
		}
	};

	private OauthLoginListener oauthlogin = new OauthLoginListener() {

		@Override
		public void OauthLoginSuccess(final AuthToken token, final AuthUser user) {

			handler.post(new Runnable() {

				@Override
				public void run() {

					String uuid = "";
					String name = "";
					int type = token.authtype;
					switch (type) {
					case LoginPlatForm.QQZONE_PLATPORM:
						uuid = ((QQToken) token).getOpenid();
						name = ((QQUserInfo) user).getNickname();
						break;

					case LoginPlatForm.WECHAT_PLATPORM:
						uuid = ((WeiXinUserInfo) user).getUnionid();
						name = ((WeiXinUserInfo) user).getNickname();
						break;
					case LoginPlatForm.WEIBO_PLATPORM:
						uuid = ((WeiBoToken) token).getUid();
						name = ((WeiBoUserInfo) user).getName();

						break;
					}
					pd.setMessage("登录成功:" + uuid + "===" + name);
                    
				}
			});
		}

		@Override
		public void OauthLoginFail() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					pd.setMessage("登录失败");
				}
			});

		}
	};

	private OauthListener oauth = new OauthListener() {

		@Override
		public void OauthSuccess(Object obj) {

			pd.setMessage("正在为你登录");
			pd.show();
		}

		@Override
		public void OauthFail(Object type) {
			Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OauthCancel(Object type) {
			Toast.makeText(getApplicationContext(), "取消授权", Toast.LENGTH_SHORT)
					.show();
		}
	};
}
