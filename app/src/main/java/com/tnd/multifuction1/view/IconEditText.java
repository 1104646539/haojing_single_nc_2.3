package com.tnd.multifuction1.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tnd.multifuction1.R;

/**
 * IconTextEdit
 *
 * An EditText with an Icon beside it.
 *
 * Created by kylewbanks on 15-09-05.
 */
public class IconEditText extends LinearLayout {

    private static final String TAG = IconEditText.class.getSimpleName();

    /**
     * UI Constants
     */
    private static final float ICON_WEIGHT = 0.15f;
    private static final float EDIT_TEXT_WEIGHT = 0.85f;

    private static final String HINT_PREFIX = " ";

    /**
     * Resource pointer for the Icon to display.
     */
    private Integer _iconResource;

    /**
     * The Hint text to display.
     */
    private String _hint;

    /**
     * Indicates if the EditText is for a password.
     */
    private boolean _isPassword = false;

    /**
     * UI Components
     */
    private ImageView _icon;
    private EditText _editText;

    private float image_width;

    private float image_height;

    /**
     * IconTextEdit Constructor
     * @param context
     */
    public IconEditText(Context context) {
        this(context, null);
    }

    /**
     * IconTextEdit Constructor
     * @param context
     * @param attrs
     */
    public IconEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * IconTextEdit Constructor
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public IconEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.parseAttributes(context, attrs);
        this.initialize();
    }

    /**
     * Parses out the custom attributes.
     *
     * @param context
     * @param attrs
     */
    private void parseAttributes(Context context, AttributeSet attrs) {
        Log.d(TAG, "parseAttributes()");
        if (attrs == null) {
            return;
        }

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.IconEditText, 0, 0);

        try {
//            int textSize = a.getDimensionPixelSize(R.styleable.IconEditText_textSize, getScaledPixels(16));
//            if(textSize < getScaledPixels(12))
//                textSize = getScaledPixels(12);
//
//            _editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            _iconResource = a.getResourceId(R.styleable.IconEditText_iconSrc, 0);
            _hint = a.getString(R.styleable.IconEditText_hint);
            _isPassword = a.getBoolean(R.styleable.IconEditText_isPassword, false);
//            image_width = DensityUtil.dip2px(context, a.getDimension(R.styleable.IconEditText_image_width, 20));
//            image_height = DensityUtil.dip2px(context, a.getDimension(R.styleable.IconEditText_image_height, 20));
            Log.d(TAG, "{ _iconResource: " + _iconResource + ", _hint: " + _hint + ", _isPassword: " + _isPassword + "}");
            Log.i(TAG, "image_width:" + image_width + "    image_height:" +image_height );
        } catch (Exception ex) {
            Log.e(TAG, "Unable to parse attributes due to: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    private int getPixels(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int getScaledPixels(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, getResources().getDisplayMetrics());
    }

    /**
     * Initializes the Icon and TextEdit.
     */
    private void initialize() {
        Log.d(TAG, "initialize()");

        // Mandatory parameters
        this.setOrientation(LinearLayout.HORIZONTAL);


        // Create the EditText
        if (_editText == null) {
            _editText = new EditText(this.getContext());
            _editText.setInputType(
                    _isPassword ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            );
            _editText.setLayoutParams(
                    new LayoutParams(0, LayoutParams.MATCH_PARENT, EDIT_TEXT_WEIGHT)
            );

            if (_hint != null) {
                _editText.setHint(String.format("%s%s", HINT_PREFIX, _hint.toLowerCase()));
            }
            _editText.setEnabled(false);
            this.addView(_editText);
        }

        // Create the Icon
        if (_icon == null) {
            _icon = new ImageView(this.getContext());

//            _icon.setLayoutParams(new LayoutParams((int)image_width, (int)image_height));
            _icon.setLayoutParams(new LayoutParams(100, LayoutParams.MATCH_PARENT));
//            _icon.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, ICON_WEIGHT));
            _icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            if (_iconResource != null && _iconResource != 0) {
                _icon.setImageResource(_iconResource);
            }
            _icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iconClickListener != null) {
                        iconClickListener.onClick(IconEditText.this);
                    }
                }
            });
            this.addView(_icon);
        }

    }

    OnClickListener iconClickListener;

    public void setOnIconClickListener(OnClickListener listener) {

        this.iconClickListener = listener;
    }

    /**
     * Convenience Accessor to the underlying EditText's 'getText()' method.
     *
     * @return
     */
    public Editable getText() {
        return _editText.getText();
    }

    public void setText(CharSequence charSequence) {
        _editText.setText(charSequence);
    }

    /**
     * Returns the underlying EditText.
     *
     * @return
     */
    public EditText getEditText() {
        return _editText;
    }

    /**
     * Returns the underlying ImageView displaying the icon.
     *
     * @return
     */
    public ImageView getImageView() {
        return _icon;
    }

}
