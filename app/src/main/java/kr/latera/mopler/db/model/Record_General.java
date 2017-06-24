package kr.latera.mopler.db.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jinwoo Shin on 2017-04-25.
 */

public class Record_General extends RealmObject
{
	@PrimaryKey
	private int id;
	private int amount;
	@Index
	private long date;
	private String where;
	private String content;

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public void setDate(long date)
	{
		this.date = date;
	}
	public void setWhere(String where) { this.where = where; }
	public void setContent(String content)
	{
		this.content = content;
	}

	public int getId() {return id;}
	public int getAmount() {return amount;}
	public long getDate() {return date;}
	public String getWhere() {return where;}
	public String getContent() {return content;}

}
