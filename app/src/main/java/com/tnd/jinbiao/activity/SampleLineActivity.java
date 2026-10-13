package com.tnd.jinbiao.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyarm.AndroidSDK.HardwareControler;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.ToolUtils;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.LineModel;
import com.tnd.jinbiao.model.ResultPhotoImgModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SampleLineActivity extends BaseActivity {

	private DbUtils db;
	private TextView tvDr = null;
	private Spinner project_spinner = null;
	private String[] project_list = null;

	private List<LineModel> projectlist = null;

	private ArrayAdapter project_adapter = null;

	private LineModel model_s = null;

	private final String TAG = CheckActivity.class.getSimpleName();

	private LineChart mChart;
	private byte[] imageData;
	/**
	 * 是否正在检测的标志位
	 */
	private boolean isTesting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_line_main);
		db = DbHelper.GetInstance();
		tvDr = (TextView) findViewById(R.id.move_tv_value);
		project_spinner = (Spinner) findViewById(R.id.spn_sample_line_project);
		mChart = (LineChart) findViewById(R.id.move_chart);
		mChart.getDescription().setEnabled(false);
		mChart.setNoDataText("");

		try {
			projectlist = db.findAll(Selector.from(LineModel.class));

			if (projectlist == null) {
				projectlist = new ArrayList<LineModel>();
			}
			project_list = new String[projectlist.size()];
			for (int i = 0; i < projectlist.size(); i++) {
				LineModel model = projectlist.get(i);
				project_list[i] = model.getName();
			}

		} catch (DbException e) {
			e.printStackTrace();
		}
		if (project_list != null && project_list.length > 0) {
			LineModel model = projectlist.get(0);
			model_s = model;
		}
		if (project_list == null || project_list.length <= 0) {
			project_list = new String[1];
			project_list[0] = "请先添加检测项目";
		}

		project_adapter = new ArrayAdapter(this, R.layout.item_simple_spiner, project_list);
		project_spinner.setAdapter(project_adapter);
		project_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (projectlist != null && projectlist.size() > 0) {
					model_s = projectlist.get(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	public void ClickDraw(View v) {

		if (model_s == null) {
			Toast.makeText(this, "请先选择检测项目", Toast.LENGTH_LONG).show();
			return;
		}
		if(isTesting){
			APPUtils.showToast(this,"正在获取图像，请稍后...");
			return;
		}
		isTesting = true;
		tvDr.setText("检测值：");

		final String message = "GetData" + "," + model_s.getSource() + ","
				+ model_s.getScanStart() + "," + model_s.getScanEnd() + ","
				+ model_s.getCTWidth() + "," + model_s.getCTDistance() + ","
				+ model_s.getLjz();

		mChart.clear();
		byte[] data = message.getBytes(Charset.forName("gb2312"));
		if(!SerialUtils.COM3_SendData(data)){
			isTesting = false;
			APPUtils.showToast(this, "数据发送失败");
			return;
		}
		ToolUtils.showHUD(SampleLineActivity.this, "请稍等...");
		new Thread(){

			@Override
			public void run() {
				SystemClock.sleep(5000);
				int minDataLenght = (Integer.parseInt(model_s.ScanEnd.trim()) - Integer.parseInt(model_s.ScanStart.trim())) * 2 + 6; //  +5是因为dr值至少为形如0.123，所以至少要+6
				if (isFinishing()) {
					return;
				}
				imageData = null;
				imageData = RecImageData(minDataLenght);
				if (imageData == null) {
					APPUtils.showToast(SampleLineActivity.this, "图像数据获取失败");
					return;
				}
				handlerMess.sendEmptyMessage(100);
			}
		}.start();

	}

	/**
	 * 接收图像数据
	 * @param minDataLength
	 * @return
	 */
	private byte[] RecImageData(int minDataLength){
		byte[] response = new byte[4096];
//		int select = HardwareControler.select(ToolUtils.devfd, 2, 20);
//		if(select == 0){
//			return null;
//		}
		int currentDataLength = 0;
		int errorCount = 0;
		byte[] buffer = new byte[1024];
		while (errorCount < 100){
			int len = HardwareControler.read(Global.DEV_COM3, buffer, buffer.length);
			if(len < 1){
				errorCount++;
			}else{
				System.arraycopy(buffer,0, response, currentDataLength, len);
				Array.setByte(buffer, 0, (byte) '\0');
			}
			currentDataLength+=len;
			if(currentDataLength >= minDataLength){
				break;
			}
			SystemClock.sleep(500);
		}
		if(errorCount >= 100){
			return null;
		}
		int index = -1;
		for (int i = 0; i < currentDataLength; i++) {
			if(response[i] == ','){
				index = i;
				break;
			}
		}
		if(index == -1){   //如果不包含逗号，则说明没有dr值，数据不完整
			return null;
		}
		final String drValue = new String(response, 0, index + 1, Charset.forName("gbk")).replace("OK","").replace(",","");
		byte[] data = new byte[currentDataLength - index - 1];
		System.arraycopy(response,index + 1, data, 0, data.length);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvDr.append(drValue);
			}
		});

		return data;
	}


	private String devName = "/dev/ttyAMA3";
	private int speed = 9600;
	private int dataBits = 8;
	private int stopBits = 1;
	private int devfd = -1;
	private final int BUFSIZE = 10240;
	private byte[] buf;
	private StringBuilder strResultBuffer = null;
	private Timer timer = null;
	private MyTimerTask task;

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handlerMess.sendMessage(message);
		}
	}

	private Handler handlerMess = new Handler() {
		@Override
		@SuppressLint("HandlerLeak")//
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 100:
					int[] ints = ToolUtils.byte2Int(imageData);
					int scanStart = Integer.parseInt(model_s.ScanStart);
					InitChart();
					try {
						setData(ints,scanStart);
						saveImage();
					}catch (ClassCastException e1){
						APPUtils.showToast(SampleLineActivity.this, "接收数据错误");
					}catch (Exception e) {
						e.printStackTrace();
					}finally {
						ToolUtils.hiddenHUD();
						isTesting = false;
					}
					break;
			}
			super.handleMessage(msg);
		}
	};

	private boolean SendData(String message, int com) {
		if (buf != null && buf.length > 0) {
			buf = null;
		}

		if (strResultBuffer != null && strResultBuffer.length() > 0) {
			strResultBuffer = null;
		}
		buf = new byte[BUFSIZE];
		strResultBuffer = new StringBuilder(BUFSIZE);
		if (com == 3) {
			devName = "/dev/ttyAMA3";
			speed = 115200;
//			timer = new Timer();
//			if (task != null) {
//				task.cancel();
//			}
//			int select = HardwareControler.select(ToolUtils.devfd, 2, 20);
//			if (select < 1) {   //选择串口失败
////				timer = null;
//				ToolUtils.hiddenHUD();
//				return false;
//			}
			HardwareControler.read(ToolUtils.devfd, new byte[BUFSIZE], BUFSIZE);
			byte[] data = message.getBytes(Charset.forName("gbk"));
			int sendDataCount = HardwareControler.write(ToolUtils.devfd, data);
			if (sendDataCount != data.length) {
				APPUtils.showToast(SampleLineActivity.this, "数据发送失败");
				ToolUtils.hiddenHUD();
				return false;
			}

//			task = new MyTimerTask();
//			timer.schedule(task, 0, 4000);
			return true;
		}
		return  false;
	}

	private void InitChart(){


		mChart.getDescription().setEnabled(false);// no description text

		mChart.setTouchEnabled(true);// enable touch gestures

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setAxisMaximum(6000);
		leftAxis.setAxisMinimum(0);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);
		leftAxis.setDrawZeroLine(false);
		leftAxis.setLabelCount(10);
		// if disabled, scaling can be done on X0- and y-axis separately
		mChart.setPinchZoom(true);

		mChart.getAxisRight().setEnabled(true);
		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setAxisMaximum(6000);
		rightAxis.setAxisMinimum(0);
		rightAxis.enableGridDashedLine(10f, 10f, 0f);
		rightAxis.setDrawZeroLine(false);
		leftAxis.setLabelCount(10);
		// set an alternative background color
		// mChart.setBackgroundColor(Color.GRAY);

	}

	/**
	 * 设置图像数据
	 * @param imageData
	 */
	public void setData(int[] imageData, int scanStart) {

		XAxis xAxis = mChart.getXAxis();
		xAxis.setAxisMaximum(scanStart + imageData.length);
		xAxis.setAxisMinimum(scanStart);
		xAxis.enableGridDashedLine(10f, 10f, 0f);
		// if disabled, scaling can be done on X0- and y-axis separately
		mChart.setPinchZoom(true);
		xAxis.setLabelCount(20);


		ArrayList<Entry> yVals = new ArrayList<Entry>();

		for (int i = 0; i < imageData.length; i++) {
			yVals.add(new Entry(scanStart + i, imageData[i]));
		}

		LineDataSet set1;

		set1 = new LineDataSet(yVals, "DataSet 1");

		set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
		set1.setCubicIntensity(0.5f);
		set1.setDrawCircles(false);
		set1.setLineWidth(3.0f);
		set1.setCircleRadius(40f);
		set1.setCircleColor(Color.WHITE);
//			set1.setHighLightColor(Color.rgb(244, 117, 117));
		set1.setColor(Color.RED);
		set1.setDrawFilled(false);
//			set1.setFillColor(Color.WHITE);
//			set1.setFillAlpha(100);
		set1.setDrawHorizontalHighlightIndicator(false);
		set1.setFillFormatter(new IFillFormatter() {
			@Override
			public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
				return -10;
			}
		});

		// create a data object with the datasets
		LineData data = new LineData(set1);
		data.setValueTypeface(Typeface.DEFAULT);
		data.setValueTextSize(9f);
		data.setDrawValues(false);

		// set data
		mChart.setData(data);

		mChart.getLegend().setEnabled(false);

//			mChart.animateXY(2000, 2000);

		// dont forget to refresh the drawing
		mChart.invalidate();
	}


	public void ClickBack(View v) {
		this.back();
	}

	public void saveImage() {

		Bitmap map = mChart.getChartBitmap();
		if (map != null) {
			String name = ToolUtils.savePrivateBmp(map, SampleLineActivity.this);
			if (name != null && name.length() > 0) {
				ResultPhotoImgModel model = new ResultPhotoImgModel();
				model.setUrl(name);
				try {
					db.save(model);
					Toast.makeText(SampleLineActivity.this, "保存图像成功",	Toast.LENGTH_LONG).show();
				} catch (DbException e) {
					e.printStackTrace();
				}
				map.recycle();
				System.gc();
			}
		}

	}

	// 状态栏高度
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height","dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}


}
