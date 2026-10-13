package com.tnd.jinbiao.model;

import com.lidroid.xutils.db.annotation.Transient;

public class PeopleModel {

	public int id;
	public String name;
	public int source;// 1表示检验员，0表示检验单位

	@Transient
	public boolean is_check;

	public PeopleModel(String name, int source) {
		this.name = name;
		this.source = source;
	}

	public PeopleModel() {
	}

	public boolean getIs_check() {
		return is_check;
	}

	public void setIs_check(boolean is_check) {
		this.is_check = is_check;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
