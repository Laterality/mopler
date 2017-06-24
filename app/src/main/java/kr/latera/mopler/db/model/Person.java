package kr.latera.mopler.db.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by Jinwoo Shin on 2017-05-30.
 */

public class Person extends RealmObject
{

	private long id;
	@Index
	private String name;

	public long getId() {return id;}
	public String getName() {return name;}

	public void setId(long id) {this.id = id;}
	public void setName(String name) {this.name = name;}
}
