package com.tnd.jinbiao.model;

public class CardCompanyModel {

	public int id;
	public String name;
	public String ScanStart;
	public String ScanEnd;
	public String CTPeakDistance;
	public String CTPeakWidth;

	public CardCompanyModel(){

	}

	public CardCompanyModel(String name, String scanStart, String scanEnd, String CTPeakWidth, String CTPeakDistance) {
		this.name = name;
		this.ScanStart = scanStart;
		this.ScanEnd = scanEnd;
		this.CTPeakWidth = CTPeakWidth;
		this.CTPeakDistance = CTPeakDistance;
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
	public String getScanStart() {
		return ScanStart;
	}
	public void setScanStart(String scanStart) {
		this.ScanStart = scanStart;
	}
	public String getScanEnd() {
		return ScanEnd;
	}
	public void setScanEnd(String scanEnd) {
		this.ScanEnd = scanEnd;
	}
	public String getCTPeakDistance() {
		return CTPeakDistance;
	}
	public void setCTPeakDistance(String CTPeakDistance) {
		this.CTPeakDistance = CTPeakDistance;
	}
	public String getCTPeakWidth() {
		return CTPeakWidth;
	}
	public void setCTPeakWidth(String CTPeakWidth) {
		this.CTPeakWidth = CTPeakWidth;
	}

}
