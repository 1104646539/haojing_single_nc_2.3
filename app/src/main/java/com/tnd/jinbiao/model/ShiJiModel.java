package com.tnd.jinbiao.model;

import com.lidroid.xutils.db.annotation.Transient;

public class ShiJiModel {

	public int id;
	public String name;
	public String code;

	@Transient
	public boolean is_check;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean getIs_check() {
		return is_check;
	}

	public void setIs_check(boolean is_check) {
		this.is_check = is_check;
	}

}
