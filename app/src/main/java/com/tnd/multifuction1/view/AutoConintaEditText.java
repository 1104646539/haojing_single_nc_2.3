package com.tnd.multifuction1.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tnd.multifuction1.R;

public class AutoConintaEditText extends FrameLayout {
    private View view;
    private String unit;
    private TextView tvUnit;
    private EditText etUnit;
    String temp = "";

    public AutoConintaEditText(@NonNull Context context) {
        this(context, null);

    }

    public AutoConintaEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoConintaEditText);
        unit = typedArray.getString(R.styleable.AutoConintaEditText_unit);
//        if (unit == null) {
//            unit = "";
//        }
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layou_auto_coninta, null, false);


        addView(view, 0);
        initView();
    }

    private void initView() {
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        etUnit = (EditText) findViewById(R.id.et_unit);


        etUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = etUnit.getText().toString().trim();
                if (unit == null) {
                    tvUnit.setText(temp + "" + "");
                } else {
                    tvUnit.setText(temp + "" + unit);
                }
                if (temp.length() == 0) {
                    tvUnit.setText("");
                }
                if (onSave != null) {
                    onSave.Save(tvUnit.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setOnSave(OnSave onSave) {
        this.onSave = onSave;
    }

    public void setText(String str) {
        if (str != null) {
            if (unit != null) {
                if (str.contains(unit)) {
                    str = str.replace(unit, "");
                }

            }
            etUnit.setText(str);
        }
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setInputType(int type) {
        etUnit.setInputType(type);
    }

    OnSave onSave;

    public interface OnSave {
        void Save(String str);
    }
}
