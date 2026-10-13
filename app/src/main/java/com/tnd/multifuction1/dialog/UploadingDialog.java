package com.tnd.multifuction1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tnd.multifuction1.R;

/**
 * 软件升级
 */
public class UploadingDialog extends Dialog {
    private Context context;
    private TextView tv_title, tv_content;
    private EditText et_content;
    private Button btn_save, btn_cancel;
    private View contentView;
    private LinearLayout ll_show;
    //编辑
    public final static int TYPE_EDIT = 1;
    //显示
    public final static int TYPE_SHOW = 2;
    private int type;

    public UploadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_uploding_layout, null, false);
        tv_title = contentView.findViewById(R.id.tv_title);
        tv_content = contentView.findViewById(R.id.tv_content);
        et_content = contentView.findViewById(R.id.et_content);
        btn_save = contentView.findViewById(R.id.btn_save);
        btn_cancel = contentView.findViewById(R.id.btn_cancel);
        ll_show = contentView.findViewById(R.id.ll_show);

//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void showDialog(String title, String msg, int type) {
        this.type = type;
        if (isShowing()) {
            dismiss();
        }
        if (type == TYPE_EDIT) {
            tv_content.setVisibility(View.GONE);
            ll_show.setVisibility(View.VISIBLE);
        } else if (type == TYPE_SHOW) {
            ll_show.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
        }
        tv_title.setText(title);
        if (msg != null) {
            tv_content.setText(msg);
        }
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmListener != null) {
                    if (type == TYPE_SHOW) {
                        onConfirmListener.onUploading();
                        dismiss();
                    } else if (type == TYPE_EDIT) {
                        if (et_content.getText().toString().trim().isEmpty()) {
                            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dismiss();
                        onConfirmListener.onConfirmPw(et_content.getText().toString().trim());
                        et_content.setText("");
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        show();
        setContentView(contentView);
    }

    public void showDialog(String title, int type) {
        showDialog(title, null, type);
    }

    OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {
        void onConfirmPw(String pw);

        void onUploading();
    }
}
