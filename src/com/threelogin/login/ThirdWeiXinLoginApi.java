package com.threelogin.login;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.threelogin.bean.WeiXinToken;
import com.threelogin.bean.WeiXinUserInfo;
import com.threelogin.utils.HttpUrlUtils;
import com.threelogin.utils.LoginPlatForm;
import com.threelogin.utils.ThirdAppKey;

public class ThirdWeiXinLoginApi {

	private static IWXAPI mWXAPI;

	public static void registerToWeiXin(Context ctx) {
		mWXAPI = WXAPIFactory.createWXAPI(ctx, ThirdAppKey.WEIXIN_APPID, true);
		mWXAPI.registerApp(ThirdAppKey.WEIXIN_APPID);
		Log.v("wxapi", mWXAPI + "");
	}

	public static void login(Context ctx) {
	
		if (!isWXAPPInstalled(ctx)) {
			Toast.makeText(ctx, "请安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!mWXAPI.isWXAppSupportAPI()) {
			Toast.makeText(ctx, "请升级微信客户端，不支持api！", Toast.LENGTH_SHORT).show();
			return;
		}
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = ThirdAppKey.WEIXIN_SCOPE;
		req.state = "none";
		req.openId = "";
		mWXAPI.sendReq(req);
	}

	//获取微信实例
	public static IWXAPI getWXAPI(Context ctx) {
		if (mWXAPI == null) {
			registerToWeiXin(ctx);
		}
		return mWXAPI;
	}

	public static boolean isWXAPPInstalled(Context ctx) {
		if (mWXAPI == null) {
			return false;
		}
		boolean result = mWXAPI.isWXAppInstalled();
		if (result) {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				try {
					ApplicationInfo info = packageManager.getApplicationInfo(
							"com.tencent.mm", PackageManager.GET_META_DATA);
					if (info != null) {
						result = true;
					}
				} catch (PackageManager.NameNotFoundException e) {
					result = false;
				}
			}
		}
		return result;
	}

	public static void getOauthAcces(String code, OauthLoginListener oauth) {
		String url = "https://api.weixin.qq.com/" + "sns/oauth2/access_token?"
				+ "appid=" + ThirdAppKey.WEIXIN_APPID + "&secret="
				+ ThirdAppKey.WEIXINA_PPSECRET + "&code=" + code
				+ "&grant_type=authorization_code";
		String data = HttpUrlUtils.httpClientGetJson(url);
		try {
			JSONObject obj = new JSONObject(data);
			if (obj != null) {
				Log.v("weixin-token", obj.toString());

				WeiXinToken token = new WeiXinToken();
				token.access_token = obj.getString("access_token");
				token.expires_in = obj.getString("expires_in");
				token.refresh_token = obj.getString("refresh_token");
				token.openid = obj.getString("openid");
				token.scope = obj.getString("scope");

				if (token != null && token.access_token != null
						&& token.openid != null) {
					if (getUserInfo(token, oauth)) {
						return;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		oauth.OauthLoginFail();
	}

	public static boolean getUserInfo(WeiXinToken token,
			OauthLoginListener oauth) {
		String url = "https://api.weixin.qq.com/sns/userinfo?" + "openid="
				+ token.openid + "&access_token=" + token.access_token;
		String data = HttpUrlUtils.httpClientGetJson(url);

		try {
			JSONObject obj = new JSONObject(data);
			Log.v("weixin-user", obj.toString());
			WeiXinUserInfo user = new WeiXinUserInfo();
			user.unionid = obj.getString("unionid");
			user.openid = obj.getString("openid");
			user.nickname = obj.getString("nickname");
			if (user != null && user.unionid != null && user.openid != null) {
				user.authtype = LoginPlatForm.WECHAT_PLATPORM;
				token.authtype = LoginPlatForm.WECHAT_PLATPORM;
				oauth.OauthLoginSuccess(token, user);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
