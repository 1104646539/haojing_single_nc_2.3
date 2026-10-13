package com.tnd.multifuction1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.DialogSelectAdapter;
import com.tnd.multifuction1.model.BCheckOrg;
import com.tnd.multifuction1.model.CheckOrg;
import com.tnd.multifuction1.model.FiltrateModel;
import com.tnd.multifuction1.model.Inspector;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.model.SampleSource;

import java.util.Iterator;
import java.util.List;

public class EditDataDialog<T> extends Dialog {
    /**
     * 被检测单位
     */
    public static final int DIALOG_TYPE_BCHEKEORG = 1000;
    /**
     * 检测单位
     */
    public static final int DIALOG_TYPE_CHECKORG = 1001;
    /**
     * 商品来源
     */
    public static final int DIALOG_TYPE_SAMPLESOURCE = 1002;
    /**
     * 检测人员
     */
    public static final int DIALOG_TYPE_INSPECTOR = 1003;
    /**
     * 样品名称
     */
    public static final int DIALOG_TYPE_SAMPLENAME = 1004;
    private Context context;
    private TextView tv_title, tv_hint;
    private EditText et_content;
    private RecyclerView recyclerView;
    private View view;
    private List<CheckOrg> checkOrgs;
    private List<SampleSource> sampleSources;
    private List<BCheckOrg> bCheckOrgs;
    private List<Inspector> inspectors;
    private List<SampleName> sampleNames;
    private List<SampleName> sampleNames_filter;
    private List<CheckOrg> checkOrgs_filter;
    private List<SampleSource> sampleSources_filter;
    private List<BCheckOrg> bCheckOrgs_filter;
    private List<Inspector> inspectors_filter;
    private DialogSelectAdapter dialogSelectAdapter;
    private Button btn_add, btn_del, btn_cancel, btn_change;

    public EditDataDialog(@NonNull Context context) {
        this(context, 0);
    }

