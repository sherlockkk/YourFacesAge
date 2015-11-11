package com.mxm.yourage.application;


import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.mxm.yourage.bean.Person;

import android.app.Application;

public class AvosApplication extends Application {

	 @Override
	  public void onCreate() {
	    super.onCreate();
	    // 初始化应用 Id 和 应用 Key，您可以在应用设置菜单里找到这些信息
	    AVOSCloud.initialize(this, "pwmurrmm1nag79qfa1os14a6vdagrl6w1481fnjv23fzbi9h",
	        "lbvmvmr0xy6f6fenvj3q177g7hdm8zoeqjbu0dmq265mbuzi");
	    // 启用崩溃错误报告
	    AVAnalytics.enableCrashReport(getApplicationContext(), true);
	    // 注册子类
	    AVObject.registerSubclass(Person.class);
	  }


}
