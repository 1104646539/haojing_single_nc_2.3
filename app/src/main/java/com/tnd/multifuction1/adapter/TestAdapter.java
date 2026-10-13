package com.tnd.multifuction1.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.BCheckOrg;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.FiltrateModel;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.model.SampleSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TestAdapter<T> extends RecyclerView.Adapter {
    public static String TAG = TestAdapter.class.getSimpleName();
    private List<CheckResult> checkResults = new ArrayList<>();
    private Context context;
    public static int DIALOG_CHECKED_ORG = 1;//被检测单位
    public static int DIALOG_SAMPLE_SOURCE = 2;//商品来源
    public static int DIALOG_SAMPLE_NAME = 3;//样品名称
    static List<BCheckOrg> bcheckedOrg;//被检测单位
    static List<SampleSource> sampleSources;//商品来源
    static List<SampleName> sampleNames;//样品名称
    static List<BCheckOrg> bcheckedOrg_s = new ArrayList<>();//被检测单位
    static List<SampleSource> sampleSources_s = new ArrayList<>();//商品来源
    static List<SampleName> sampleNames_s = new ArrayList<>();//样品名称

    public List<CheckResult> getData() {
        return checkResults;
    }

    /**
     * 是否选择了一通道对照
     *
     * @return
     */
    public boolean isSelectContrast() {
        return getSelectedCount() > 0;
    }

    public void setData(List<CheckResult> checkResults) {
        this.checkResults.clear();
        if (checkResults != null) {
            this.checkResults.addAll(checkResults);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_test, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
//            viewHolder.tv_sample_aisle_number.setSelected(checkResults.get(position).isSelected);
            viewHolder.item_view.setSelected(checkResults.get(position).isSelected);

            Log.d("onBindViewHolder", "position=" + position + "isSelected=" + checkResults.get(position).isSelected + "size=" + checkResults.size());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        CheckResult checkResult = checkResults.get(position);
        viewHolder.tv_sample_aisle_number.setText(checkResult.channel != null ? checkResult.channel : "");
        viewHolder.tv_sample_name.setText(checkResult.sampleName != null ? checkResult.sampleName : "");
        viewHolder.tv_bchecked_org.setText(checkResult.bcheckedOrganization != null ? checkResult.bcheckedOrganization : "");
        viewHolder.tv_sample_source.setText(checkResult.sampleSource != null ? checkResult.sampleSource : "");
        viewHolder.et_sample_number.setText(checkResult.sampleNum != null ? checkResult.sampleNum : "");
        viewHolder.tv_yzl.setText(checkResult.testValue != null ? checkResult.testValue : "");
        viewHolder.tv_judge.setText(checkResult.resultJudge != null ? checkResult.resultJudge : "");
        viewHolder.et_sample_weight.setText(checkResult.weight != null ? checkResult.weight : "");


        viewHolder.item_view.setSelected(checkResult.isSelected);
        viewHolder.tv_sample_aisle_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkResults.get(position).isSelected = !checkResults.get(position).isSelected;
                viewHolder.tv_sample_aisle_number.setSelected(checkResults.get(position).isSelected);
                viewHolder.item_view.setSelected(checkResults.get(position).isSelected);
                if (onAllSelectListener != null) {
                    onAllSelectListener.onAllSelect(checkAllSelect());
                }
            }
        });

        viewHolder.tv_sample_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(position, DIALOG_SAMPLE_NAME, viewHolder);
            }
        });
        viewHolder.tv_sample_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(position, DIALOG_SAMPLE_SOURCE, viewHolder);
            }
        });
        viewHolder.tv_bchecked_org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog(position, DIALOG_CHECKED_ORG, viewHolder);
            }
        });

        viewHolder.et_sample_weight.setTag(position);
        viewHolder.sample_weight_ChangeWatch = new MyTextChangeWatch(viewHolder) {
            @Override
            void onChangedTextListener(ViewHolder viewHolder, String str) {
                int position = (Integer) viewHolder.et_sample_weight.getTag();
                checkResults.get(position).weight = str;
                Log.d("viewHolder", checkResults.toString());
            }
        };
        viewHolder.et_sample_weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 在EditText获取到焦点时，才给它添加监听事件，同时处理完数据后，要移除监听，必须要这样做，不然数据会错乱
                    viewHolder.et_sample_weight.addTextChangedListener(viewHolder.sample_weight_ChangeWatch);
                } else {
                    viewHolder.et_sample_weight.removeTextChangedListener(viewHolder.sample_weight_ChangeWatch);
                }
            }
        });

        viewHolder.et_sample_number.setTag(position);
        viewHolder.sample_number_ChangeWatch = new MyTextChangeWatch(viewHolder) {
            @Override
            void onChangedTextListener(ViewHolder viewHolder, String str) {
                int position = (Integer) viewHolder.et_sample_number.getTag();
                checkResults.get(position).sampleNum = str;
                Log.d("viewHolder", checkResults.toString());
            }
        };
        viewHolder.et_sample_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 在EditText获取到焦点时，才给它添加监听事件，同时处理完数据后，要移除监听，必须要这样做，不然数据会错乱
                    viewHolder.et_sample_number.addTextChangedListener(viewHolder.sample_number_ChangeWatch);
                } else {
                    viewHolder.et_sample_number.removeTextChangedListener(viewHolder.sample_number_ChangeWatch);
                }
            }
        });

    }

    private boolean checkAllSelect() {
        for (int i = 0; i < checkResults.size(); i++) {
            if (!checkResults.get(i).isSelected) {
                return false;
            }
        }
        return true;
    }

    public void showSelectDialog(int sposition, int type, ViewHolder viewHolder) {
        if (viewHolder.dialog == null) {
//            Dialog.Builder builder = new AlertDialog.Builder(context);
//            viewHolder.dialog = builder.create();

            viewHolder.dialog = new Dialog(context);

            viewHolder.dialogContentView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_filtrate_select, null, false);
            viewHolder.lv = viewHolder.dialogContentView.findViewById(R.id.lv);
            viewHolder.et_content = viewHolder.dialogContentView.findViewById(R.id.et_content);
            viewHolder.filtrateAdapter = new FiltrateAdapter(context);

            viewHolder.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        }
        viewHolder.et_content.setText("");
        boolean isclear = true;
        if (type == DIALOG_CHECKED_ORG) {
//            if (bcheckedOrg == null || bcheckedOrg.isEmpty()) {
            bcheckedOrg = new BCheckOrg().findAll();
            bcheckedOrg_s.clear();
            if (bcheckedOrg != null) {
                bcheckedOrg_s.addAll(bcheckedOrg);
            }
//            }
            viewHolder.filtrateAdapter.setData(bcheckedOrg);
        } else if (type == DIALOG_SAMPLE_SOURCE) {
//            if (sampleSources == null || sampleSources.isEmpty()) {
            sampleSources = new SampleSource().findAll();
            sampleSources_s.clear();
            if (sampleSources != null) {
                sampleSources_s.addAll(sampleSources);
            }
//            }
            viewHolder.filtrateAdapter.setData(sampleSources);
        } else if (type == DIALOG_SAMPLE_NAME) {
//            if (sampleNames == null || sampleNames.isEmpty()) {
//            try {
//                sampleNames = DbHelper.GetInstance().findAll(Selector.from(SampleName.class)
//                        .orderBy("time", true)
//                );
//            } catch (DbException e) {
//                e.printStackTrace();
//            }
//            sampleNames_s.clear();
//            if (sampleNames != null) {
//                sampleNames_s.addAll(sampleNames);
//            }
//            }
//            viewHolder.filtrateAdapter.setData(sampleNames);
            if (sampleNames_s == null || sampleNames_s.isEmpty()) {
                try {
                    List<SampleName> sns = DbHelper.GetInstance().findAll(Selector.from(SampleName.class)
                            .orderBy("time", true));
                    if (sns == null) {
                        sns = new ArrayList<>();
                    }
                    if (sampleNames_s == null) {
                        sampleNames_s = new ArrayList<>();
                    } else {
                        sampleNames_s.clear();
                    }
                    sampleNames_s.addAll(sns);
                } catch (DbException e) {
                    e.printStackTrace();
                }
//                sampleNames_s.clear();
                if (sampleNames == null) {
                    sampleNames = new ArrayList<>();
                } else {
                    sampleNames.clear();
                }
                if (sampleNames_s != null) {
                    sampleNames.addAll(sampleNames_s);
                }
            } else {
                sampleNames.clear();
                sampleNames.addAll(sampleNames_s);
            }
            viewHolder.filtrateAdapter.setData(sampleNames);
        }
        viewHolder.lv.setAdapter(viewHolder.filtrateAdapter);
        viewHolder.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == DIALOG_CHECKED_ORG) {
                    checkResults.get(sposition).bcheckedOrganization = bcheckedOrg.get(position).getName();
                    viewHolder.tv_bchecked_org.setText(checkResults.get(sposition).bcheckedOrganization);
                } else if (type == DIALOG_SAMPLE_SOURCE) {
                    checkResults.get(sposition).sampleSource = sampleSources.get(position).getName();
                    viewHolder.tv_sample_source.setText(checkResults.get(sposition).sampleSource);
                } else if (type == DIALOG_SAMPLE_NAME) {
                    checkResults.get(sposition).sampleName = sampleNames.get(position).getName();
                    checkResults.get(sposition).sampleNum = "" + sampleNames.get(position).getSampleNumber();
                    viewHolder.tv_sample_name.setText(checkResults.get(sposition).sampleName);
                    viewHolder.et_sample_number.setText(checkResults.get(sposition).sampleNum);
//                    checkResults.get(sposition).sampleNum = sampleNames.get(position).getSampleNumber();
//                    viewHolder.et_sample_number.setText(checkResults.get(sposition).sampleNum);
                    checkResults.get(sposition).sn = sampleNames.get(position);
                    //将最近选择的排列在最前面
                    sampleNames.get(position).setTime(new Date().getTime());
                    new SampleName().saveOrUpdate(sampleNames.get(position));
                    SampleName sn = sampleNames.get(position);
                    if (position < sampleNames_s.size()) {
                        sampleNames_s.remove(sn);
                        sampleNames_s.add(0, sn);
                    }
                    sampleNames.remove(position);
                    sampleNames.add(0, sn);
//                    Log.d(TAG, "选择了样品名是=" + sampleNames.get(sposition).sampleName + "position=" + sposition);
                }
                if (viewHolder.dialog != null && viewHolder.dialog.isShowing()) {
                    viewHolder.dialog.dismiss();
                }
            }
        });

        viewHolder.et_content.setSingleLine();
        viewHolder.et_content.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        viewHolder.et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (type == DIALOG_CHECKED_ORG) {
                        viewHolder.tv_bchecked_org.setText(viewHolder.et_content.getText());
                    } else if (type == DIALOG_SAMPLE_SOURCE) {
                        viewHolder.tv_sample_source.setText(viewHolder.et_content.getText());
                    } else if (type == DIALOG_SAMPLE_NAME) {
                        viewHolder.tv_sample_name.setText(viewHolder.et_content.getText());
                    }

                    handled = true;

                    /*隐藏软键盘*/
