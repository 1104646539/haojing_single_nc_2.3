package com.tnd.multifuction1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.SampleName;

/**
 * 上传网址设置
 */
public class EditSampleDialog extends Dialog {
    private Context context;
    private TextView tv_title, tv_url, tv_user, tv_pw;
    private EditText et_code, et_name, et_type;
    private Button btn_save, btn_cancel;
    private View contentView;

    public EditSampleDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }


    private void init() {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_sample_layout, null, false);
        et_code = contentView.findViewById(R.id.et_code);
        et_name = contentView.findViewById(R.id.et_name);
        et_type = contentView.findViewById(R.id.et_type);
        btn_save = contentView.findViewById(R.id.btn_save);
        btn_cancel = contentView.findViewById(R.id.btn_cancel);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void showDilaog(OnSampleSave onSampleSave) {
        this.onSampleSave = onSampleSave;
        if (isShowing()) {
            dismiss();
        }
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNull()) {
                    if(!isRepeat()){
                        if (onSampleSave != null) {
                            onSampleSave.onSampleSave(et_code.getText().toString().trim(),
                                    et_name.getText().toString().trim(),
                                    et_type.getText().toString().trim());
                            dismiss();
                        }
                    }
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

    private boolean isNull() {
        if (et_code.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "请输入商品编码", Toast.LENGTH_SHORT).show();
            return true;
        } else if (et_name.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "请输入商品名称", Toast.LENGTH_SHORT).show();
            return true;
        } else if (et_type.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "请输入商品类别", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    private boolean isRepeat() {
        SampleName sampleName = new SampleName();
        SampleName sampleCode = new SampleName();
        try {
            sampleName = DbHelper.GetInstance().findFirst(Selector.from(SampleName.class).where("sampleName", "=", et_name.getText().toString().trim()));// 自定义sql查询
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sampleCode = DbHelper.GetInstance().findFirst(Selector.from(SampleName.class).where("sampleNumber", "=", et_code.getText().toString().trim()));// 自定义sql查询
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (sampleCode != null) {
            Toast.makeText(context, "商品编码已存在", Toast.LENGTH_SHORT).show();
            return true;
        }else if(sampleName != null){
            Toast.makeText(context, "商品名称已存在", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }



    OnSampleSave onSampleSave;

    public void setOnSampleSave(OnSampleSave onSampleSave) {
        this.onSampleSave = onSampleSave;
    }

    public interface OnSampleSave {
        void onSampleSave(String code, String name, String type);
    }

}
