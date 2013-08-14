package ar.marquez.passme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ar.marquez.passme.model.AccountEntity;
import ar.marquez.passme.model.PassMeApplication;

import static ar.marquez.passme.model.PassMeApplication.getModel;

public class DataBaseManager {
	public static final String TAG = "DataBaseManager";

	private static final String DATABASE_NAME = "passme.db";
	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase mDb = null;
	private DatabaseHelper mDatabaseHelper = null;

	public DataBaseManager(Context context) {
		mDatabaseHelper = new DatabaseHelper(context);
		mDb = mDatabaseHelper.getWritableDatabase();
		Log.i(TAG, "Creating DataBaseManager");
	}

	public void testOpen() {
		if (mDatabaseHelper == null)
			mDatabaseHelper = new DatabaseHelper(PassMeApplication.getContext());

		if (mDb != null && !mDb.isOpen())
			mDb = mDatabaseHelper.getWritableDatabase();
	}

	public ArrayList<AccountEntity> getAccountList() {
		ArrayList<AccountEntity> result = new ArrayList<AccountEntity>();

		Cursor cursor = null;

		try {
			cursor = mDb
					.query(AccountEntity.ACCOUNT_TABLE_NAME,
							AccountEntity.ACCOUNT_COLUMNS, null, null, null,
							null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			result = null;
		}

		if (result != null && cursor.getCount() > 0) {
			AccountEntity newItem;
			Log.i(TAG, "getAccountList(): Adding stored accounts");

			while (cursor.moveToNext()) {
				newItem = new AccountEntity(cursor);
                newItem.setPassword(PasswordUtil.decryptWrapper(getModel().getMasterPassword(), newItem.getPassword()));
				result.add(newItem);
				Log.i(TAG, "getAccountList(): " + newItem.toString());
			}
		}

		if (cursor != null) {
			cursor.close();
		}

		return result;
	}

	public boolean insertAccount(AccountEntity account) {
		long result = -1L;

		ContentValues values = new ContentValues();
		values.put(AccountEntity.ACCOUNT_ACCOUNT_NAME, account.getAccountName());
		values.put(AccountEntity.ACCOUNT_USERNAME, account.getUserName());
		values.put(AccountEntity.ACCOUNT_PASSWORD, encrypt(account.getPassword()));
		values.put(AccountEntity.ACCOUNT_DETAIL, account.getDetail());

		try {
			result = mDb.insertOrThrow(AccountEntity.ACCOUNT_TABLE_NAME, null,
					values);
			Log.i(TAG, "insertAccount(): " + values.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result != -1L;
	}

    private String encrypt(String password) {
        return PasswordUtil.encryptWrapper(getModel().getMasterPassword(), password);
    }

    public void removeAccount(AccountEntity account) {
		try {
			Log.i(TAG, "removeAccount(): " + account.toString());

			mDb.delete(
					AccountEntity.ACCOUNT_TABLE_NAME,
					AccountEntity.ACCOUNT_ACCOUNT_NAME + "='"
							+ account.getAccountName() + "'", null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateAccount(AccountEntity account) {
		Log.i(TAG, "updateAccount(): " + account.toString());
		Log.i(TAG,
				"where " + AccountEntity.ACCOUNT_ACCOUNT_NAME + "='"
						+ account.getAccountName() + "'");

		ContentValues values = new ContentValues();
		values.put(AccountEntity.ACCOUNT_PASSWORD, encrypt(account.getPassword()));
		values.put(AccountEntity.ACCOUNT_DETAIL, account.getDetail());

		try {
			mDb.update(
					AccountEntity.ACCOUNT_TABLE_NAME,
					values,
					AccountEntity.ACCOUNT_ACCOUNT_NAME + "='"
							+ account.getAccountName() + "'", null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public AccountEntity getAccount(String accountAccountName) {
		Log.i(TAG, "getAccount(): where " + AccountEntity.ACCOUNT_ACCOUNT_NAME
				+ "='" + accountAccountName + "'");

		Cursor cursor = null;

        try {
			cursor = mDb.query(AccountEntity.ACCOUNT_TABLE_NAME,
					AccountEntity.ACCOUNT_COLUMNS,
					AccountEntity.ACCOUNT_ACCOUNT_NAME + "='" + accountAccountName
							+ "'", null, null, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
        }

        assert cursor != null;
        Log.i(TAG, "cursor.getCount(): " + String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
            return new AccountEntity(cursor);
		}

        cursor.close();

        return null;
	}

	/**
	 * This is a standard helper class for constructing the database.
	 */
	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createAccountsTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion != DATABASE_VERSION) {
				db.execSQL("DROP TABLE IF EXISTS "
						+ AccountEntity.ACCOUNT_TABLE_NAME);
				createAccountsTable(db);
			}
		}

		private void createAccountsTable(SQLiteDatabase db) throws SQLException {
			db.execSQL("CREATE TABLE " + AccountEntity.ACCOUNT_TABLE_NAME + "("
					+ AccountEntity.ACCOUNT_ACCOUNT_NAME + " TEXT PRIMARY KEY, "
					+ AccountEntity.ACCOUNT_USERNAME + " TEXT, "
					+ AccountEntity.ACCOUNT_PASSWORD + " TEXT, "
					+ AccountEntity.ACCOUNT_DETAIL + " TEXT)");
		}
	}
}
