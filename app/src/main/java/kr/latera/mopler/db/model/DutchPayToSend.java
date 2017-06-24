package kr.latera.mopler.db.model;

import io.realm.RealmObject;

/**
 * Created by Jinwoo Shin on 2017-05-30.
 */

public class DutchPayToSend extends RealmObject
{
	private long id;
	private long date;
	private String title;
	private int amount;
	private boolean payed;

	public String getTitle() {return this.title;}
	public long getId() {return this.id;}
	public long getDate() {return this.date;}
	public int getAmount() {return this.amount;}
	public boolean isPayed() {return this.payed;}

	public void setTitle(String title) {this.title = title;}
	public void setId(long id) {this.id = id;}
	public void setDate(long date) {this.date = date;}
	public void setAmount(int amount) {this.amount = amount;}
	public void setPayed(boolean payed) {this.payed = payed;}
}