//                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (inputMethodManager.isActive()) {
//                        inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
//                    }
                    viewHolder.dialog.dismiss();
                    viewHolder.et_content.setText("");
                }
                return handled;
            }
        });

        viewHolder.dialog.show();
        viewHolder.dialog.setContentView(viewHolder.dialogContentView);

        viewHolder.et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = "";
                if (s == null || s.toString().equals("") || s.toString().trim().equals("")) {
                    str = "";
                } else {
                    str = s.toString().trim();
                }
                onAdapterFilter(str, type, viewHolder);
            }
        });
//        viewHolder.filtrateAdapter.notifyDataSetChanged();
    }

    private void onAdapterFilter(String str, int type, ViewHolder viewHolder) {
        if (str == null || str.equals("")) {
            if (type == DIALOG_CHECKED_ORG) {
//                viewHolder.bcheckedOrg = new BCheckOrg().findAll();
                bcheckedOrg = new ArrayList<>();
                bcheckedOrg.addAll(bcheckedOrg_s);
                viewHolder.filtrateAdapter.setData(bcheckedOrg);
            } else if (type == DIALOG_SAMPLE_SOURCE) {
//                viewHolder.sampleSources = new SampleSource().findAll();
                sampleSources = new ArrayList<>();
                sampleSources.addAll(sampleSources_s);
                viewHolder.filtrateAdapter.setData(sampleSources);
            } else if (type == DIALOG_SAMPLE_NAME) {
                sampleNames = new ArrayList<>();
                sampleNames.addAll(sampleNames_s);
                viewHolder.filtrateAdapter.setData(sampleNames);
            }
        } else {
            if (type == DIALOG_CHECKED_ORG) {
//                bcheckedOrg = new BCheckOrg().findAll();
                bcheckedOrg = new ArrayList<>();
                bcheckedOrg.addAll(bcheckedOrg_s);
                viewHolder.filtrateAdapter.setData(filter(str, (List<T>) bcheckedOrg));
            } else if (type == DIALOG_SAMPLE_SOURCE) {
//                sampleSources = new SampleSource().findAll();
                sampleSources = new ArrayList<>();
                sampleSources.addAll(sampleSources_s);
                viewHolder.filtrateAdapter.setData(filter(str, (List<T>) sampleSources));
            } else if (type == DIALOG_SAMPLE_NAME) {
//                sampleNames = new SampleName().findAll();
                sampleNames = new ArrayList<>();
                sampleNames.addAll(sampleNames_s);
                viewHolder.filtrateAdapter.setData(filter(str, (List<T>) sampleNames));
            }
        }
    }

    private List<T> filter(String str, List<T> data) {
        List<FiltrateModel> filtrateModels = (List<FiltrateModel>) data;
        Iterator<FiltrateModel> iterable = filtrateModels.iterator();
        while (iterable.hasNext()) {
            FiltrateModel m
                    = iterable.next();
            if (!m.getName().contains(str)) {
                iterable.remove();
            }
        }
        return (List<T>) filtrateModels;
    }

    @Override
    public int getItemCount() {
        return checkResults.size();
    }

    public TestAdapter(Context context) {
        this.context = context;
    }

    public TestAdapter(Context context, List<CheckResult> checkResults) {
        this.context = context;
        this.checkResults = checkResults;
    }

    //全选
    public void setAllSelect(boolean selected) {
        Log.d("setAllSelect", "setAllSelect selected=" + selected);
        for (int i = 0; i < checkResults.size(); i++) {
            checkResults.get(i).isSelected = selected;
            notifyItemChanged(i, selected ? 1 : 0);
        }
    }

    //一共选择了多少通道
    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < checkResults.size(); i++) {
            if (checkResults.get(i).isSelected) {
                count++;
            }
        }
        return count;
    }

    //一共选择了的通道数组
    public int[] getSelectedArray() {
        int[] array = new int[getSelectedCount()];
        int j = 0;
        for (int i = 0; i < checkResults.size(); i++) {
            if (checkResults.get(i).isSelected) {
                array[j] = i + 1;
                j++;
            }
        }
        return array;
    }

    //一共选择了的通道列表
    public List<CheckResult> getSelectedList() {
        List<CheckResult> cr = new ArrayList<>();
        for (int i = 0; i < checkResults.size(); i++) {
            if (checkResults.get(i).isSelected) {
                cr.add(checkResults.get(i));
            }
        }
        return cr;
    }

    /**
     * 验证
     *
     * @return
     */
    public boolean verification() {
        for (int i = 0; i < checkResults.size(); i++) {
            CheckResult checkResult = checkResults.get(i);
            if (checkResult.isSelected) {
//                if (!isNotNull(checkResult.sampleNum)) {
//                    showToast("请输入" + checkResult.channel + "的样品编号");
//                    return false;
//                } else
                if (!isNotNull(checkResult.sampleName)) {
                    showToast("请输入" + checkResult.channel + "的样品名称");
                    return false;
                }
//                    else if (!isNotNull(checkResult.bcheckedOrganization)) {
//                    showToast("请输入" + checkResult.channel + "的被检单位");
//                    return false;
//                } else if (!isNotNull(checkResult.sampleSource)) {
//                    showToast("请输入" + checkResult.channel + "的商品来源");
//                    return false;
//                } else if (!isNotNull(checkResult.weight)) {
//                    showToast("请输入" + checkResult.channel + "的重量");
//                    return false;
//                }
            }
        }
        return true;
    }

    private void showToast(String text) {
        if (text != null)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private boolean isNotNull(String str) {
        if (str != null) {
//            if (!str.equals("")) {
            return true;
//            }
        }
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //通道名 样品名称 检测单位 商品来源 抑制率 检测结果
        TextView tv_sample_aisle_number, tv_sample_name, tv_bchecked_org, tv_sample_source, tv_yzl, tv_judge;
        //样品编号   重量
        EditText et_sample_number, et_sample_weight;
        Dialog dialog;
        View dialogContentView;
        FiltrateAdapter filtrateAdapter;
        ListView lv;
        EditText et_content;
        //        List<BCheckOrg> bcheckedOrg;//被检测单位
//        List<SampleSource> sampleSources;//商品来源
//        List<SampleName> sampleNames;//样品名称
//        List<BCheckOrg> bcheckedOrg_s;//被检测单位
//        List<SampleSource> sampleSources_s;//商品来源
//        List<SampleName> sampleNames_s;//样品名称
        View item_view;
        ImageView iv_cb;
        TextWatcher sample_number_ChangeWatch;
        TextWatcher sample_weight_ChangeWatch;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_sample_aisle_number = itemView.findViewById(R.id.tv_sample_aisle_number);
            tv_sample_name = itemView.findViewById(R.id.tv_sample_name);
            tv_bchecked_org = itemView.findViewById(R.id.tv_bchecked_org);
            tv_sample_source = itemView.findViewById(R.id.tv_sample_source);
            et_sample_number = itemView.findViewById(R.id.et_sample_number);
            tv_yzl = itemView.findViewById(R.id.et_yzl);
            tv_judge = itemView.findViewById(R.id.et_judge);
            et_sample_weight = itemView.findViewById(R.id.et_sample_weight);
            item_view = itemView.findViewById(R.id.item_view);
            iv_cb = itemView.findViewById(R.id.iv_cb);
//            et_sample_number.setEnabled(false);
//            bcheckedOrg_s = new BCheckOrg().findAll();
//            sampleSources_s = new SampleSource().findAll();
//            sampleNames_s = new SampleName().findAll();
//            bcheckedOrg = new ArrayList<>();
//            sampleSources = new ArrayList<>();
//            sampleNames = new ArrayList<>();
//            bcheckedOrg.addAll(bcheckedOrg_s);
//            sampleSources.addAll(sampleSources_s);
//            sampleNames.addAll(sampleNames_s);
        }
    }

    OnAllSelectListener onAllSelectListener;

    abstract class MyTextChangeWatch implements TextWatcher {
        private int position;
        ViewHolder viewHolder;

        public MyTextChangeWatch(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        public void upDataPosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
//            checkResults.get(position).sampleNum = s.toString();
            onChangedTextListener(viewHolder, s.toString());
        }

        abstract void onChangedTextListener(ViewHolder viewHolder, String str);
//        OnChangedTextListener onChangedTextListener;
//
//        public void setOnChangedTextListener(OnChangedTextListener onChangedTextListener) {
//            this.onChangedTextListener = onChangedTextListener;
//        }
    }

    public interface OnChangedTextListener {
        void onChangedTextListener(ViewHolder viewHolder, String str);
    }

    public void setOnAllSelectListener(OnAllSelectListener onAllSelectListener) {
        this.onAllSelectListener = onAllSelectListener;
    }

    public interface OnAllSelectListener {
        void onAllSelect(boolean isAllSelect);
    }
}
