package com.lidroid.xutils.db.model;

public class Sample{

    public String sampleName;

    public String sampleNo;

    public Sample() {
    }

    public Sample(String sampleName, String sampleNo) {
        this.sampleName = sampleName;
        this.sampleNo = sampleNo;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "sampleName='" + sampleName + '\'' +
                ", sampleNo='" + sampleNo + '\'' +
                '}';
    }
}
