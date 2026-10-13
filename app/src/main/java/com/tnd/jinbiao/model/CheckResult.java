package com.tnd.jinbiao.model;

import com.lidroid.xutils.db.annotation.Transient;

public class CheckResult extends BaseData<CheckResult> {


    public int uploadId;

    public CheckResult() {

    }

    public CheckResult(String checkedOrganization, String bcheckedOrganization, String projectName,
                       String sampleNum,
                       String sampleName, String sampleSource,
                       String channel, long testTime, String testValue,
                       String resultJudge, String xlz, String testStandard,
                       String checker, String weight, String twh, String url, String mobile, int totalTime) {
        this.checkedOrganization = checkedOrganization;
        this.bcheckedOrganization = bcheckedOrganization;
        this.sampleNum = sampleNum;
        this.projectName = projectName;
        this.sampleName = sampleName;
        this.sampleSource = sampleSource;
        this.channel = channel;
        this.testTime = testTime;
        this.testValue = testValue;
        this.resultJudge = resultJudge;
        this.xlz = xlz;
        this.testStandard = testStandard;
        this.checker = checker;
        this.weight = weight;
        this.twh = twh;
        this.qrUrl = url;
        this.mobile = mobile;
        this.totalTime = totalTime;
    }

    public String checkedOrganization = "";
    public String bcheckedOrganization = "";

    public String projectName;

    public String sampleName;

    public String sampleNum = "";

    public String sampleSource = "";

    public String weight = "";

    public String channel;

    public String qrUrl;
    public String mobile;

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public long testTime;

    public String testValue;

    public String resultJudge;

    public String xlz;

    public String testStandard;

    public String checker;
    public String twh;
    public int totalTime;

    @Transient
    public boolean isSelected;

    public SampleName sn;

    @Override
    public String toString() {
        return "CheckResult{" +
                "uploadId=" + uploadId +
                ", checkedOrganization='" + checkedOrganization + '\'' +
                ", bcheckedOrganization='" + bcheckedOrganization + '\'' +
                ", projectName='" + projectName + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", sampleSource='" + sampleSource + '\'' +
                ", channel='" + channel + '\'' +
                ", testTime=" + testTime +
                ", testValue='" + testValue + '\'' +
                ", resultJudge='" + resultJudge + '\'' +
                ", xlz='" + xlz + '\'' +
                ", testStandard='" + testStandard + '\'' +
                ", checker='" + checker + '\'' +
                ", isSelected=" + isSelected + '\'' +
                ", weight=" + weight +
                ", twh=" + twh +
                ", sampleNum=" + sampleNum + ", sn=" + sn +
                '}';
    }
}
