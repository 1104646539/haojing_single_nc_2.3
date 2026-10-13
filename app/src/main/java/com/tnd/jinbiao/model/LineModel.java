package com.tnd.jinbiao.model;

public class LineModel {

	public int id;

	public int source;
	public String name;
	public String card_name;

	public String ScanStart;
	public String ScanEnd;
	public String CTDistance;
	public String CTWidth;
	public String Jcx;
	public String Ljz;
	public String A1;
	public String A2;
	public String X0;
	public String P;
	public String ConcentrateUnit;

	public LineModel(){

	}

	public LineModel(int source, String name, String cardName, String scanStart, String scanEnd,
					 String peakWidth, String peakDistance, String jcx, String ljz, String concentrateUnit){
		this.source= source;
		this.name = name;
		this.card_name = cardName;
		this.ScanStart = scanStart;
		this.ScanEnd = scanEnd;
		this.CTWidth = peakWidth;
		this.CTDistance = peakDistance;
		this.Jcx = jcx;
		this.Ljz = ljz;
		this.ConcentrateUnit = concentrateUnit;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	public String getX0() {
		return X0;
	}

	public void setX0(String x0) {
		this.X0 = x0;
	}

	public String getP() {
		return P;
	}

	public void setP(String p) {
		this.P = p;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
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

	public String getCTDistance() {
		return CTDistance;
	}

	public void setCTDistance(String CTDistance) {
		this.CTDistance = CTDistance;
	}

	public String getCTWidth() {
		return CTWidth;
	}

	public void setCTWidth(String CTWidth) {
		this.CTWidth = CTWidth;
	}

	public String getJcx() {
		return Jcx;
	}

	public void setJcx(String jcx) {
		this.Jcx = jcx;
	}

	public String getLjz() {
		return Ljz;
	}

	public void setLjz(String ljz) {
		this.Ljz = ljz;
	}

	public String getA1() {
		return A1;
	}

	public void setA1(String a1) {
		this.A1 = a1;
	}

	public String getA2() {
		return A2;
	}

	public void setA2(String a2) {
		this.A2 = a2;
	}

	public String getConcentrateUnit() {
		return ConcentrateUnit;
	}

	public void setConcentrateUnit(String concentrateUnit) {
		this.ConcentrateUnit = concentrateUnit;
	}

}
