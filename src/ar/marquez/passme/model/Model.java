package ar.marquez.passme.model;

public class Model {
	public AccountEntity mSelectedAccountEntity;

	public boolean mIsLocked;
	
	public void init() {
		mSelectedAccountEntity = null;
		
		mIsLocked = true;
	}

}
