package com.tnd.jinbiao.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;


import com.tnd.multifuction1.R;

import java.lang.reflect.Field;

public class DateTimePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener{

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";

	private final DatePicker mDatePicker;
	private final TimePicker mTimePicker;
	private final OnDateSetListener mCallBack;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {


		void onDateSet(DatePicker startDatePicker, int startYear,int startMonthOfYear, int startDayOfMonth,
				TimePicker timePicker, int hour, int minute);
	}
	
	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public DateTimePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth,
                                int hour, int minute) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth, hour, minute);
	}

	public DateTimePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth,
                                int hour, int minute) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth, hour, minute, true);
	}

	
	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param theme
	 *            the theme to apply to this dialog
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public DateTimePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                                int dayOfMonth, int hour, int minute, boolean isDayVisible) {
		super(context, theme);

		mCallBack = callBack;

		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, "确 定", this);
		setButton(BUTTON_NEGATIVE, "取 消", this);
		// setButton(BUTTON_POSITIVE,
		// themeContext.getText(android.R.string.date_time_done), this);
		setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_picker_dialog, null);
		setView(view);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		mTimePicker = (TimePicker) view.findViewById(R.id.timepicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
		
		System.out.println(mDatePicker.getWidth());
		System.out.println(mTimePicker.getWidth());
		// updateTitle(year, monthOfYear, dayOfMonth);

		// 如果要隐藏当前日期，则使用下面方法。
		if (!isDayVisible) {
			hidDay(mDatePicker);
			//hidDay(mDatePicker_end);
		}
	}
	      
	/**
	 * 隐藏DatePicker中的日期显示
	 * 
	 * @param mDatePicker
	 */
	private void hidDay(DatePicker mDatePicker) {
		Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
		for (Field datePickerField : datePickerfFields) {
			if ("mDaySpinner".equals(datePickerField.getName())) {
				datePickerField.setAccessible(true);
				Object dayPicker = new Object();
				try {
					dayPicker = datePickerField.get(mDatePicker);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				// datePicker.getCalendarView().setVisibility(View.GONE);
				((View) dayPicker).setVisibility(View.GONE);
			}
		}
	}
	      
	public void onClick(DialogInterface dialog, int which) {
		// Log.d(this.getClass().getSimpleName(), String.format("which:%d",
		// which));
		// 如果是“取 消”按钮，则返回，如果是“确 定”按钮，则往下执行
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if (view.getId() == R.id.datePicker)
			mDatePicker.init(year, month, day, this);
//		if (view.getId() == R.id.ti)
//			mDatePicker_end.init(year, month, day, this);
		// updateTitle(year, month, day);
	}

	/**
	 * 获得开始日期的DatePicker
	 * 
	 * @return The calendar view.
	 */
	public DatePicker getDatePickerStart() {
		return mDatePicker;
	}

	/**
	 * 获得结束日期的DatePicker
	 * 
	 * @return The calendar view.
	 */
//	public DatePicker getDatePickerEnd() {
//		return mDatePicker_end;
//	}

	/**
	 * Sets the ScanStart date.
	 * 
	 * @param year
	 *            The date year.
	 * @param monthOfYear
	 *            The date month.
	 * @param dayOfMonth
	 *            The date day of month.
	 */
	public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

//	public void updateEndDate(int year, int monthOfYear, int dayOfMonth) {
//		mDatePicker_end.updateDate(year, monthOfYear, dayOfMonth);
//	}

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mTimePicker.clearFocus();
			mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),	mDatePicker.getMonth(),mDatePicker.getDayOfMonth(), 
					mTimePicker, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
		}
	}

	@Override
	protected void onStop() {
		// tryNotifyDateSet();
		super.onStop();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		state.putInt(HOUR, mTimePicker.getCurrentHour());
		state.putInt(MINUTE, mTimePicker.getCurrentMinute());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int start_year = savedInstanceState.getInt(YEAR);
		int start_month = savedInstanceState.getInt(MONTH);
		int start_day = savedInstanceState.getInt(DAY);
		mDatePicker.init(start_year, start_month, start_day, this);

		int hour = savedInstanceState.getInt(HOUR);
		int minute = savedInstanceState.getInt(MINUTE);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
		//mDatePicker_end.init(end_year, end_month, end_day, this);

	}
}
