package ar.marquez.passme.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * An example database that records the state of each purchase. You should use
 * an obfuscator before storing any information to persistent storage. The
 * obfuscator should use a key that is specific to the device and/or user.
 * Otherwise an attacker could copy a database full of valid purchases and
 * distribute it to others.
 */
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

		if (mDb != null && mDb.isOpen() == false)
			mDb = mDatabaseHelper.getWritableDatabase();
	}

	public void init() {

	}

	public void close() {
		mDatabaseHelper.close();
	}

	public void copyDataBase() throws IOException {
		mDatabaseHelper.copyDataBase(mDb.getPath());
	}

	public void clearDataBase() {
		mDatabaseHelper.onUpgrade(mDb, 1, 2);
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
			AccountEntity newItem = null;
			Log.i(TAG, "getAccountList(): Adding stored accounts");

			while (cursor.moveToNext()) {
				newItem = new AccountEntity(cursor);
				result.add(newItem);
				Log.i(TAG, "getAccountList(): " + newItem.toString());
			}
		}

		if (cursor != null) {
			cursor.close();
		}

		return result;
	}

	/*
	 * public boolean setAccountList(ArrayList<AccountEntity> accountList) {
	 * boolean bResult = true;
	 * 
	 * clearDataBase();
	 * 
	 * for (Iterator<AccountEntity> it = accountList.iterator(); it.hasNext();)
	 * { AccountEntity account = (AccountEntity) it.next(); if
	 * (insertAccount(account) == false) { bResult = false; } }
	 * 
	 * return bResult; }
	 */

	public boolean insertAccount(AccountEntity account) {
		long result = -1L;

		ContentValues values = new ContentValues();
		values.put(AccountEntity.ACCOUNT_ACCOUNT_NAME, account.getAccountName());
		values.put(AccountEntity.ACCOUNT_USERNAME, account.getUserName());
		values.put(AccountEntity.ACCOUNT_PASSWORD, account.getPassword());
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
		values.put(AccountEntity.ACCOUNT_PASSWORD, account.getPassword());
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
		boolean bResult = true;

		try {
			cursor = mDb.query(AccountEntity.ACCOUNT_TABLE_NAME,
					AccountEntity.ACCOUNT_COLUMNS,
					AccountEntity.ACCOUNT_ACCOUNT_NAME + "='" + accountAccountName
							+ "'", null, null, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			bResult = false;
		}

		Log.i(TAG, "cursor.getCount(): " + String.valueOf(cursor.getCount()));
		if (bResult == true && cursor.getCount() > 0) {
			cursor.moveToNext();
			AccountEntity ret = new AccountEntity(cursor);
			return ret;
		}

		if (cursor != null) {
			cursor.close();
		}

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

		/**
		 * Copies your database from your local assets-folder to the just
		 * created empty database in the system folder, from where it can be
		 * accessed and handled. This is done by transfering bytestream.
		 * */
		private void copyDataBase(String dbFile) throws IOException {
			InputStream myInput = new FileInputStream(dbFile);

			File xmlFile = null; // path generator.
			xmlFile = new File("db.xml");
			OutputStream myOutput = new FileOutputStream(xmlFile);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}
	}
}
