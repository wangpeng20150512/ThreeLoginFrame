package com.threelogin.login;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.threelogin.bean.QQToken;
import com.threelogin.bean.QQUserInfo;
import com.threelogin.utils.LoginPlatForm;
import com.threelogin.utils.ThirdAppKey;

public class ThirdQQLoginApi {

	private static Tencent mTencent;

	public static Tencent getTencent(Context context) {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(ThirdAppKey.QQ_APPID, context);
		}
		return mTencent;
	}

	public static void login(final Activity activity,
			final OauthListener listener, final OauthLoginListener oauth) {
		// && !mTencent.isSessionValid()
		if (mTencent != null) {
			// 本地配置文件注销
			mTencent.logout(activity);
			mTencent.login(activity, ThirdAppKey.QQ_SCOPE, new IUiListener() {

				@Override
				public void onError(UiError arg0) {
					listener.OauthFail(arg0);
				}

				@Override
				public void onComplete(Object arg0) {

					JSONObject obj = (JSONObject) arg0;
					Log.v("qq:", obj.toString());
					try {
						QQToken token = new QQToken();
						token.setAccess_token(obj.getString("access_token"));
						token.setPay_token(obj.getString("pay_token"));
						token.setOpenid(obj.getString("openid"));
						token.setExpires_in(obj.getString("expires_in"));

						if (token != null && token.getAccess_token() != null) {
							listener.OauthSuccess(token);
							mTencent.setOpenId(token.getOpenid());
							mTencent.setAccessToken(token.getAccess_token(),
									token.getExpires_in() + "");
							ThirdQQLoginApi.getUserInfo(activity, oauth, token);
						}
					} catch (Exception e) {
						listener.OauthFail(null);
					}
				}

				@Override
				public void onCancel() {
					listener.OauthCancel(null);
				}
			});
		}
	}

	public static void getUserInfo(final Activity activity,
			final OauthLoginListener oauth, final QQToken token) {
		UserInfo info = new UserInfo(activity, mTencent.getQQToken());
		info.getUserInfo(new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				oauth.OauthLoginFail();
			}

			@Override
			public void onComplete(Object arg0) {
				try {
					JSONObject obj = (JSONObject) arg0;
					Log.v("qq-user", obj.toString());
					QQUserInfo info = new QQUserInfo();
					info.gender = obj.getString("gender");
					info.nickname = obj.getString("nickname");
					info.figureurl_qq_1 = obj.getString("figureurl_qq_1");
					info.figureurl_qq_2 = obj.getString("figureurl_qq_2");
					if (info != null && info.nickname != null) {
						token.authtype = LoginPlatForm.QQZONE_PLATPORM;
						info.authtype = LoginPlatForm.QQZONE_PLATPORM;
						oauth.OauthLoginSuccess(token, info);
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				oauth.OauthLoginFail();
			}

			@Override
			public void onCancel() {
				oauth.OauthLoginFail();
			}
		});
	}
}
