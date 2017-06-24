package kr.latera.mopler.db;

import android.content.Context;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import io.realm.RealmResults;
import kr.latera.mopler.db.model.DutchPayToReceive;
import kr.latera.mopler.db.model.DutchPayToSend;
import kr.latera.mopler.db.model.Pay;
import kr.latera.mopler.db.model.Person;
import kr.latera.mopler.db.model.Plan_Daily;
import kr.latera.mopler.db.model.Record_General;

/**
 * Created by Jinwoo Shin on 2017-05-31.
 */

public interface IDBManager
{
	public void createGeneralRecord(final Context c,
	                                final String where, final String content,
	                                final int amount, final DateTime date,
	                                final int transactionCode,
	                                @NonNull final TransactionCallback<Record_General> callback);

	public RealmResults<Record_General> retrieveRecordByMonth(DateTime date);

	public void createDailyPlan(final DateTime d, final int budget, final int transactionCode,
	                            @NonNull final TransactionCallback<Plan_Daily> callback);

	public Plan_Daily retrieveDailyPlan(DateTime d);

	public RealmResults<Plan_Daily> retrieveDailyPlanByMonth(DateTime d);

	public void createDutchPayToReceive(final String title, final long date, final int total,
	                                    final int transactionCode,
	                                    @NonNull final TransactionCallback<DutchPayToReceive> callback);

	public void createPay(@NonNull final long payId,
//	                      @NonNull final Person person,
                          @NonNull final String name,
	                      final int quotient,
	                      final int transactionCode,
	                      @NonNull final TransactionCallback<Pay> callback);

	public RealmResults<Pay> retrievePayByDutchPay(long dutchPayId);

	public void updatePay(long payId, String name, int quotient, boolean received);

	public void createDutchPayToSend(final String title, final long date, final int amount,
	                                 final int transactionCode,
	                                 @NonNull final TransactionCallback<DutchPayToSend> callback);

	public RealmResults<DutchPayToReceive> retrieveDutchPayToReceive();

	public DutchPayToReceive retrieveDutchPayById(long id);

	public RealmResults<DutchPayToSend> retrieveDutchPayToSend();

	public void updateDutchPayToSend(long id, String title, int amount, boolean sent);


}
