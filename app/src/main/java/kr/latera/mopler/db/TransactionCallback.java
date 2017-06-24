package kr.latera.mopler.db;

import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Jinwoo Shin on 2017-05-07.
 */

public interface TransactionCallback<T extends RealmModel>
{
	public void onTransactionCommitted(int transactionCode, RealmResults<T> results);
	public void onTransactionCanceled(int transactionCode, Throwable error);
}
