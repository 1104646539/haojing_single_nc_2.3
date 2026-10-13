package com.tnd.multifuction1.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


import com.tnd.multifuction1.R;

import java.util.ArrayList;


public class MaskedEditText extends EditText implements View.OnTouchListener, View.OnFocusChangeListener {

    private Context context;
    private String mask;

    @Nullable
    private IconCallback iconCallback;
    private Drawable maskIcon;

    private ArrayList<Integer> listValidCursorPositions = new ArrayList<>();
    private Integer firstAllowedPosition = 0;
    private Integer lastAllowedPosition = 0;
    private View.OnFocusChangeListener onFocusChangeListener;

    public MaskedEditText(Context context) {
        super(context);
        init(context, null, null, null);
    }

    private MaskedEditText(Context context, @DrawableRes int maskIcon, IconCallback iconCallback) {
        super(context);
        Drawable drawable = null;
        if (maskIcon != -1)
            drawable = this.getResources().getDrawable(maskIcon);
        init(context, null, drawable, iconCallback);
    }

    public MaskedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaskedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        init(context, attrs, null, null);
    }

    private void init(Context context, AttributeSet attrs, Drawable maskIcon, IconCallback iconCallback) {
        this.context = context;
        this.maskIcon = maskIcon;
        this.iconCallback = iconCallback;
        initByAttributes(context, attrs);
        initMaskIcon();

        this.setLongClickable(false);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

    @SuppressLint("RestrictedApi")
    private void initByAttributes(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaskedEditText, 0, 0);
//        mask = a.getString(R.styleable.MaskedEditText_mask);
//        int maskedIconRes = a.getResourceId(R.styleable.MaskedEditText_maskIcon, 0);
//        if (maskedIconRes > 0) {
//            AppCompatDrawableManager dm = AppCompatDrawableManager.get();
//            Drawable drawableIcon = dm.getDrawable(context, maskedIconRes);
//            if (drawableIcon != null) {
//                final Drawable wrappedDrawable = DrawableCompat.wrap(drawableIcon);
//                maskIcon = wrappedDrawable;
//            }
//        }

//        notMaskedSymbol = a.getString(R.styleable.MaskedEditText_notMaskedSymbol);
        mask = a.getString(R.styleable.MaskedEditText_mask);

        int maskedIconRes = a.getResourceId(R.styleable.MaskedEditText_maskIcon, 0);

        if (maskedIconRes > 0) {
            AppCompatDrawableManager dm = AppCompatDrawableManager.get();
            Drawable drawableIcon = dm.getDrawable(context, maskedIconRes);
            if (drawableIcon != null) {
                final Drawable wrappedDrawable = DrawableCompat.wrap(drawableIcon);
                int drawableIconColor = a.getColor(R.styleable.MaskedEditText_maskIconColor, getCurrentHintTextColor());
                DrawableCompat.setTint(wrappedDrawable, drawableIconColor);
                maskIcon = wrappedDrawable;
            }
        }
    }

    private void initMaskIcon() {
        if (maskIcon != null) {
            maskIcon.setBounds(0, 0, maskIcon.getIntrinsicWidth(), maskIcon.getIntrinsicHeight());
            final Drawable[] compoundDrawables = getCompoundDrawables();
            setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], maskIcon, compoundDrawables[3]);
        }
        super.setOnFocusChangeListener(this);
        super.setOnTouchListener(this);
    }

    @Override
    public void setOnFocusChangeListener(final View.OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final View.OnTouchListener onTouchListener) {
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(view, hasFocus);
        }
        if (hasFocus) {
            this.setSelection(firstAllowedPosition);
            this.requestFocus();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int x = (int) event.getX();
        if (maskIcon != null && maskIcon.isVisible() && x > getWidth() - getPaddingRight() - maskIcon.getIntrinsicWidth()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (iconCallback != null)
                    iconCallback.onIconPushed(this);
            }
            return true;
        }
        return false;
    }

    public void setIconCallback(@Nullable IconCallback iconCallback) {
        this.iconCallback = iconCallback;
    }

    public interface IconCallback {
        void onIconPushed(EditText et);
    }

}
