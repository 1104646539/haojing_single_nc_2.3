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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.adapter.LineAdapter;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.CardCompanyModel;
import com.tnd.jinbiao.model.LineModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.util.APPUtils;

public class CheckProjectLineActivity extends BaseActivity {

	private ListView listview = null;
	private int source;

	private List<LineModel> list = null;
	private TextView title;
	private TextView ct_tv, ct_width_tv, sc_ct_tv, sc_ct_width_tv;

	private EditText name;
	private EditText start;
	private EditText end;
	private EditText ct;
	private EditText ct_width;
	private EditText xian;
	private EditText value;
	private EditText k;
	private EditText b;
	private EditText x;
	private EditText p;
	private EditText unitZh;
	private View seletview;
	private Spinner card_spinner = null;

	private LineModel selectmodel = null;

	private String[] card_list = null;

	private List<CardCompanyModel> cardlist = null;

	private ArrayAdapter<String> card_adapter = null;
	private CardCompanyModel cardCompanyModel = null;
	private DbUtils db;
	private LineAdapter lineAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_project_line_edit);
		db = DbHelper.GetInstance();
		source = Integer.parseInt(getIntent().getStringExtra("source"));

		initView();
		
		showAllList();
		if (cardlist == null) {
			cardlist = new ArrayList<CardCompanyModel>();
		}

		card_list = new String[cardlist.size()];
		for (int i = 0; i < cardlist.size(); i++) {
			CardCompanyModel model = cardlist.get(i);
			card_list[i] = model.getName();
		}

		if (card_list == null || card_list.length <= 0) {
			card_list = new String[1];
			card_list[0] = "请先添加卡厂商";
		}		

		if (source == 1) {
			title.setText("显线金标编辑");
		} else if (source == 2) {
			title.setText("消线金标编辑");
		} else if (source == 3) {
			title.setText("比线金标编辑");
		} else if (source == 4) {
			title.setText("圆形试纸编辑");
		} else if (source == 5) {
			title.setText("正向试纸条编辑");
		} else if (source == 6) {
			title.setText("反向试纸条编辑");
		}

		if (source < 4) {
			ct_tv.setText("CT间距");
			ct_width_tv.setText("CT峰宽");
			sc_ct_tv.setText("CT间距");
			sc_ct_width_tv.setText("CT峰宽");
		} else {
			ct_tv.setText("采样起点");
			ct_width_tv.setText("采样终点");
			sc_ct_tv.setText("采样起点");
			sc_ct_width_tv.setText("采样终点");
		}

	
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
				if(card_list!=null&&card_list.length>0){
					for(int i=0;i<card_list.length;i++){
						if(card_list[i]!=null){
							if(card_list[i].equals(selectmodel.getCard_name())){
								card_spinner.setSelection(i);
							}
						}
					}
				}
				
			}
		});

		card_adapter = new ArrayAdapter<String>(this, R.layout.item_simple_spiner, card_list);
		if(cardlist != null && cardlist.size()>0){
			cardCompanyModel= cardlist.get(0);
		}
		
		card_spinner.setAdapter(card_adapter);
		card_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (cardlist.size() > 0) {
					cardCompanyModel= cardlist.get(arg2);
//					start.setText(modelPoduct.getScanStart());
//					end.setText(modelPoduct.getScanEnd());
//					ct.setText(modelPoduct.getCTPeakDistance());
//					ct_width.setText(modelPoduct.getCTPeakWidth());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}
	private void showAllList() {
		// TODO Auto-generated method stub
		if (list !=  null && list.size() > 0) {
			list.clear();
		}
		list = new ArrayList<LineModel>();
		try {
			list = db.findAll(Selector.from(LineModel.class).where("source", "=", source));
			cardlist = db.findAll(CardCompanyModel.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lineAdapter = new LineAdapter(this, list);
		listview.setAdapter(lineAdapter);

	}

	public void initView() {

		title = (TextView) findViewById(R.id.check_project_line_project_title);
		name = (EditText) findViewById(R.id.check_project_line_project_edit);
		start = (EditText) findViewById(R.id.check_project_line_project_sao_start);
		end = (EditText) findViewById(R.id.check_project_line_project_sao_end);
		ct = (EditText) findViewById(R.id.check_project_line_project_ct);
		ct_width = (EditText) findViewById(R.id.check_project_line_project_ct_width);
		xian = (EditText) findViewById(R.id.check_project_line_project_xian);
		value = (EditText) findViewById(R.id.check_project_line_project_ljz);
		k = (EditText) findViewById(R.id.check_project_line_project_k);
		b = (EditText) findViewById(R.id.check_project_line_project_b);
		x = (EditText) findViewById(R.id.check_project_line_project_x);
		p = (EditText) findViewById(R.id.check_project_line_project_p);
		unitZh = (EditText) findViewById(R.id.check_project_line_project_concentrate_unit);
		ct_tv = (TextView) findViewById(R.id.check_projetc_line_project_ct_tv);
		ct_width_tv = (TextView) findViewById(R.id.check_projetc_line_project_ct_width_tv);
		sc_ct_tv = (TextView) findViewById(R.id.check_projetc_line_project_sc_ct_tv);
		sc_ct_width_tv = (TextView) findViewById(R.id.check_projetc_line_project_sc_ct_width_tv);
		listview = (ListView) findViewById(R.id.check_project_line_listview);
		card_spinner = (Spinner) findViewById(R.id.check_project_line_project_card_spinner);
	}

	public void Value(LineModel model) {
		
		name.setText(model.getName());
		start.setText(model.getScanStart());
		end.setText(model.getScanEnd());
		ct.setText(model.getCTDistance());
		ct_width.setText(model.getCTWidth());
		xian.setText(model.getJcx());
		value.setText(model.getLjz());
		k.setText(model.getA1());
		b.setText(model.getA2());
		x.setText(model.getX0());
		p.setText(model.getP());
		unitZh.setText(model.getConcentrateUnit());
	}

	public void ClickClear(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckProjectLineActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除所有数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				try {
					db.delete(LineModel.class, WhereBuilder.b("source", "=", source));
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showAllList();
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

	public void ClickDelete(View v) {

		if (selectmodel == null) {
			Toast.makeText(this, "请先选中一条数据", Toast.LENGTH_LONG).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(CheckProjectLineActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除选中数据?");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				try {
					db.delete(LineModel.class, WhereBuilder.b("name", "=", selectmodel.getName()));
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showAllList();
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
		selectmodel.setName(name.getText().toString());
		selectmodel.setCard_name(cardCompanyModel.getName());
		selectmodel.setScanStart(cardCompanyModel.getScanStart());
		selectmodel.setScanEnd(cardCompanyModel.getScanEnd());
		selectmodel.setCTDistance(cardCompanyModel.getCTPeakDistance());
		selectmodel.setCard_name(cardCompanyModel.getName());
		selectmodel.setCTWidth(cardCompanyModel.getCTPeakWidth());
		selectmodel.setJcx(xian.getText().toString());
		selectmodel.setLjz(value.getText().toString());
		selectmodel.setA1(k.getText().toString());
		selectmodel.setA2(b.getText().toString());
		selectmodel.setX0(x.getText().toString());
		selectmodel.setP(p.getText().toString());
		selectmodel.setConcentrateUnit(unitZh.getText().toString());
		

		try {
			db.update(selectmodel, "name","card_name", "ScanStart", "ScanEnd", "CTDistance", "CTWidth",
					"Jcx", "Ljz", "A1", "A2", "X0","P" , "unitZh");
			selectmodel = null;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showAllList();
		clear();
	}
	public void ClickEdit(View v) {
		if (selectmodel != null) {
			clear();
			seletview.setBackgroundColor(Color.WHITE);
			seletview = null;
			selectmodel = null;
		}

		if (name.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入项目名称", Toast.LENGTH_SHORT).show();
			return;
		}

		if(cardCompanyModel == null){
			APPUtils.showToast(this, "请选择厂商");
			return;
		}

		if (xian.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入限量标准", Toast.LENGTH_SHORT).show();
			return;
		}

		if (value.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入临界值", Toast.LENGTH_SHORT).show();
			return;
		}

//		if (A1.getText().toString().length() <= 0) {
//			Toast.makeText(this, "请输入A1", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		if (A2.getText().toString().length() <= 0) {
//			Toast.makeText(this, "请输入A2", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		if (X0.getText().toString().length() <= 0) {
//			Toast.makeText(this, "请输入X0", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		if (P.getText().toString().length() <= 0) {
//			Toast.makeText(this, "请输入P", Toast.LENGTH_SHORT).show();
//			return;
//		}

//		if (unitZh.getText().toString().length() <= 0) {
//			Toast.makeText(this, "请输入浓度单位", Toast.LENGTH_SHORT).show();
//			return;
//		}
		queryOne(name.getText().toString());
		
	}
	private void queryOne(String name) {
		// TODO Auto-generated method stub
		List<LineModel> lineList = null;
		try {
			lineList = db.findAll(Selector.from(LineModel.class).where("name", "=", name));// 自定义sql查询
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (lineList != null && lineList.size() > 0) {
			Toast.makeText(getApplicationContext(), "名称已经存在", Toast.LENGTH_SHORT).show();
		}else{
			LineModel modelLine = new LineModel();
			modelLine.setName(name);
			modelLine.setCard_name(cardCompanyModel.getName());
			modelLine.setScanStart(cardCompanyModel.getScanStart());
			modelLine.setScanEnd(cardCompanyModel.getScanEnd());
			modelLine.setCTDistance(cardCompanyModel.getCTPeakDistance());
			modelLine.setCard_name(cardCompanyModel.getName());
			modelLine.setCTWidth(cardCompanyModel.getCTPeakWidth());
			modelLine.setJcx(xian.getText().toString());
			modelLine.setLjz(value.getText().toString());
			modelLine.setA1(k.getText().toString());
			modelLine.setA2(b.getText().toString());
			modelLine.setX0(x.getText().toString());
			modelLine.setP(p.getText().toString());
			modelLine.setConcentrateUnit(unitZh.getText().toString());
			modelLine.setSource(source);

			list.add(modelLine);
			try {
				db.save(modelLine);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lineAdapter.notifyDataSetChanged();

			clear();
		}
	}
	public void clear() {
		
		name.setText("");		
		xian.setText("");
		value.setText("");
		k.setText("");
		b.setText("");
		x.setText("");
		p.setText("");
		unitZh.setText("");
	}

	public void ClickBack(View v) {
		this.back();
	}
}
