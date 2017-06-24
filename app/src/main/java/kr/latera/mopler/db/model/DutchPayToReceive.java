package kr.latera.mopler.db.model;

import io.realm.RealmObject;

/**
 * Created by Jinwoo Shin on 2017-05-30.
 */

public class DutchPayToReceive extends RealmObject
{

	private long id;
	private long date;
	private String title;
	private int total;

	public String getTitle() {return title;}
	public long getId() {return id;}
	public long getDate() {return date;}
	public int getTotal() {return total;}

	public void setTitle(String title) {this.title = title;}
	public void setId(long id) {this.id = id;}
	public void setDate(long date) {this.date = date;}
	public void setTotal(int total) {this.total = total;}
}
