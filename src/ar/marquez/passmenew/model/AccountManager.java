package ar.marquez.passmenew.model;

import java.util.ArrayList;
import android.content.Context;

import ar.marquez.passmenew.database.DataBaseManager;

public class AccountManager {

	private DataBaseManager dbManager;

	public AccountManager() {
	}

	public boolean addAccount(AccountEntity account) {
		return dbManager.insertAccount(account);
	}

	public ArrayList<AccountEntity> getAllAccounts() {
		return dbManager.getAccountList();
	}

	public void init(Context context) {
		dbManager = new DataBaseManager(context);
		dbManager.testOpen();
	}

	public void remove(AccountEntity entity) {
		dbManager.removeAccount(entity);
	}

	public void updateAccount(AccountEntity entity) {
		dbManager.updateAccount(entity);
	}

	public AccountEntity getAccount(String accountUserName) {
		return dbManager.getAccount(accountUserName);
	}
}
