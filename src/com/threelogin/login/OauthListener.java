package com.threelogin.login;

public interface OauthListener {
	public void OauthSuccess(Object obj);

	public void OauthFail(Object type);

	public void OauthCancel(Object type);
}
