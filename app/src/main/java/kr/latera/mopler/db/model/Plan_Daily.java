package kr.latera.mopler.db.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jinwoo Shin on 2017-05-06.
 */

public class Plan_Daily extends RealmObject
{
	@PrimaryKey
	private int id;
	@Index private long date;
	private int budget_daily; // 일간 예산

	public void setDate(long d) {this.date = d;}
	public void setBudget(int budget) throws IllegalArgumentException
	{
		if(budget < 0)
		{
			// illegal value
			throw new IllegalArgumentException("Budget less than zero");
		}
		else
		{
			this.budget_daily = budget;
		}

	}

	public int getId() {return this.id;}
	public long getDate() {return this.date;}
	public int getBudget() {return budget_daily;}
}
