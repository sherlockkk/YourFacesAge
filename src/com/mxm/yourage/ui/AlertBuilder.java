package com.mxm.yourage.ui;


 

import com.mxm.yourage.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @note
 * @author hm
 * @date 2014年7月22日下午2:31:33
 */
public class AlertBuilder {
	public AlertBuilder(Context context){
		 
	}
	 
 
	public static Dialog createDialog(Context context,String msg,OnClickListener lis) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.view_dialog_share, null);// 得到加载view
		TextView tv= (TextView) v.findViewById(R.id.textView);
		tv.setText(msg);
		Button btnPositive=(Button) v.findViewById(R.id.button_positive);
		btnPositive.setOnClickListener(lis);
		Button btnNegative=(Button) v.findViewById(R.id.button_negative);
		btnNegative.setOnClickListener(lis);
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		loadingDialog.setCanceledOnTouchOutside(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return loadingDialog;
	}
	 
}