    public EditDataDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    private void init() {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_layout, null, false);
        tv_title = view.findViewById(R.id.dialog_edit_title);
        tv_hint = view.findViewById(R.id.dialog_edit_hint);
        et_content = view.findViewById(R.id.dialog_edit_content);
        recyclerView = view.findViewById(R.id.dialog_edit_list);
        btn_add = view.findViewById(R.id.dialog_edit_add);
        btn_del = view.findViewById(R.id.dialog_edit_del);
        btn_cancel = view.findViewById(R.id.dialog_edit_cancel);
        btn_change = view.findViewById(R.id.dialog_edit_change);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void showDialog(String title, int type, DialogSelectAdapter.OnItemClick onItemClick) {
        if (isShowing()) {
            dismiss();
        }
        tv_title.setText(title);
        tv_hint.setText(title);
        et_content.setText("");

        if (dialogSelectAdapter == null) {
            dialogSelectAdapter = new DialogSelectAdapter(context);
            LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(dialogSelectAdapter);
        }
        if (type == DIALOG_TYPE_BCHEKEORG) {//被检测单位
            dialogSelectAdapter.setSelect(false);
            bCheckOrgs = new BCheckOrg().findAll();
            dialogSelectAdapter.dataChange(bCheckOrgs);
            dialogSelectAdapter.setOnItemClick(new DialogSelectAdapter.OnItemClick() {
                @Override
                public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                    et_content.setText(bCheckOrgs.get(newPosition).getName());
                    et_content.setSelection(et_content.getText().toString().length());
                }
            });
        } else if (type == DIALOG_TYPE_CHECKORG) {//检测单位
            dialogSelectAdapter.setSelect(true);
            checkOrgs = new CheckOrg().findAll();
            dialogSelectAdapter.dataChange(checkOrgs);
            if (onItemClick != null) {
                dialogSelectAdapter.setOnItemClick(new DialogSelectAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                        if (oldPosition != -1) {
                            checkOrgs.get(oldPosition).setSelect(false);
                            new CheckOrg().saveOrUpdate(checkOrgs.get(oldPosition));
                        }
                        et_content.setText(checkOrgs.get(newPosition).getName());
                        et_content.setSelection(et_content.getText().toString().length());
                        checkOrgs.get(newPosition).setSelect(true);
                        onItemClick.onItemClick(data, oldPosition, newPosition);
                        new CheckOrg().saveOrUpdate(checkOrgs.get(newPosition));
                        Log.d("onItemClick", "CheckOrg:" +
                                checkOrgs.get(newPosition).isSelect());
                        for (int i = 0; i < checkOrgs.size(); i++) {
                            Log.d("tag", "name=" + checkOrgs.get(i).getName() +
                                    "isselect=" + checkOrgs.get(i).isSelect());
                        }
                    }

                });
            }
        } else if (type == DIALOG_TYPE_SAMPLESOURCE) {//商品来源
            dialogSelectAdapter.setSelect(false);
            sampleSources = new SampleSource().findAll();
            dialogSelectAdapter.dataChange(sampleSources);
            dialogSelectAdapter.setOnItemClick(new DialogSelectAdapter.OnItemClick() {
                @Override
                public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                    et_content.setText(sampleSources.get(newPosition).getName());
                    et_content.setSelection(et_content.getText().toString().length());
                }
            });
        } else if (type == DIALOG_TYPE_INSPECTOR) {//检测人员
            dialogSelectAdapter.setSelect(true);
            inspectors = new Inspector().findAll();
            dialogSelectAdapter.dataChange(inspectors);
            if (onItemClick != null) {
                dialogSelectAdapter.setOnItemClick(new DialogSelectAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                        if (oldPosition != -1) {
                            inspectors.get(oldPosition).setSelect(false);
                            new Inspector().saveOrUpdate(inspectors.get(oldPosition));
                        }
                        et_content.setText(inspectors.get(newPosition).getName());
                        et_content.setSelection(et_content.getText().toString().length());
                        inspectors.get(newPosition).setSelect(true);
                        onItemClick.onItemClick(data, oldPosition, newPosition);

                        new Inspector().saveOrUpdate(inspectors.get(newPosition));
                        Log.d("onItemClick", "inspectors:" +
                                inspectors.get(newPosition).isSelect());
                    }
                });
            }
        } else if (type == DIALOG_TYPE_SAMPLENAME) {//样品名称
            dialogSelectAdapter.setSelect(true);
            sampleNames = new SampleName().findAll();
            dialogSelectAdapter.dataChange(sampleNames);
            if (onItemClick != null) {
                dialogSelectAdapter.setOnItemClick(new DialogSelectAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                        if (oldPosition != -1) {
                            sampleNames.get(oldPosition).setSelect(false);
                            new SampleName().saveOrUpdate(sampleNames.get(oldPosition));
                        }
                        et_content.setText(sampleNames.get(newPosition).getName());
                        et_content.setSelection(et_content.getText().toString().length());
                        sampleNames.get(newPosition).setSelect(true);
                        onItemClick.onItemClick(data, oldPosition, newPosition);

                        new SampleName().saveOrUpdate(sampleNames.get(newPosition));
                        Log.d("onItemClick", "sampleNames:" +
                                sampleNames.get(newPosition).getName());
                    }
                });
            }
        }

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                String str = "";
//                if (s == null || s.toString().equals("") || s.toString().trim().equals("")) {
//                    str = "";
//                } else {
//                    str = s.toString().trim();
//                }
//                if (type == DIALOG_TYPE_BCHEKEORG ||
//                        type == DIALOG_TYPE_SAMPLESOURCE) {
//                    onAdapterFilter(str, type);
//                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_content.getText().toString();
                if (isNull(str)) {
                    Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type == DIALOG_TYPE_BCHEKEORG) {//被检测单位
                    if (new BCheckOrg().save(new BCheckOrg(str))) {//保存成功
                        bCheckOrgs = new BCheckOrg().findAll();
                        dialogSelectAdapter.dataChange(bCheckOrgs);
                        Log.d("BCheckOrg", "size:" + bCheckOrgs.size());
                    }
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_CHECKORG) {//检测单位
                    if (new CheckOrg().save(new CheckOrg(str))) {//保存成功
                        checkOrgs = new CheckOrg().findAll();
                        dialogSelectAdapter.dataChange(checkOrgs);
                    }
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_SAMPLESOURCE) {//商品来源
                    if (new SampleSource().save(new SampleSource(str))) {//保存成功
                        sampleSources = new SampleSource().findAll();
                        dialogSelectAdapter.dataChange(sampleSources);
                    }
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_INSPECTOR) {//检测人员
                    if (new Inspector().save(new Inspector(str))) {//保存成功
                        inspectors = new Inspector().findAll();
                        dialogSelectAdapter.dataChange(inspectors);
                    }
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_SAMPLENAME) {//样品名称
                    if (new SampleName().save(new SampleName(str))) {//保存成功
                        sampleNames = new SampleName().findAll();
                        dialogSelectAdapter.dataChange(sampleNames);
                    }
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                }
                et_content.setText("");
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltrateModel filtrateModel = dialogSelectAdapter.getSelect();
                if (filtrateModel == null) {
                    Toast.makeText(context, "请选择删除项目", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type == DIALOG_TYPE_BCHEKEORG) {//被检测单位
                    if (new BCheckOrg().delete((BCheckOrg) filtrateModel)) {//保存成功
                        bCheckOrgs = new BCheckOrg().findAll();
                        dialogSelectAdapter.dataChange(bCheckOrgs);
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_CHECKORG) {//检测单位
                    if (new CheckOrg().delete((CheckOrg) filtrateModel)) {//保存成功
                        checkOrgs = new CheckOrg().findAll();
                        dialogSelectAdapter.dataChange(checkOrgs);
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_SAMPLESOURCE) {//商品来源
                    if (new SampleSource().delete((SampleSource) filtrateModel)) {//保存成功
                        sampleSources = new SampleSource().findAll();
                        dialogSelectAdapter.dataChange(sampleSources);
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_INSPECTOR) {//检测人员
                    if (new Inspector().delete((Inspector) filtrateModel)) {//保存成功
                        inspectors = new Inspector().findAll();
                        dialogSelectAdapter.dataChange(inspectors);
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                }else if (type == DIALOG_TYPE_SAMPLENAME) {//样品名称
                    if (new SampleName().delete((SampleName) filtrateModel)) {//保存成功
                        sampleNames = new SampleName().findAll();
                        dialogSelectAdapter.dataChange(sampleNames);
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                }
                et_content.setText("");
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltrateModel filtrateModel = dialogSelectAdapter.getSelect();
                if (filtrateModel == null) {
                    Toast.makeText(context, "请选择一行", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_content.getText() == null || et_content.getText().toString() == null ||
                        et_content.getText().toString().trim().equals("")) {
                    Toast.makeText(context, "请输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                String changeName = et_content.getText().toString().trim();

                if (type == DIALOG_TYPE_BCHEKEORG) {//被检测单位
//                    new BCheckOrg().saveOrUpdate((BCheckOrg) filtrateModel);
                    bCheckOrgs.get(dialogSelectAdapter.getSelectPosition()).bco_name = changeName;
                    new BCheckOrg().saveOrUpdate(bCheckOrgs.get(dialogSelectAdapter.getSelectPosition()));
//                    bCheckOrgs = new BCheckOrg().findAll();
//                    dialogSelectAdapter.dataChange(bCheckOrgs);
                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
                    Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_CHECKORG) {//检测单位
                    checkOrgs.get(dialogSelectAdapter.getSelectPosition()).co_name = changeName;
                    new CheckOrg().saveOrUpdate(checkOrgs.get(dialogSelectAdapter.getSelectPosition()));
//                    checkOrgs = new CheckOrg().findAll();
//                    dialogSelectAdapter.dataChange(checkOrgs);
                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
                    Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_SAMPLESOURCE) {//商品来源
                    sampleSources.get(dialogSelectAdapter.getSelectPosition()).ss_name = changeName;
                    new SampleSource().saveOrUpdate(sampleSources.get(dialogSelectAdapter.getSelectPosition()));
//                    dialogSelectAdapter.dataChange(inspectors);
                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
                    Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
                } else if (type == DIALOG_TYPE_INSPECTOR) {//检测人员
                    inspectors.get(dialogSelectAdapter.getSelectPosition()).i_name = changeName;
                    new Inspector().saveOrUpdate(inspectors.get(dialogSelectAdapter.getSelectPosition()));
                    //保存成功
                    inspectors = new Inspector().findAll();
//                    dialogSelectAdapter.dataChange(inspectors);
                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
//                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
                    Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
                }else if (type == DIALOG_TYPE_SAMPLENAME) {//样品名称
                    sampleNames.get(dialogSelectAdapter.getSelectPosition()).sampleName = changeName;
                    new SampleName().saveOrUpdate(sampleNames.get(dialogSelectAdapter.getSelectPosition()));
                    //保存成功
                    sampleNames = new SampleName().findAll();
//                    dialogSelectAdapter.dataChange(inspectors);
                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
//                    dialogSelectAdapter.notifyItemChanged(dialogSelectAdapter.getSelectPosition());
                    Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
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

        setContentView(view);
    }

    private void onAdapterFilter(String str, int type) {
        if (str.equals("")) {
            if (type == DIALOG_TYPE_BCHEKEORG) {//被检测单位
                bCheckOrgs = new BCheckOrg().findAll();
                dialogSelectAdapter.dataChange(bCheckOrgs);
                Log.d("onAdapterFilter", "size=" + bCheckOrgs.size());
            } else if (type == DIALOG_TYPE_CHECKORG) {//检测单位
                checkOrgs = new CheckOrg().findAll();
                dialogSelectAdapter.dataChange(checkOrgs);
                Log.d("onAdapterFilter", "size=" + checkOrgs.size());
            } else if (type == DIALOG_TYPE_SAMPLESOURCE) {//商品来源
                sampleSources = new SampleSource().findAll();
                dialogSelectAdapter.dataChange(sampleSources);
            } else if (type == DIALOG_TYPE_INSPECTOR) {//检测人员
                inspectors = new Inspector().findAll();
                dialogSelectAdapter.dataChange(inspectors);
            }
        } else {
            if (type == DIALOG_TYPE_BCHEKEORG) {//被检测单位
                dialogSelectAdapter.dataChange(filter(str, (List<T>) bCheckOrgs));
            } else if (type == DIALOG_TYPE_CHECKORG) {//检测单位
                dialogSelectAdapter.dataChange(filter(str, (List<T>) checkOrgs));
            } else if (type == DIALOG_TYPE_SAMPLESOURCE) {//商品来源
                dialogSelectAdapter.dataChange(filter(str, (List<T>) sampleSources));
            } else if (type == DIALOG_TYPE_INSPECTOR) {//检测人员
                dialogSelectAdapter.dataChange(filter(str, (List<T>) inspectors));
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

    public boolean isNull(String str) {
        return str == null || str.length() == 0;
    }

    public interface OnSelectItem {
        void onSelectItem(String name);
    }

}

