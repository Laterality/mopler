package kr.latera.mopler.db.model;

import org.joda.time.DateTime;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Jinwoo Shin on 2017-05-30.
 */

public class Pay extends RealmObject
{
	@Ignore
	public static final long DATE_NOT_PAYED = -1;
	private long id;
	private long payId;
//	private Person person;
	private String name;
	private int quotient;
	private boolean payed;
	private long datePayed;

	public long getId() {return id;}
	public long getDutchPayId() {return this.payId;}
//	public Person getPerson() {return this.person;}
	public String getName() { return this.name; }
	public int getQuotient() {return this.quotient;}
	public boolean isPayed() {return this.payed;}
	public long getPayedDate() {return this.datePayed;}


	public void setId(long id) {this.id = id;}
	public void setPay(long payId) {this.payId = payId;}
//	public void setPerson(Person p) {this.person = p;}
	public void setName(String n) {this.name = n;}
	public void setQuotient(int quotient) {this.quotient = quotient;}
	public void setPayed(boolean payed) {datePayed = new DateTime().getMillis(); this.payed = payed;}
}
