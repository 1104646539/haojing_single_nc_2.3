package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.multifuction1.util.ToolUtils;

import java.util.ArrayList;
import java.util.List;


public class DebugActivity extends Activity implements View.OnClickListener {

    private Activity act;
    private ListView lv;
    private List<DebugInfoModel> list;
    private DebugDataAdapter adapter;
    private Button btnGetData;
    private Button btnClearWindow;
    private Button btnReturn;
    private String TAG = "DebugActivity";
    private EditText etStep;
    private Button btnFitStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        act = this;
        initView();
    }

    private void initView() {

        lv = findViewById(R.id.lv);
        btnGetData = findViewById(R.id.btn_get_data);
        btnClearWindow = findViewById(R.id.btn_clear_window);
        btnReturn = findViewById(R.id.btn_return);
        btnGetData.setOnClickListener(this);
        btnClearWindow.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        etStep = findViewById(R.id.et_step);
        btnFitStep = findViewById(R.id.btn_fit_step);
        btnFitStep.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_get_data:
                enableControls(false);
                getData();
                break;
            case R.id.btn_clear_window:
                adapter = null;
                lv.setAdapter(null);
                break;
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_fit_step:
                if(!validateStep()) return;
                enableControls(false);
                fitStep();
                break;
        }
    }

    private void enableControls(boolean enable) {
        btnFitStep.setEnabled(enable);
        btnGetData.setEnabled(enable);
    }

    private void fitStep() {

        byte[] data = ("PYJZ," + etStep.getText() + "\0").getBytes();
        if (!SerialUtils.COM3_SendData(data)) {
            APPUtils.showToast(this, "数据发送失败");
            enableControls(true);
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] rec = SerialUtils.COM3_RevData();
                if (rec == null || rec.length == 0) {
                    APPUtils.showToast(act, "校正步数失败");
                } else {
                    String str = new String(rec);
                    if ("PYJZ_OK\n".equals(str)) {
                        APPUtils.showToast(act, "校正步数成功");
                    } else {
                        APPUtils.showToast(act, "校正步数失败");
                    }
                }
                enableControls(true);
            }
        }, 2500);
    }

    private boolean validateStep() {

        if (TextUtils.isEmpty(etStep.getText().toString())) {
            APPUtils.showToast(this, "步数不能为空");
            return false;
        }
        int i;
        try {
            i = Integer.parseInt(etStep.getText().toString());
            if (i > 1000 || i < -1000) {
                APPUtils.showToast(this, "步数必须大于-1000且小于1000", true);
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            APPUtils.showToast(this, "步数格式不正确", true);
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private void getData() {

        if (Global.DEBUG) Log.i(TAG, "发送数据====" + new String(Global.TEST_INSTRUCTION));
        if (!SerialUtils.COM3_SendData(Global.TEST_INSTRUCTION)) {
            sendEmptyMessage(false);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    byte[] response = SerialUtils.COM3_RevData();
                    if (response == null || response.length == 0 || response[response.length - 1] != '\n') {
                        sendEmptyMessage(false);
                    } else {
                        DebugInfoModel debugInfo = dealTestData(response);
                        if(debugInfo == null){
                            sendEmptyMessage(false);
                        }else{
                            if (adapter == null) {
                                list = new ArrayList<>();
                                list.add(debugInfo);
                                adapter = new DebugDataAdapter();
                                lv.setAdapter(adapter);
                            } else {
                                list.add(debugInfo);
                                adapter.notifyDataSetChanged();
                            }
                            lv.setSelection(adapter.getCount() - 1);
                            sendEmptyMessage(true);
                        }
                    }
                }
            }, 6500);
        }
    }
    private DebugInfoModel dealTestData(byte[] response) {

        String result = new String(response).replace("OK", "").replace("\n", "");
        String[] st = result.split("\\|");
        if (st.length != Global.CHANNEL_COUNT) {
            return null;
        }

        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < st.length; i++) {
            String[] s = st[i].split(",");
            if (s.length != 3) {
                return null;
            }
            list.add(s);
        }
        DebugInfoModel debugInfo = new DebugInfoModel();
        try {
            for (int j = 0; j < list.size(); j++) {
                Integer[] in = new Integer[4];
                for (int i = 0; i < list.get(j).length; i++) {
                    in[i] = Integer.parseInt(list.get(j)[i]);
                }
                debugInfo.grayList.add(ToolUtils.computeGrayValue(in[0], in[1], in[2]));
                debugInfo.bgColorList.add(Color.rgb(in[0], in[1], in[2]));
            }
            return debugInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendEmptyMessage(boolean isSucc) {

        enableControls(true);
        String info = isSucc ? "获取数据成功" : "获取数据失败";
        APPUtils.showToast(act, info);
    }

    private class DebugDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list == null ? 0 :list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = View.inflate(act, R.layout.item_debug, null);
                vh.tv1 = convertView.findViewById(R.id.tv1);
                vh.tv2 = convertView.findViewById(R.id.tv2);
                vh.tv3 = convertView.findViewById(R.id.tv3);
                vh.tv4 = convertView.findViewById(R.id.tv4);
                vh.tv5 = convertView.findViewById(R.id.tv5);
                vh.tv6 = convertView.findViewById(R.id.tv6);
                vh.tv7 = convertView.findViewById(R.id.tv7);
                vh.tv8 = convertView.findViewById(R.id.tv8);
                vh.tv9 = convertView.findViewById(R.id.tv9);
                vh.tv10 = convertView.findViewById(R.id.tv10);
                vh.tv11 = convertView.findViewById(R.id.tv11);
                vh.tv12 = convertView.findViewById(R.id.tv12);

                vh.iv1 = convertView.findViewById(R.id.iv1);
                vh.iv2 = convertView.findViewById(R.id.iv2);
                vh.iv3 = convertView.findViewById(R.id.iv3);
                vh.iv4 = convertView.findViewById(R.id.iv4);
                vh.iv5 = convertView.findViewById(R.id.iv5);
                vh.iv6 = convertView.findViewById(R.id.iv6);
                vh.iv7 = convertView.findViewById(R.id.iv7);
                vh.iv8 = convertView.findViewById(R.id.iv8);
                vh.iv9 = convertView.findViewById(R.id.iv9);
                vh.iv10 = convertView.findViewById(R.id.iv10);
                vh.iv11 = convertView.findViewById(R.id.iv11);
                vh.iv12 = convertView.findViewById(R.id.iv12);
                convertView.setTag(vh);
            }
            vh = (ViewHolder) convertView.getTag();
            DebugInfoModel debugInfo = list.get(position);

            vh.tv1.setText(debugInfo.grayList.get(0) + "");
            vh.tv2.setText(debugInfo.grayList.get(1) + "");
            vh.tv3.setText(debugInfo.grayList.get(2) + "");
            vh.tv4.setText(debugInfo.grayList.get(3) + "");
            vh.tv5.setText(debugInfo.grayList.get(4) + "");
            vh.tv6.setText(debugInfo.grayList.get(5) + "");
            vh.tv7.setText(debugInfo.grayList.get(6) + "");
            vh.tv8.setText(debugInfo.grayList.get(7) + "");
            vh.tv9.setText(debugInfo.grayList.get(8) + "");
            vh.tv10.setText(debugInfo.grayList.get(9) + "");
            vh.tv11.setText(debugInfo.grayList.get(10) + "");
            vh.tv12.setText(debugInfo.grayList.get(11) + "");

            vh.iv1.setBackgroundColor(debugInfo.bgColorList.get(0));
            vh.iv2.setBackgroundColor(debugInfo.bgColorList.get(1));
            vh.iv3.setBackgroundColor(debugInfo.bgColorList.get(2));
            vh.iv4.setBackgroundColor(debugInfo.bgColorList.get(3));
            vh.iv5.setBackgroundColor(debugInfo.bgColorList.get(4));
            vh.iv6.setBackgroundColor(debugInfo.bgColorList.get(5));
            vh.iv7.setBackgroundColor(debugInfo.bgColorList.get(6));
            vh.iv8.setBackgroundColor(debugInfo.bgColorList.get(7));
            vh.iv9.setBackgroundColor(debugInfo.bgColorList.get(8));
            vh.iv10.setBackgroundColor(debugInfo.bgColorList.get(9));
            vh.iv11.setBackgroundColor(debugInfo.bgColorList.get(10));
            vh.iv12.setBackgroundColor(debugInfo.bgColorList.get(11));
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        TextView tv5;
        TextView tv6;
        TextView tv7;
        TextView tv8;
        TextView tv9;
        TextView tv10;
        TextView tv11;
        TextView tv12;

        ImageView iv1;
        ImageView iv2;
        ImageView iv3;
        ImageView iv4;
        ImageView iv5;
        ImageView iv6;
        ImageView iv7;
        ImageView iv8;
        ImageView iv9;
        ImageView iv10;
        ImageView iv11;
        ImageView iv12;
    }

    class DebugInfoModel {

        public List<Integer> bgColorList = new ArrayList<>(Global.CHANNEL_COUNT);
        public List<Double> grayList = new ArrayList<>(Global.CHANNEL_COUNT);
    }
}
