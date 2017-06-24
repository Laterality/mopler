package kr.latera.mopler.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jinwoo Shin on 2017-04-25.
 */

public class Record_Repetition_Monthly extends RealmObject
{
	@PrimaryKey
	private int id;
	private int amount;
	private int day_monthly;
	private String where;
	private String content;

	private boolean month_jan;
	private boolean month_feb;
	private boolean month_mar;
	private boolean month_apr;
	private boolean month_may;
	private boolean month_jun;
	private boolean month_jul;
	private boolean month_aug;
	private boolean month_sep;
	private boolean month_oct;
	private boolean month_nov;
	private boolean month_dec;

	public void setAmount(int amount){this.amount = amount;}
	public void setDay(int day_monthly){this.day_monthly = day_monthly;}
	public void setWhere(String where){this.where = where;}
	public void setContent(String content){this.content = content;}

	public int getId() {return id;}
	public int getDay() {return day_monthly;}
	public int getValue() {return amount;}
	public String getWhere() {return where;}
	public String getContent() {return content;}

	public boolean isRepeatedMonth(int month)
	{
		if (month >= 13 || month <= 0)
		{
			return false;
		}

		switch(month)
		{
			case 1: return month_jan;
			case 2: return month_feb;
			case 3: return month_mar;
			case 4: return month_apr;
			case 5: return month_may;
			case 6: return month_jun;
			case 7: return month_jul;
			case 8: return month_aug;
			case 9: return month_sep;
			case 10: return month_oct;
			case 11: return month_nov;
			case 12: return month_dec;
			default: return false;
		}
	}
}
