package ar.marquez.passmenew.model;

import android.database.Cursor;

import ar.marquez.passmenew.database.PasswordUtil;

import static ar.marquez.passmenew.model.PassMeApplication.getModel;

public class AccountEntity {
	// DATABASE DEFINITION
	static public final String ACCOUNT_TABLE_NAME = "account";
	static public final String ACCOUNT_ACCOUNT_NAME = "accountname";
	static public final String ACCOUNT_USERNAME = "username";
	static public final String ACCOUNT_PASSWORD = "password";
	static public final String ACCOUNT_DETAIL = "detail";

	static public final String[] ACCOUNT_COLUMNS = {ACCOUNT_ACCOUNT_NAME,
			ACCOUNT_USERNAME, ACCOUNT_PASSWORD, ACCOUNT_DETAIL};

	private String accountName;
	private String userName;
	private String password;
	private String detail;

	public AccountEntity(String accountName, String userName, String password,
			String detail) {
		this.accountName = accountName;
		this.userName = userName;
		this.password = password;
		this.detail = detail;
	}

	public AccountEntity(Cursor cursor) {
		this.accountName = cursor.getString(0);
		this.userName = cursor.getString(1);
		this.password = cursor.getString(2);
		this.detail = cursor.getString(3);
	}

	public AccountEntity() {
		this("", "", "", "");
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "Username: " + userName + ", Password: " + password;
	}
}
