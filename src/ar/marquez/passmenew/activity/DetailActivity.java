package ar.marquez.passmenew.activity;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ar.marquez.passmenew.R;
import ar.marquez.passmenew.model.AccountEntity;
import ar.marquez.passmenew.model.PassMeApplication;

public class DetailActivity extends Activity {
	public static final String TAG = "DetailActivity";

	private TextView lblAccountNameData;
	private TextView lblUsernameData;
	private TextView lblPasswordData;
	private TextView lblDetailsData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		handleControls();

		setDataSource();

		initView();
	}

	private void handleControls() {
		lblAccountNameData = (TextView) findViewById(R.id.lblAccountNameData);
		lblUsernameData = (TextView) findViewById(R.id.lblUsernameData);
		lblPasswordData = (TextView) findViewById(R.id.lblPasswordData);
		lblDetailsData = (TextView) findViewById(R.id.lblDetailsData);

		ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setDataSource() {
		AccountEntity accEnt = PassMeApplication.getModel().mSelectedAccountEntity;
		if (accEnt != null) {
			lblAccountNameData.setText(accEnt.getAccountName());
			lblUsernameData.setText(accEnt.getUserName());
			lblPasswordData.setText(accEnt.getPassword());
			lblDetailsData.setText(accEnt.getDetail());
		}
	}

	private void initView() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	private void buildEditAccountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AccountEntity accountEnt = PassMeApplication.getModel().mSelectedAccountEntity;
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_edit, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				// Add action buttons
				.setPositiveButton(R.string.accept,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Log.i(TAG,
										"onClick() from buildEditAccountDialog()");

                                assert view != null;
                                EditText edtPassword = (EditText) view
										.findViewById(R.id.edtPassword);
								accountEnt.setPassword(edtPassword.getText()
										.toString());

								EditText edtDetails = (EditText) view
										.findViewById(R.id.edtDetails);
								accountEnt.setDetail(edtDetails.getText()
										.toString());

								PassMeApplication.getAccountManager()
										.updateAccount(accountEnt);

								refresh();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setTitle(getString(R.string.title_edit_account) + " \"" + accountEnt.getAccountName() + "\"");
		builder.create().show();
	}

	private void refresh() {
		Log.i(TAG, "refresh()");

		AccountEntity selectedAccount = PassMeApplication.getModel().mSelectedAccountEntity;
		AccountEntity refreshedAccount = PassMeApplication.getAccountManager()
				.getAccount(selectedAccount.getAccountName());

		lblAccountNameData.setText(refreshedAccount.getAccountName());
		lblUsernameData.setText(refreshedAccount.getUserName());
		lblPasswordData.setText(refreshedAccount.getPassword());
		lblDetailsData.setText(refreshedAccount.getDetail());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		if (item.getItemId() == android.R.id.home) {
			PassMeApplication.getModel().mIsLocked = false;
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		// edit button
		if (title
				.equals(getString(R.string.action_edit_account))) {
			buildEditAccountDialog();
			return true;
		}

		else

			return super.onOptionsItemSelected(item);
	}
}
