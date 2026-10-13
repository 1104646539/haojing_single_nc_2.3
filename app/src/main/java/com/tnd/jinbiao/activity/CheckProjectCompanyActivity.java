package com.tnd.jinbiao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.adapter.CheckPersionAdapter;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.PeopleModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;

public class CheckProjectCompanyActivity extends BaseActivity {

    private int CHECK_UNIT = 1;
    private int CHECKER = 2;
    private int SAMPLE_UNIT = 3;
    private int source;// 来源1标识单位，2标识检验员，3表示抽样单位
    private TextView title;
    private TextView name;
    private ListView listview;
    private List<PeopleModel> list = new ArrayList<>();
    private CheckPersionAdapter adapter = null;
    private DbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_company_main);
        db = DbHelper.GetInstance();
        source = Integer.parseInt(getIntent().getStringExtra("source"));

        title = (TextView) findViewById(R.id.company_title);
        name = (TextView) findViewById(R.id.company_name);
        listview = (ListView) findViewById(R.id.company_listview);

        if (source == CHECK_UNIT) {
            title.setText("检测单位编辑");
            name.setText("检测单位名称");
        } else if (source == CHECKER) {
            title.setText("检验员编辑");
            name.setText("检验员名称");
        } else {
            title.setText("商品来源编辑");
            name.setText("商品来源名称");
        }
        list = new ArrayList<PeopleModel>();
        query();
        adapter = new CheckPersionAdapter(this, list);
        listview.setAdapter(adapter);
    }

    private void query() {
        // TODO Auto-generated method stub
        try {
            List<PeopleModel> pms = db.findAll(Selector.from(PeopleModel.class).where("source", "=", source));
            if (pms != null) {
                list.clear();
                list.addAll(pms);
            }
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ClickClear(View v) {

        if (!(list != null && list.size() > 0)) {
            Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckProjectCompanyActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定删除所有数据?");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                try {
                    db.delete(PeopleModel.class, WhereBuilder.b("source", "=", source));
                } catch (DbException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                list.clear();
                adapter.notifyDataSetChanged();
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        builder.create().show();

    }

    private int ischeckNum = 0;

    public void ClickDelete(View v) {
        ischeckNum = 0;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null) {
                    if (list.get(i).is_check) {
                        ischeckNum++;
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ischeckNum <= 0) {
            Toast.makeText(getApplicationContext(), "请先选中删除数据", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckProjectCompanyActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定删除选中数据?");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                List<PeopleModel> deletelist = new ArrayList<PeopleModel>();

                for (int i = 0; i < list.size(); i++) {
                    PeopleModel model = list.get(i);
                    if (model.getIs_check()) {
                        try {
                            deletelist.add(model);
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("name", "=", model.getName());
                            whereBuilder.and("source", "=", model.getSource());
                            db.delete(PeopleModel.class, whereBuilder);
                        } catch (DbException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                for (int i = 0; i < deletelist.size(); i++) {
                    PeopleModel model = deletelist.get(i);
                    list.remove(model);
                }
                adapter.notifyDataSetChanged();
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        builder.create().show();
    }

    public void ClickEdit(View v) {

        showDialog();
    }

    public void ClickBack(View v) {
        this.back();
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckProjectCompanyActivity.this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(CheckProjectCompanyActivity.this, R.layout.dialog, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();

        final EditText messagetf = (EditText) view.findViewById(R.id.dialog_message);

        Button btn_cancel = (Button) view.findViewById(R.id.dialog_cancel);
        Button btn_OK = (Button) view.findViewById(R.id.dialog_truebtu);

        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        btn_OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (messagetf.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "请输入名称", Toast.LENGTH_LONG).show();
                    return;
                }
                queryOne(dialog, messagetf.getText().toString());

            }
        });
    }

    private void queryOne(AlertDialog dialog, String name) {

        List<PeopleModel> peopleList = new ArrayList<>();
        try {
            peopleList = db.findAll(Selector.from(PeopleModel.class).where("name", "=", name).and("source", "=", source));// 自定义sql查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (peopleList != null && peopleList.size() > 0) {
            Toast.makeText(getApplicationContext(), "名称已经存在", Toast.LENGTH_SHORT).show();
        } else {
            dialog.dismiss();
            PeopleModel model = new PeopleModel();
            model.setSource(source);
            model.setIs_check(false);
            model.setName(name);
            list.add(model);
            adapter.notifyDataSetChanged();

            try {
                db.save(model);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }
}
