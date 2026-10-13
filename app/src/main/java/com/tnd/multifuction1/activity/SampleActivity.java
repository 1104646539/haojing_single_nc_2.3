package com.tnd.multifuction1.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.SampleAdapter;
import com.tnd.multifuction1.adapter.UserInputAdapter;
import com.tnd.multifuction1.dialog.OperateBaseDataDialog;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.util.APPUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SampleActivity extends UserInputDataActivity<SampleName> {
	
	private static String url = null;

	protected void onCreate(Bundle savedInstanceState) {
		bean = new SampleName();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getCustomTitle() {
		return "样品名称编辑";
	}

	@Override
	protected UserInputAdapter<SampleName> getAdapter() {
		return new SampleAdapter(list, this);
	}

	@Override
	protected OperateBaseDataDialog<SampleName> getDialog(SampleName sample) {
		View view = View.inflate(this, R.layout.dialog_sample_edit, null);
		final Button btnSave =  view.findViewById(R.id.btn_OK);
		final Button btnCancel =  view.findViewById(R.id.btn_cancel);
		final EditText etSampleName = view.findViewById(R.id.et_sampleName);
		return new OperateBaseDataDialog<SampleName>(this, view, sample) {
			@Override
			protected void initCustomView(SampleName sample) {
				if (sample != null) {
					etSampleName.setText(sample.sampleName);
					etSampleName.setSelection(etSampleName.getText().toString().length());
				}
			}

			@Override
			protected SampleName getData(SampleName sample) {
				if (sample == null) {
					sample = new SampleName();
				}
				if (TextUtils.isEmpty(etSampleName.getText().toString())) {
					APPUtils.showToast(act, "请输入样品名称");
					return null;
				}
				sample.sampleName = etSampleName.getText().toString().trim();
				return sample;
			}

			@Override
			protected Button getSaveButton() {
				return btnSave;
			}

			@Override
			protected Button getCancelButton() {
				return btnCancel;
			}
		};
	}

	/**
	 * 根据输入流返回一个字符串
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private static String getStringFromInputStream(InputStream is) throws Exception{

		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		byte[] buff=new byte[1024];
		int len=-1;
		while((len=is.read(buff))!=-1){
			baos.write(buff, 0, len);
		}
		is.close();
		String html=baos.toString();
		baos.close();


		return html;
	}
}
