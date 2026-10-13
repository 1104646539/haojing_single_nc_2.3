package com.tnd.multifuction1.model;

/**
 * 检测单位
 */
public class BCheckOrg extends UserInputModel<BCheckOrg> implements FiltrateModel {
    //id
    public int bco_id;
    //name
    public String bco_name;
    private boolean isSelect;
    public BCheckOrg() {
    }

    @Override
    public String toString() {
        return bco_name==null?"":bco_name;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public BCheckOrg(String co_name) {
        this.bco_name = co_name;
    }

    @Override
    public String getName() {
        return bco_name;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }
}
