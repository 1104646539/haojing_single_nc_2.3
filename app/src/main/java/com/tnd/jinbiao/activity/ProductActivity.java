package com.tnd.jinbiao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.adapter.ProductAdapter;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.CardCompanyModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;

public class ProductActivity extends BaseActivity {

	private ListView listview = null;
	private EditText name;
	private EditText start, end, ct, ct_width;
	private List<CardCompanyModel> list = null;
	private ProductAdapter adapter = null;

	private View seletview;

	private CardCompanyModel selectmodel;
	private DbUtils db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_main);
		db = DbHelper.GetInstance();
		listview = (ListView) findViewById(R.id.card_listview);
		name = (EditText) findViewById(R.id.card_name);
		start = (EditText) findViewById(R.id.card_start);
		end = (EditText) findViewById(R.id.card_end);
		ct = (EditText) findViewById(R.id.card_ct);
		ct_width = (EditText) findViewById(R.id.card_ct_width);

		showAllProductInfo();

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (seletview != null) {
					seletview.setBackgroundColor(Color.WHITE);
				}

				arg1.setBackgroundColor(Color.YELLOW);
				seletview = arg1;

				selectmodel = list.get(arg2);
				Value(selectmodel);
			}
		});
	}

	private void showAllProductInfo() {

		try {
			 list = db.findAll(Selector.from(CardCompanyModel.class));// 自定义sql查询
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list == null){
			list = new ArrayList<CardCompanyModel>();
		}

		adapter = new ProductAdapter(this, list);
		listview.setAdapter(adapter);
	}

	public void Value(CardCompanyModel model) {
		name.setText(model.getName());
		start.setText(model.getScanStart());
		end.setText(model.getScanEnd());
		ct.setText(model.getCTPeakDistance());
		ct_width.setText(model.getCTPeakWidth());
	}

	public void ClickDeleteItem(View v) {
		if (selectmodel == null) {
			Toast.makeText(this, "请先选中一条数据", Toast.LENGTH_LONG).show();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除选中数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				list.remove(selectmodel);
				try {
					db.delete(selectmodel);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
				clear();
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

	public void ClickSave(View v) {
		if (selectmodel == null) {
			Toast.makeText(this, "请先选中一条数据", Toast.LENGTH_LONG).show();
			return;
		}
//		if(selectmodel.getName().equals(name.getText().toString())){
			selectmodel.setName(name.getText().toString());
			selectmodel.setScanStart(start.getText().toString());
			selectmodel.setScanEnd(end.getText().toString());
			selectmodel.setCTPeakDistance(ct.getText().toString());
			selectmodel.setCTPeakWidth(ct_width.getText().toString());

			try {
				db.update(selectmodel,WhereBuilder.b("id","=", selectmodel.id) , "name", "ScanStart", "ScanEnd", "CTPeakDistance", "CTPeakWidth");
//				db.update(selectmodel, "name", "ScanStart", "ScanEnd", "CTDistance", "CTWidth");
//				List<LineModel> listLineModel  = db.findAll(Selector.from(LineModel.class).where("card_name",	"=", name.getText().toString()));
//				if(listLineModel!=null&&listLineModel.size()>0){
//					for (int i = 0; i < listLineModel.size(); i++) {
//						if(listLineModel.get(i)!=null){
//							listLineModel.get(i).setCard_name(name.getText().toString());
//							listLineModel.get(i).setScanStart(start.getText().toString());
//							listLineModel.get(i).setScanEnd(end.getText().toString());
//							listLineModel.get(i).setCTDistance(ct.getText().toString());
//							listLineModel.get(i).setCTWidth(ct_width.getText().toString());
//							db.update(listLineModel.get(i), "name","card_name", "ScanStart", "ScanEnd", "CTDistance", "CTWidth",
//									"Jcx", "Ljz", "A1", "A2", "unitZh");
//						}
//					}
//				}
				selectmodel = null;
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			showAllProductInfo();
			clear();
//		}else{
//			AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
//			builder.setTitle("提示");
//			builder.setMessage("名称已变化，是否覆盖?");
//			builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//					try {
//
//						List<LineModel> listLineModel  = db.findAll(Selector.from(LineModel.class).where("card_name",
//								"=", selectmodel.getName()));
//						if(listLineModel!=null&&listLineModel.size()>0){
//							for (int i = 0; i < listLineModel.size(); i++) {
//								if(listLineModel.get(i)!=null){
//									listLineModel.get(i).setCard_name(name.getText().toString());
//									listLineModel.get(i).setScanStart(start.getText().toString());
//									listLineModel.get(i).setScanEnd(end.getText().toString());
//									listLineModel.get(i).setCTDistance(ct.getText().toString());
//									listLineModel.get(i).setCTWidth(ct_width.getText().toString());
//									db.update(listLineModel.get(i), "name","card_name", "ScanStart", "ScanEnd", "CTDistance", "CTWidth",
//											"Jcx", "Ljz", "A1", "A2", "unitZh");
//								}
//							}
//						}
//						selectmodel.setName(name.getText().toString());
//						selectmodel.setScanStart(start.getText().toString());
//						selectmodel.setScanEnd(end.getText().toString());
//						selectmodel.setCTPeakDistance(ct.getText().toString());
//						selectmodel.setCTPeakWidth(ct_width.getText().toString());
//						db.update(selectmodel, "name", "ScanStart", "ScanEnd", "CTDistance", "CTWidth");
//						selectmodel = null;
//						showAllProductInfo();
//						clear();
//						arg0.dismiss();
//					} catch (DbException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//
//
//				}
//			});
//
//			builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//					arg0.dismiss();
//				}
//			});
//			builder.create().show();
//		}
		
	}

	public void ClickAdd(View v) {

		if (selectmodel != null) {

			seletview.setBackgroundColor(Color.WHITE);
			seletview = null;

			clear();
			selectmodel = null;
		}

		if (name.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入卡厂商名称", Toast.LENGTH_SHORT).show();
			return;
		}

		if (start.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入扫描起点", Toast.LENGTH_SHORT).show();
			return;
		}

		if (end.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入扫描终点", Toast.LENGTH_SHORT).show();
			return;
		}

		if (ct.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入CT间距", Toast.LENGTH_SHORT).show();
			return;
		}

		if (ct_width.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入CT峰宽", Toast.LENGTH_SHORT).show();
			return;
		}

		CardCompanyModel model = new CardCompanyModel();
		model.setName(name.getText().toString());
		model.setScanStart(start.getText().toString());
		model.setScanEnd(end.getText().toString());
		model.setCTPeakDistance(ct.getText().toString());
		model.setCTPeakWidth(ct_width.getText().toString());

		list.add(model);
		try {
			db.save(model);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();

		clear();
	}

	public void clear() {
		name.setText("");
		start.setText("");
		end.setText("");
		ct.setText("");
		ct_width.setText("");
	}

	public void ClickBack(View v) {
		this.back();
	}

}
