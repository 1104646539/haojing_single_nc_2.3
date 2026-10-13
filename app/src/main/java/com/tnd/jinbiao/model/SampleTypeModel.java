package com.tnd.jinbiao.model;

import com.lidroid.xutils.db.annotation.Transient;

public class SampleTypeModel {

	private int id;
	private String name;

	@Transient
	public boolean is_check;

	public boolean getIs_check() {
		return is_check;
	}

	public void setIs_check(boolean is_check) {
		this.is_check = is_check;
	}

	public SampleTypeModel(String name) {
		this.name = name;
	}

	public SampleTypeModel() {

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
