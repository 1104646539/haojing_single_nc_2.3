package com.tnd.multifuction1.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.tnd.multifuction1.interfaces.ISaveCallBack;
import com.tnd.multifuction1.model.UserInputModel;


public abstract class OperateBaseDataDialog<T extends UserInputModel> extends AlertDialog {

    T t = null;
    ISaveCallBack<T> iSaveCallBack;
    protected OperateBaseDataDialog(Context context, View view, T data) {
        super(context);
        setView(view);
        initCustomView(data);
        this.t = data;
        getSaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSaveCallBack != null) {
                    T data = getData(t);
                    if(data != null) {
                        if(!iSaveCallBack.save(data)) {
                            dismiss();
                        }
                    }
                }
            }
        });
        getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public OperateBaseDataDialog<T> setCallBack(ISaveCallBack<T> iSaveCallBack) {

        this.iSaveCallBack = iSaveCallBack;
        return this;
    }

    protected abstract void initCustomView(T t);

    protected abstract T getData(T t);

    protected abstract Button getSaveButton();

    protected abstract Button getCancelButton();
}
