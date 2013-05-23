package ar.marquez.passme;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import ar.marquez.passme.model.AccountEntity;
import ar.marquez.passme.model.PassMeApplication;

// -------- ADAPTER CLASS ---------  
// --------------------------------

public class AccountAdapter extends ArrayAdapter<AccountEntity> {
	Context context = null;
	ArrayList<AccountEntity> data = null;
	MainActivity activity = null;
	int _selectedIndex = -1;

	public AccountAdapter(Context context, ArrayList<AccountEntity> data,
			MainActivity activity) {
		super(context, R.layout.list_accounts_row, data);
		this.context = context;
		this.data = data;
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AccountEntity accountItem = data.get(position);

		AccountHolder accountHolder = new AccountHolder();
		View accountRow = convertView;

		if (accountRow == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			accountRow = inflater.inflate(R.layout.list_accounts_row, parent,
					false);

			accountHolder.lblUserName = (TextView) accountRow
					.findViewById(R.id.lblUserName);
			accountHolder.lblAccountName = (TextView) accountRow
					.findViewById(R.id.lblAccountName);
			accountHolder.btnDetails = (Button) accountRow
					.findViewById(R.id.btnDetails);

			accountRow.setTag(accountHolder);
		} else
			accountHolder = (AccountHolder) accountRow.getTag();

		accountHolder.lblAccountName.setText(accountItem.getAccountName());
		accountHolder.lblUserName.setText(accountItem.getUserName());

		accountHolder.btnDetails
				.setOnClickListener(new AccountAdapterOnClickListener(this,
						position, accountItem));
		accountRow.setOnClickListener(new AccountAdapterOnClickListener(this,
				position, accountItem));
		accountRow
				.setOnLongClickListener(new AccountAdapterOnLongClickListener(
						this, position, accountItem));

		return accountRow;
	}

	static class AccountHolder {
		TextView lblAccountName;
		TextView lblUserName;
		Button btnDetails;
	}

	class AccountAdapterOnLongClickListener implements OnLongClickListener {
		private final AccountAdapter adapter;
		private final int index;
		private final AccountEntity entity;

		public AccountAdapterOnLongClickListener(AccountAdapter adapter,
				int index, AccountEntity entity) {
			this.adapter = adapter;
			this.index = index;
			this.entity = entity;
		}

		@Override
		public boolean onLongClick(View arg0) {
			// Avoid update if click on the same item
			if (index != -1 && adapter != null) {
				adapter._selectedIndex = index;
				notifyDataSetChanged();

				activity.buildDeleteDialog(entity);
			}
			return true;
		}
	}

	class AccountAdapterOnClickListener implements OnClickListener {
		private final AccountAdapter adapter;
		private final int index;
		private final AccountEntity entity;

		public AccountAdapterOnClickListener(AccountAdapter adapter, int index,
				AccountEntity entity) {
			this.adapter = adapter;
			this.index = index;
			this.entity = entity;
		}

		@Override
		public void onClick(View arg0) {
			// Avoid update if click on the same item
			if (index != -1 && adapter != null) {
				adapter._selectedIndex = index;
				notifyDataSetChanged();

				activity.btnDetailsClick(entity);

				PassMeApplication.getModel().mSelectedAccountEntity = entity;
			}
		}
	}
}
