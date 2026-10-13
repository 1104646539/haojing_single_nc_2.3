package com.tnd.multifuction1.model;

/**
 * 商品来源
 */
public class SampleSource extends UserInputModel<SampleSource> implements FiltrateModel{
    //id
    public int ss_id;
    //name
    public String ss_name;
    private boolean isSelect;
    public SampleSource() {
    }
    @Override
    public String toString() {
        return ss_name==null?"":ss_name;
    }
    public SampleSource(String ss_name) {
        this.ss_name = ss_name;
    }

    @Override
    public String getName() {
        return ss_name;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public void setSelect(boolean isSelect) {

    }
}
