package com.tnd.multifuction1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.PrintAdapter;
import com.tnd.multifuction1.model.Print;

import java.util.List;

public class PrintSettingDialog extends Dialog {
    private Context context;
    private TextView tv_title;
    private Button btn_save, btn_cancel;
    private LinearLayout ll_mu, ll_me;
    private ImageView iv_mu, iv_me;
    private View contentView;
    private PrintAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager manager;

    public PrintSettingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_print_check_layout, null, false);
        tv_title = contentView.findViewById(R.id.tv_title);
        ll_mu = contentView.findViewById(R.id.ll_mu);
        ll_me = contentView.findViewById(R.id.ll_me);
        iv_mu = contentView.findViewById(R.id.iv_mu);
        iv_me = contentView.findViewById(R.id.iv_me);
        btn_save = contentView.findViewById(R.id.btn_save);
        btn_cancel = contentView.findViewById(R.id.btn_cancel);
        recyclerView = contentView.findViewById(R.id.rv_data);

        adapter = new PrintAdapter(context);
        manager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * @param title    标题
     * @param prints   全部数据
     * @param isSingle 是否是逐条打印
     */
    public void showDialog(String title, List<Print> prints, boolean isSingle) {
        Log.d("showDialog",
                "title=" + title + "isSingle=" +
                        isSingle + "prints=" + prints.toString());
        //default
        iv_mu.setSelected(isSingle ? true : false);
        iv_me.setSelected(isSingle ? false : true);

        adapter.setZT(isSingle);
        adapter.setData(prints);

        tv_title.setText(title != null ? title : "");
        ll_mu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setZT(true);
                adapter.notifyDataSetChanged();
                iv_mu.setSelected(true);
                iv_me.setSelected(false);
            }
        });
        if (title.contains("数据")){
            ll_me.setVisibility(View.GONE);

        }else{
            ll_me.setVisibility(View.VISIBLE);
        }
        ll_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setZT(false);
                adapter.notifyDataSetChanged();
                iv_mu.setSelected(false);
                iv_me.setSelected(true);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPrintDataSave != null) {
                    onPrintDataSave.OnPrintDataSave(adapter.getRealData(), adapter.isZT());
                    dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });
        show();
        setContentView(contentView);
    }

    OnPrintDataSave onPrintDataSave;

    public void setOnPrintDataSave(OnPrintDataSave onPrintDataSave) {
        this.onPrintDataSave = onPrintDataSave;
    }

    public interface OnPrintDataSave {
        void OnPrintDataSave(List<Print> prints, boolean isZt);
    }
}
