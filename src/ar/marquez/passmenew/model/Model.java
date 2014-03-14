package ar.marquez.passmenew.model;

import android.content.SharedPreferences;

import static ar.marquez.passmenew.model.PassMeApplication.getPrefs;

public class Model {
	public AccountEntity mSelectedAccountEntity;

	public boolean mIsLocked;
	
	public void init() {
		mSelectedAccountEntity = null;
		
		mIsLocked = true;
	}

    public void saveMasterPassword(String password) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(Consts.MASTER_PASSWORD, password);
        editor.commit();
    }

    public String getMasterPassword() {
        return getPrefs().getString(Consts.MASTER_PASSWORD, "");
    }
}
