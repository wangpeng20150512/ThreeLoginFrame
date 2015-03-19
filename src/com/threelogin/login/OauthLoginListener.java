package com.threelogin.login;

import com.threelogin.bean.AuthToken;
import com.threelogin.bean.AuthUser;

public interface OauthLoginListener {
	public void OauthLoginSuccess(AuthToken token, AuthUser user);

	public void OauthLoginFail();

}
