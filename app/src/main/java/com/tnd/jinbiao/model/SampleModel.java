package com.tnd.jinbiao.model;

import com.lidroid.xutils.db.annotation.Transient;

public class SampleModel {

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

	public SampleModel(String name) {
		this.name = name;
	}

	public SampleModel() {

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
