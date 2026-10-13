package com.tnd.jinbiao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.ToolUtils;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.ResultPhotoImgModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;

public class CheckResultPhotoActivity extends BaseActivity {

	private ImageView imageview;
	private List<ResultPhotoImgModel> list = null;
	private int index = 0;
	private Bitmap map = null;
	private DbUtils db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_photo_main);
		db = DbHelper.GetInstance();
		imageview = (ImageView) findViewById(R.id.check_result_photo_img);

		try {
			list = db.findAll(ResultPhotoImgModel.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (list == null) {
			list = new ArrayList<ResultPhotoImgModel>();
		}

		if (list.size() <= 0) {
			showDialog("暂无检测图片!");
		} else {
			ResultPhotoImgModel model = list.get(index);

			map = ToolUtils.getPicUrlWithBitmap(this, model.getUrl());
			imageview.setImageBitmap(map);
		}
	}

	public void showDialog(final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("提示!");
		builder.setMessage(message);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (message.equals("暂无检测图片!")) {
					finish();
				} else {
					ResultPhotoImgModel model = list.get(0);
					list.remove(model);
					try {
						db.delete(model);
					} catch (DbException e) {
						e.printStackTrace();
					}
					finish();
				}
			}
		});

		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

		builder.create().show();
	}

	public void ClickForWord(View v) {
		if (list.size() <= 0) {
			return;
		}
		index--;
		if (index <= 0) {
			index = 0;
		}
		if (map != null && !map.isRecycled()) {
			map.recycle();
			System.gc();
			map = null;
		}

		ResultPhotoImgModel model = list.get(index);
		if (model != null && model.getUrl() != null
				&& model.getUrl().length() > 0) {
			map = ToolUtils.getPicUrlWithBitmap(this, model.getUrl());
		}

		if (map != null) {
			imageview.setImageBitmap(map);
		}

	}

	public void ClickMoveNext(View v) {
		if (list.size() <= 0) {
			return;
		}

		index++;
		if (index >= list.size() - 1) {
			index = list.size() - 1;
		}
		if (map != null && !map.isRecycled()) {
			map.recycle();
			System.gc();
			map = null;
		}

		ResultPhotoImgModel model = list.get(index);
		if (model != null && model.getUrl() != null
				&& model.getUrl().length() > 0) {
			map = ToolUtils.getPicUrlWithBitmap(this, model.getUrl());
		}
		if (map != null) {
			imageview.setImageBitmap(map);
		}

	}

	public void ClickDelete(View v) {
		if (list.size() <= 0) {
			return;
		}
		if (list.size() == 1) {
			showDialog("是否删除最后一张检测图片");
		} else {
			ResultPhotoImgModel model = list.get(index);
			list.remove(model);
			try {
				db.delete(model);
				ToolUtils.deleteAllFile(CheckResultPhotoActivity.this,
						model.getUrl());
			} catch (DbException e) {
				e.printStackTrace();
			}

			map.recycle();
			System.gc();
			map = null;

			ResultPhotoImgModel models = list.get(0);

			map = ToolUtils.getPicUrlWithBitmap(this, models.getUrl());

			imageview.setImageBitmap(map);

		}
	}

	public void ClickBack(View v) {
		if (map != null && !map.isRecycled()) {
			map.recycle();
			System.gc();
			map = null;
		}
		this.back();
	}

}
