package kr.latera.mopler.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import kr.latera.mopler.Logger;
import kr.latera.mopler.SettingManager;
import kr.latera.mopler.Utility;
import kr.latera.mopler.db.model.DutchPayToReceive;
import kr.latera.mopler.db.model.DutchPayToSend;
import kr.latera.mopler.db.model.Pay;
import kr.latera.mopler.db.model.Person;
import kr.latera.mopler.db.model.Plan_Daily;
import kr.latera.mopler.db.model.Record_General;

/**
 * Created by Jinwoo Shin on 2017-04-25.
 *
 * Managing about db execution
 *
 * Inserting operation is executed asynchronously
 */
public class DBManager implements IDBManager
{
	private static String TAG;
	private static DBManager dbm;

	public static DBManager getInstance(Context context)
	{
		if(dbm == null)
		{
			dbm = new DBManager(context);
		}
		return dbm;
	}

	private DBManager(Context c)
	{
		TAG = getClass().getSimpleName();
		Realm.init(c);
		// empty constructor
	}


	/**
	 *
	 * @param where
	 * @param content
	 * @param amount
	 * @param date
	 * @param transactionCode number to identify transaction
	 * @param callback callback for transaction
	 *
	 * Use callback(non-null)
	 */
	@Override
	public void createGeneralRecord(final Context c,
			final String where, final String content,
	                                final int amount, final DateTime date,
	                                final int transactionCode,
	                                @NonNull final TransactionCallback<Record_General> callback)
	{
		final Realm realm = Realm.getDefaultInstance();
		realm.executeTransactionAsync(new Realm.Transaction()
		{
			@Override
			public void execute(final Realm realm)
			{
				// 1. check if there is daily plan for the date
				if(retrieveDailyPlan(date) == null)
				{
					// 2-1. if not, cancel the transaction
					Logger.onError("Daily plan does not exists");
					realm.cancelTransaction();
				}
				else
				{
					// 2-2. if so, start transaction
					Logger.onReport(String.format(Utility.LOCALE, "created date : %s", date.toString() ));
					Number n = realm.where(Record_General.class).max("id");
					int nextID = n == null ? 1 : n.intValue() + 1;
					Record_General rg = realm.createObject(Record_General.class, nextID);
					rg.setWhere(where);
					rg.setContent(content);
					rg.setAmount(amount);
					rg.setDate(date.getMillis());
				}

			}
		}, new Realm.Transaction.OnSuccess()
		{
			@Override
			public void onSuccess()
			{
				DateTime d = new DateTime(date);
				callback.onTransactionCommitted(transactionCode,
						realm.where(Record_General.class)
								.between("date", d.withTimeAtStartOfDay().getMillis(),
										d.plusDays(1).withTimeAtStartOfDay().getMillis())
								.findAllSorted("id"));
			}
		}, new Realm.Transaction.OnError()
		{
			@Override
			public void onError(Throwable error)
			{
				callback.onTransactionCanceled(transactionCode, error);
			}
		});
		//return rg;
	}

	@Override
	public RealmResults<Record_General> retrieveRecordByMonth(DateTime date)
	{
		final Realm realm = Realm.getDefaultInstance();
		RealmQuery<Record_General> query = realm.where(Record_General.class);
		DateTime dateFrom = date.withTimeAtStartOfDay().dayOfMonth().setCopy(1);
		DateTime dateTo = dateFrom.plusMonths(1);

		RealmResults res = query
				.between("date", dateFrom.getMillis(), dateTo.getMillis())
				.findAllSorted("date", Sort.ASCENDING);
		Logger.onReport(String.format(Utility.LOCALE, "retrieve records...date : %s, from : %s, to : %s",
				date.toString(), dateFrom.toString(), dateTo.toString() ));
		Logger.onReport(String.format(Utility.LOCALE, "results : %d", res.size()));
		return res;
	}


