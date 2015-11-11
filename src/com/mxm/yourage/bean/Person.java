package com.mxm.yourage.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

@AVClassName(Person._CLASS)
public class Person extends AVObject {
	static final String _CLASS = "MFaceUser";
//	private double lat;// Î³¶È
//	private double longit;// ¾­¶È
//	private AVFile bit;
//	private String msg;
	 
	public double getLat() {
		return getDouble("lat");
	}

	public void setLat(double lat) {
		put("lat", lat);
	}

	public double getLongit() {
		return getDouble("longat");
	}

	public void setLongit(double longit) {
		put("longat", longit);
		 
	}

	public AVFile getBit() {
		return getAVFile("bmp");
 
	}

	public void setBit(AVFile bmp) {
		put("bmp", "AVFile");
	}

	public String getMsg() {
		return getString("msg");
	}

	public void setMsg(String msg) {
		put("msg", msg);
	}

}
