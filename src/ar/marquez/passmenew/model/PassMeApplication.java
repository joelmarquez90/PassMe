package ar.marquez.passmenew.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PassMeApplication extends Application {

	// Application context
	private static Context stContext = null;

	// DashboardManager singleton
	private static AccountManager accountManager = null;

	// Persist method.
	private static SharedPreferences prefs;

	// Model
	private static Model model;

	@Override
	public void onCreate() {
		super.onCreate();

		// Initalize context
		stContext = this.getBaseContext();

		getAccountManager().init(stContext);

		getModel().init();

		// Initalize SharedPreferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public static Model getModel() {
		if (model == null)
			model = new Model();
		return model;
	}

	public static Context getContext() {
		return stContext;
	}

	public static AccountManager getAccountManager() {
		if (accountManager == null)
			accountManager = new AccountManager();
		return accountManager;
	}

	static public SharedPreferences getPrefs() {
		return prefs;
	}
}