	@Override
	public void createDailyPlan(final DateTime d, final int budget, final int transactionCode,
	                            @NonNull final TransactionCallback<Plan_Daily> callback)
	{
		final Realm realm = Realm.getDefaultInstance();
		realm.executeTransactionAsync(new Realm.Transaction()
		{
			@Override
			public void execute(Realm realm)
			{
				if(retrieveDailyPlan(d) != null)
				{
					Logger.onReport("Daily Plan already exists, cancelling transaction");
					realm.cancelTransaction();
				}
				else
				{
					Number n = realm.where(Plan_Daily.class).max("id");
					int nextID = n == null ? 1 : n.intValue() + 1;
					Plan_Daily dp = realm.createObject(Plan_Daily.class, nextID);
					dp.setDate(d.getMillis());
					dp.setBudget(budget);
				}
			}
		}, new Realm.Transaction.OnSuccess()
		{
			@Override
			public void onSuccess()
			{
				callback.onTransactionCommitted(transactionCode, realm.where(Plan_Daily.class)
						.equalTo("date", d.getMillis()).findAll());
			}
		}, new Realm.Transaction.OnError()
		{
			@Override
			public void onError(Throwable error)
			{
				callback.onTransactionCanceled(transactionCode, error);
			}
		});
	}

	/**
	 * @return Null if the plan of the day does not exist
	 *
	 */
	@Override
	public Plan_Daily retrieveDailyPlan(DateTime d)
	{
		final Realm realm = Realm.getDefaultInstance();
		DateTime dt = d.withTimeAtStartOfDay();
		RealmResults<Plan_Daily> results = realm.where(Plan_Daily.class).between("date",
				dt.getMillis(), dt.plusDays(1).getMillis()).findAll();
		if(results.isEmpty())
		{
			return null;
		}
		return results.get(0);
	}

	@Override
	public RealmResults<Plan_Daily> retrieveDailyPlanByMonth(DateTime d)
	{
		final Realm realm = Realm.getDefaultInstance();
		DateTime dt = d.withTimeAtStartOfDay();
		DateTime from = dt.dayOfMonth().setCopy(1); // first day of the month
//		DateTime to = dt.plusDays(1);
		DateTime to = from.plusMonths(1); // first day of the next month
		Logger.onReport(String.format(Utility.LOCALE,
				"retrieve daily plans...current: %s, Date between from %s to %s",
				d.toString(), from.toString(), to.toString()
				));
		return realm.where(Plan_Daily.class)
//				.between("date",
//						dt.dayOfMonth().setCopy(1).getMillis(),
//						dt.plusDays(1).getMillis())
				.between("date",
						from.getMillis(), to.getMillis())
				.findAllSorted("date", Sort.DESCENDING);
	}

	public void reportCounts()
	{
		final Realm realm = Realm.getDefaultInstance();
		Logger.onReport(String.valueOf("plans: " + realm.where(Plan_Daily.class).count()));
		Logger.onReport(String.valueOf("records: " + realm.where(Record_General.class).count()));
		Logger.onReport(String.valueOf("dutch pays: tr " + realm.where(DutchPayToReceive.class).count())+
		", ts " + realm.where(DutchPayToReceive.class).count());
	}


	@Override
	public void createDutchPayToReceive(final String title, final long date, final int total,
	                                    final int transactionCode,
	                                    @NonNull final TransactionCallback<DutchPayToReceive> callback)
	{
		final Realm realm = Realm.getDefaultInstance();
		Number n = realm.where(DutchPayToReceive.class).max("id");
		final int id = n == null ? 1 : n.intValue() + 1;
		realm.executeTransactionAsync(new Realm.Transaction()
		{
			@Override
			public void execute(Realm realm)
			{
				DutchPayToReceive dtr = realm.createObject(DutchPayToReceive.class);
				dtr.setId(id);
				dtr.setDate(date);
				dtr.setTitle(title);
				dtr.setTotal(total);
			}
		}, new Realm.Transaction.OnSuccess()
		{
			@Override
			public void onSuccess()
			{
				callback.onTransactionCommitted(transactionCode,
						realm.where(DutchPayToReceive.class).equalTo("id", id).findAll());
			}
		}, new Realm.Transaction.OnError()
		{
			@Override
			public void onError(Throwable error)
			{
				callback.onTransactionCanceled(transactionCode, error);
			}
		});
	}

