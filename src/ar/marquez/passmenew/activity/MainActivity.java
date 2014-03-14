package ar.marquez.passmenew.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ar.marquez.passmenew.R;
import ar.marquez.passmenew.adapter.AccountAdapter;
import ar.marquez.passmenew.model.AccountEntity;
import ar.marquez.passmenew.model.Consts;

import static ar.marquez.passmenew.model.PassMeApplication.getAccountManager;
import static ar.marquez.passmenew.model.PassMeApplication.getModel;
import static ar.marquez.passmenew.model.PassMeApplication.getPrefs;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	private ListView accountList;
	private AccountAdapter accountAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handleControls();

		initView();
	}

	private void handleControls() {
		accountList = (ListView) findViewById(R.id.accountList);
	}

	private void initView() {
		accountAdapter = new AccountAdapter(this,
				new ArrayList<AccountEntity>(), this);
		accountList.setAdapter(accountAdapter);

		if (isFirstTimeApp()) {
			buildFirstTimeDialog();
		} else if (getModel().mIsLocked)
			buildValidationDialog();
		else
			fillAccountList();
	}

	private boolean isFirstTimeApp() {
		boolean ret;

		ret = getPrefs().getBoolean(Consts.FIRST_TIME_APP,
				true);
		if (ret) {
			Log.i(TAG, "isFirstTimeApp()");

			Editor editor = getPrefs().edit();
			editor.putBoolean(Consts.FIRST_TIME_APP, false);
			editor.commit();
		}

		return ret;
	}

	public void fillAccountList() {
		ArrayList<AccountEntity> accountListItems = getAccountManager().getAllAccounts();

		Log.i(TAG, "fillAccountList(): " + accountListItems.size()
				+ " items on database");

		if (accountListItems != null && accountListItems.size() != 0) {
			accountAdapter.clear();
			accountAdapter.addAll(accountListItems);
			accountAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		Log.i(TAG, "onOptionsItemSelected(): selected item " + title);

		// Settings button
		if (title.equals(getString(R.string.action_settings))) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		// Add account button
		else if (title
				.equals(getString(R.string.action_add_pass))) {
			buildAddAccountDialog();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void buildFirstTimeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_first_time, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton(R.string.accept,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Log.i(TAG,
										"onClick() from buildFirstTimeDialog()");

								EditText edtPassword = (EditText) view
										.findViewById(R.id.edtPassword);
								EditText edtConfirmPassword = (EditText) view
										.findViewById(R.id.edtConfirmPassword);

								if (edtPassword
										.getText()
										.toString()
										.equals(edtConfirmPassword.getText()
												.toString())) {
                                        getModel().saveMasterPassword(edtPassword.getText().toString());
								} else {
									AlertDialog myAlertDialog;
									AlertDialog.Builder builder = new AlertDialog.Builder(
											MainActivity.this);
									builder.setMessage(
											getString(R.string.msg_pass_must_match))
											.setPositiveButton(
													R.string.accept,
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int arg1) {
															dialog.dismiss();
															buildFirstTimeDialog();
														}
													})
											.setCancelable(false)
											.setTitle(
													getString(R.string.title_alert));
									myAlertDialog = builder.create();
									myAlertDialog.show();
								}

							}
						}).setCancelable(false)
				.setTitle(getString(R.string.title_first_time));
		builder.create().show();

	}

	private void buildValidationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_login, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton(R.string.login,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Log.i(TAG, "buildValidationDialog()");

								EditText edtPassword = (EditText) view
										.findViewById(R.id.edtPassword);

								String pass = getModel().getMasterPassword();
								if (pass.equals(edtPassword.getText()
										.toString())) {
									fillAccountList();
								} else {
									AlertDialog myAlertDialog;
									AlertDialog.Builder builder = new AlertDialog.Builder(
											MainActivity.this);
									builder.setMessage(getString(R.string.msg_wrong_pass));
									builder.setPositiveButton(
											R.string.accept,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int arg1) {
													dialog.dismiss();
													initView();
												}
											});
									builder.setCancelable(false);
									myAlertDialog = builder.create();
									myAlertDialog.show();
								}
							}
						}).setCancelable(false)
				.setTitle(getString(R.string.title_login));
		builder.create().show();
	}

	private void buildAddAccountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_add, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton(R.string.create,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Log.i(TAG, "buildAddAccountDialog()");

								AccountEntity accountEnt = new AccountEntity();

								EditText edtAccountName = (EditText) view
										.findViewById(R.id.edtAccountName);
								accountEnt.setAccountName(edtAccountName
										.getText().toString());

								EditText edtUsername = (EditText) view
										.findViewById(R.id.edtUsername);
								accountEnt.setUserName(edtUsername.getText()
										.toString());

								EditText edtPassword = (EditText) view
										.findViewById(R.id.edtPassword);
								accountEnt.setPassword(edtPassword.getText()
										.toString());

								EditText edtDetails = (EditText) view
										.findViewById(R.id.edtDetails);
								accountEnt.setDetail(edtDetails.getText()
										.toString());

								if (!getAccountManager()
                                        .addAccount(accountEnt)) {
									buildConfirm2Dialog(accountEnt);
								} else {
									fillAccountList();
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						}).setCancelable(false)
				.setTitle(getString(R.string.action_add_pass));
		builder.create().show();
	}

	public void buildDeleteDialog(final AccountEntity entity) {
		Log.i(TAG, "buildDeleteDialog()");
		final CharSequence[] items = { getString(R.string.delete),
				getString(R.string.cancel) };
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					buildConfirmDialog(entity);
				}
			}
		}).setCancelable(false)
				.setTitle("Cuenta \"" + entity.getAccountName() + "\"");
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void buildConfirmDialog(final AccountEntity entity) {
		Log.i(TAG, "buildConfirmDialog()");
		DialogInterface.OnClickListener updateDialog = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					getAccountManager().remove(entity);
					fillAccountList();
					buildOKDialog(entity);
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"�Est� seguro que desea eliminar la cuenta \""
						+ entity.getAccountName()
						+ "\" de la lista de cuentas?")
				.setPositiveButton(R.string.accept, updateDialog)
				.setNegativeButton(R.string.cancel, updateDialog)
				.setCancelable(false).show();
	}

	private void buildConfirm2Dialog(final AccountEntity entity) {
		Log.i(TAG, "buildConfirmDialog()");
		DialogInterface.OnClickListener updateDialog = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					buildAddAccountDialog();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"No se pudo crear la cuenta \"" + entity.getAccountName()
						+ "\" ya que existe otra con el mismo nombre.")
				.setPositiveButton(R.string.edit, updateDialog)
				.setNegativeButton(R.string.cancel, updateDialog)
				.setCancelable(false).show();
	}

	private void buildOKDialog(AccountEntity entity) {
		Log.i(TAG, "buildOKDialog()");
		AlertDialog myAlertDialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Se ha borrado la cuenta \""
				+ entity.getAccountName() + "\" de la lista de cuentas.");
		builder.setPositiveButton("Aceptar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				}).setCancelable(false);
		myAlertDialog = builder.create();
		myAlertDialog.show();
	}

	public void btnDetailsClick() {
		startActivity(new Intent(this, DetailActivity.class));
	}

	@Override
	public void onStop() {
		super.onStop();
		getModel().mIsLocked = true;
	}
}
