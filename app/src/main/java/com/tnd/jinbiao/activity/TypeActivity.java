package com.tnd.jinbiao.activity;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.adapter.CheckSampleTypeAdapter;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.SampleTypeModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TypeActivity extends BaseActivity {
	
	private ListView listview;
	private List<SampleTypeModel> sampleTypeModelsList = null;
	private CheckSampleTypeAdapter checkSampleAdapter;
	private DbUtils db;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_sample_type);
		db = DbHelper.GetInstance();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		listview = (ListView) findViewById(R.id.company_listview);		

		query();		
	}

	private void query() {
		// TODO Auto-generated method stub
		if(sampleTypeModelsList!=null&&sampleTypeModelsList.size()>0){
			sampleTypeModelsList.clear();
			sampleTypeModelsList=null;
		}
		sampleTypeModelsList = new ArrayList<SampleTypeModel>();
		try {
			sampleTypeModelsList = db.findAll(Selector.from(SampleTypeModel.class));// 自定义sql查询
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		checkSampleAdapter = new CheckSampleTypeAdapter(this, sampleTypeModelsList);
		listview.setAdapter(checkSampleAdapter);
	}

	public void ClickClear(View v) {
		if(!(sampleTypeModelsList!=null&&sampleTypeModelsList.size()>0)){
			Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			return ;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TypeActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除所有数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
					try {
						db.deleteAll(SampleTypeModel.class);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				query();
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
	private int ischeckNum=0;
	public void ClickDelete(View v) {
		
		
		ischeckNum=0;
		if(sampleTypeModelsList!=null&&sampleTypeModelsList.size()>0){
			for (int i = 0; i < sampleTypeModelsList.size(); i++) {
				if(sampleTypeModelsList.get(i)!=null){
					if(sampleTypeModelsList.get(i).getIs_check()){
						ischeckNum++;
					}
				}
			}
		}else{
			Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(ischeckNum<=0){
			Toast.makeText(getApplicationContext(), "请先选中删除数据", Toast.LENGTH_SHORT).show();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TypeActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除选中数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				List<SampleTypeModel> deletelist = new ArrayList<SampleTypeModel>();

				for (int i = 0; i < sampleTypeModelsList.size(); i++) {
					SampleTypeModel model = sampleTypeModelsList.get(i);
					if (model.getIs_check()) {
						try {
							deletelist.add(model);
							db.delete(SampleTypeModel.class, WhereBuilder.b("name", "=", model.getName()));
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				}

				query();
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
		AlertDialog.Builder builder = new AlertDialog.Builder(TypeActivity.this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(TypeActivity.this, R.layout.dialog, null);
		dialog.setView(view,0,0,0,0);
		dialog.show();

		final EditText messagetf = (EditText) view.findViewById(R.id.dialog_message);

		Button cancel = (Button) view.findViewById(R.id.dialog_cancel);
		Button truebtu = (Button) view.findViewById(R.id.dialog_truebtu);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		truebtu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (messagetf.getText().toString().length() <= 0) {
					Toast.makeText(getApplicationContext(), "请输入样品类型",
							Toast.LENGTH_LONG).show();
					return;
				}
				queryOne(dialog,messagetf.getText().toString());
				
			}
		});
	}
	private void queryOne(AlertDialog dialog, String name) {
		// TODO Auto-generated method stub
		SampleTypeModel sampleTypeModel = null;
		try {
			sampleTypeModel = db.findFirst(Selector.from(SampleTypeModel.class).where("name", "=", name));// 自定义sql查询
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sampleTypeModel != null) {
			Toast.makeText(getApplicationContext(), "名称已经存在", Toast.LENGTH_SHORT).show();
		}else{
			dialog.dismiss();

			SampleTypeModel model = new SampleTypeModel();
			model.is_check = false;
			model.setName(name);

			try {
				db.save(model);
			} catch (DbException e) {
				e.printStackTrace();
			}
			query();
		}
	}
}