	@Override
	public void createPay(@NonNull final long payId,
	                      /*@NonNull final Person person*/
	                      @NonNull final String name, final int quotient,
	                      final int transactionCode,
	                      @NonNull final TransactionCallback<Pay> callback)
	{
		final Realm realm = Realm.getDefaultInstance();
		realm.executeTransactionAsync(new Realm.Transaction()
		{
			@Override
			public void execute(Realm realm)
			{

				Number n = realm.where(Pay.class).max("id");
				Pay p = realm.createObject(Pay.class);
				p.setId(n == null ? 1 : n.intValue() + 1);
				p.setPay(payId);
//				p.setPerson(person);
				p.setName(name);
				p.setPayed(false);
				p.setQuotient(quotient);
			}
		}, new Realm.Transaction.OnSuccess()
		{
			@Override
			public void onSuccess()
			{
				callback.onTransactionCommitted(transactionCode, realm.where(Pay.class)
						.equalTo("payId", payId)
						.findAll());
				Log.d(TAG, "pay added");
			}
		}, new Realm.Transaction.OnError()
		{
			@Override
			public void onError(Throwable error)
			{
				callback.onTransactionCanceled(transactionCode, error);
			}
		});
	}

	@Override
	public void createDutchPayToSend(final String title, final long date, final int amount,
	                                    final int transactionCode,
	                                    @NonNull final TransactionCallback<DutchPayToSend> callback)
	{
		final Realm realm = Realm.getDefaultInstance();
		Number n = realm.where(DutchPayToSend.class).max("id");
		final int id = n == null ? 1 : n.intValue() + 1;
		realm.executeTransactionAsync(new Realm.Transaction()
		{
			@Override
			public void execute(Realm realm)
			{
				DutchPayToSend dts = realm.createObject(DutchPayToSend.class);
				dts.setId(id);
				dts.setDate(date);
				dts.setTitle(title);
				dts.setAmount(amount);
			}
		}, new Realm.Transaction.OnSuccess()
		{
			@Override
			public void onSuccess()
			{
				callback.onTransactionCommitted(transactionCode,
						realm.where(DutchPayToSend.class).equalTo("id", id).findAll());
			}
		}, new Realm.Transaction.OnError()
		{
			@Override
			public void onError(Throwable error)
			{
				callback.onTransactionCanceled(transactionCode, error);
			}
		});
	}

	@Override
	public RealmResults<DutchPayToReceive> retrieveDutchPayToReceive()
	{
		final Realm realm = Realm.getDefaultInstance();
		return realm.where(DutchPayToReceive.class).findAllSorted("date", Sort.DESCENDING);
	}

	@Override
	public RealmResults<DutchPayToSend> retrieveDutchPayToSend()
	{
		final Realm realm = Realm.getDefaultInstance();
		return realm.where(DutchPayToSend.class).findAllSorted("date", Sort.DESCENDING);
	}

	@Override
	public DutchPayToReceive retrieveDutchPayById(long id)
	{
		final Realm realm = Realm.getDefaultInstance();
		return realm.where(DutchPayToReceive.class).equalTo("id", id).findFirst();
	}

	@Override
	public RealmResults<Pay> retrievePayByDutchPay(long dutchPayId)
	{
		final Realm realm = Realm.getDefaultInstance();
		Logger.onReport("Pays: " + realm.where(Pay.class).count());
		return realm.where(Pay.class).equalTo("payId", dutchPayId).findAll();
	}

	@Override
	public void updatePay(long payId, String name, int quotient, boolean received)
	{
		final Realm realm = Realm.getDefaultInstance();
		realm.beginTransaction();
		Pay p = realm.where(Pay.class).equalTo("id", payId).findFirst();
		p.setName(name);
		p.setQuotient(quotient);
		p.setPayed(received);
		realm.commitTransaction();
	}

	@Override
	public void updateDutchPayToSend(long id, String title, int amount, boolean sent)
	{
		final Realm realm = Realm.getDefaultInstance();
		realm.beginTransaction();
		DutchPayToSend dp = realm.where(DutchPayToSend.class).equalTo("id", id).findFirst();
		dp.setTitle(title);
		dp.setAmount(amount);
		dp.setPayed(sent);
		realm.commitTransaction();
	}
}
