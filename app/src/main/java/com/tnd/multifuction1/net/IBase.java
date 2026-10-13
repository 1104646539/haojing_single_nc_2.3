package com.tnd.multifuction1.net;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public interface IBase {

    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    int getContentLayout();

    //View getView();

    void bindView(View view, Bundle savedInstanceState);

    void initData();

}
