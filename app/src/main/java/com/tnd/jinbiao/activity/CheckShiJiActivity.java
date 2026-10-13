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
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.adapter.CheckShijiAdapter;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.ShiJiModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;

public class CheckShiJiActivity extends BaseActivity {

	private ListView listview;

	private List<ShiJiModel> list = null;
	private CheckShijiAdapter adapter = null;
	private DbUtils db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_shiji_main);
		db = DbHelper.GetInstance();
		listview = (ListView) findViewById(R.id.shiji_listview);
	
		if (list == null) {
			list = new ArrayList<ShiJiModel>();
		}
		query();
		adapter = new CheckShijiAdapter(this, list);
		listview.setAdapter(adapter);
	}
	private void query() {
		// TODO Auto-generated method stub
		 try {
			 list= db.findAll(Selector.from(ShiJiModel.class));// 自定义sql查询
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ClickClear(View v) {
		if(!(list!=null&&list.size()>0)){
			Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			return ;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckShiJiActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除所有数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				try {
					db.deleteAll(ShiJiModel.class);
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
	private int ischeckNum=0;
	public void ClickDelete(View v) {
		ischeckNum=0;
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i)!=null){
					if(list.get(i).is_check){
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
		AlertDialog.Builder builder = new AlertDialog.Builder(CheckShiJiActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除选中数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();

				for (int i = 0; i < list.size(); i++) {
					ShiJiModel model = list.get(i);
					if (model.getIs_check()) {
						try {
							db.delete(ShiJiModel.class, WhereBuilder.b("name", "=", model.getName()));
							list.remove(i);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				//list.clear();
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
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckShiJiActivity.this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(CheckShiJiActivity.this, R.layout.dialog, null);
		dialog.setView(view,0,0,0,0);
		dialog.show();

		final EditText messagetf = (EditText) view
				.findViewById(R.id.dialog_message);
		final EditText code = (EditText) view.findViewById(R.id.dialog_code);
		code.setVisibility(View.VISIBLE);

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
					Toast.makeText(getApplicationContext(), "请输入名称", Toast.LENGTH_LONG);
					return;
				}

				if (code.getText().toString().length() <= 0) {
					Toast.makeText(getApplicationContext(), "请输入代码", Toast.LENGTH_LONG);
					return;
				}

				queryOne(dialog,messagetf.getText().toString(),code.getText().toString());								
			}
		});
	}
	private void queryOne(AlertDialog dialog, String name,String code) {
		// TODO Auto-generated method stub
		List<ShiJiModel> shijiList = null;
		try {
			shijiList = db.findAll(Selector.from(ShiJiModel.class).where("name", "=", name));// 自定义sql查询
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (shijiList != null && shijiList.size() > 0) {
			Toast.makeText(getApplicationContext(), "名称已经存在", Toast.LENGTH_SHORT).show();
		} else {
			dialog.dismiss();

			ShiJiModel model = new ShiJiModel();
			model.setIs_check(false);
			model.setName(name);
			model.setCode(code);
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
