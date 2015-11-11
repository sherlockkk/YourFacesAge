package com.mxm.yourage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.mxm.yourage.tools.FileTools;
import com.mxm.yourage.ui.AlertBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
/**
 * 主界面
 * @author HM
 *
 */
public class MainActivity extends BaseActivity {
	private Button btn1, btn2, btn3;
	private ImageView imageView;
	final private int PICTURE_CHOOSE = 2;
	final private int CAPTURE_CHOOSE = 1;
	private Bitmap img;
	private String age;
	private String gender = "";
	private String range;
	private Dialog dialog;
	private double lat;
	private double longt;
	private float radius;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dingwei();
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn3.setBackgroundResource(R.drawable.shenhui);
		btn3.setEnabled(false);
		imageView = (ImageView) findViewById(R.id.imageView);
		findViewById(R.id.buttonMap).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getApplicationContext(),
						MapActivity.class);
				it.putExtra("lat", lat);
				it.putExtra("longt", longt);
				it.putExtra("radius", radius);
				startActivity(it);
			}
		});
		//拍照
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File f = new File(Environment.getExternalStorageDirectory(),
						"tmpe.png");// localTempImgDir和localTempImageFileName是自己定义的名字
				Uri u = Uri.fromFile(f);
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
				startActivityForResult(intent, CAPTURE_CHOOSE);
			}
		});
		//图库
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
			}
		});
		//点击分析
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = createLoadingDialog(MainActivity.this, "分析中...");
				dialog.show();
				// textView.setText("Waiting ...");

				FaceppDetect faceppDetect = new FaceppDetect();
				faceppDetect.setDetectCallback(new DetectCallback() {

					public void detectResult(final JSONObject rst) {
						// use the red paint
						Paint paint = new Paint();
						paint.setColor(Color.RED);
						paint.setStrokeWidth(Math.max(img.getWidth(),
								img.getHeight()) / 100f);

						// create a new canvas
						final Bitmap bitmap = Bitmap.createBitmap(
								img.getWidth(), img.getHeight(),
								img.getConfig());
						Canvas canvas = new Canvas(bitmap);
						canvas.drawBitmap(img, new Matrix(), null);

						try {
							// find out all faces
							final int count = rst.getJSONArray("face").length();
							for (int i = 0; i < count; ++i) {
								float x, y, w, h;
								// get the center point
								x = (float) rst.getJSONArray("face")
										.getJSONObject(i)
										.getJSONObject("position")
										.getJSONObject("center").getDouble("x");
								y = (float) rst.getJSONArray("face")
										.getJSONObject(i)
										.getJSONObject("position")
										.getJSONObject("center").getDouble("y");

								// get face size
								w = (float) rst.getJSONArray("face")
										.getJSONObject(i)
										.getJSONObject("position")
										.getDouble("width");
								h = (float) rst.getJSONArray("face")
										.getJSONObject(i)
										.getJSONObject("position")
										.getDouble("height");
								age = rst.getJSONArray("face").getJSONObject(i)
										.getJSONObject("attribute")
										.getJSONObject("age")
										.getString("value");
								range = rst.getJSONArray("face")
										.getJSONObject(i)
										.getJSONObject("attribute")
										.getJSONObject("age")
										.getString("range");
								gender = rst.getJSONArray("face")
										.getJSONObject(i)
										.getJSONObject("attribute")
										.getJSONObject("gender")
										.getString("value");
								// change percent value to the real size
								x = x / 100 * img.getWidth();
								w = w / 100 * img.getWidth() * 0.7f;
								y = y / 100 * img.getHeight();
								h = h / 100 * img.getHeight() * 0.7f;

								// draw the box to mark it out
								canvas.drawLine(x - w, y - h, x - w, y + h,
										paint);
								canvas.drawLine(x - w, y - h, x + w, y - h,
										paint);
								canvas.drawLine(x + w, y + h, x - w, y + h,
										paint);
								canvas.drawLine(x + w, y + h, x + w, y - h,
										paint);
							}
							// save new image
							// img = bitmap;

							MainActivity.this.runOnUiThread(new Runnable() {

								public void run() {
									dialog.dismiss();
									String msg = null;
									try {
										if (Integer.valueOf(age) <= 15) {
											msg = Integer.valueOf(age)
													+ Integer.valueOf(range)
													+ "";
										} else if (Integer.valueOf(age) <= 18
												&& Integer.valueOf(age) >= 15) {
											msg = age;
										} else if (Integer.valueOf(age) > 25
												&& Integer.valueOf(age) <= 35) {
											int i = (Integer.valueOf(age) - 25)
													/ Integer.valueOf(range);
											msg = Integer.valueOf(age) - i + "";
										} else if (Integer.valueOf(age) > 35) {
											msg = Integer.valueOf(age)
													- Integer.valueOf(range)
													+ "";
										} else {
											msg = age;
										}
										age = msg;
									} catch (Exception e) {
										showToast("分析失败.");
										return;
									}
									if (gender.equals("Male")) {
										gender = "男";
									} else if (gender.equals("Female")) {
										gender = "女";
									}
									msg = "分析结果:" + gender + "   " + msg + "岁";
									showToast(msg);
									// show the image
									imageView.setImageBitmap(bitmap);

									// textView.setText("Finished, "+ count +
									// " faces.");
									showdialog(msg, age);
									age = "";
								};

							});

						} catch (JSONException e) {
							e.printStackTrace();
							MainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									showToast("JSONException");
								}
							});
						}

					}
				});
				faceppDetect.detect(img);

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == PICTURE_CHOOSE) {
				if (data != null) {
					// The Android api ~~~
					// Log.d(TAG, "idButSelPic Photopicker: " +
					// intent.getDataString());
					Cursor cursor = getContentResolver().query(data.getData(),
							null, null, null, null);
					cursor.moveToFirst();
					int idx = cursor.getColumnIndex(ImageColumns.DATA);
					String fileSrc = cursor.getString(idx);
					// Log.d(TAG, "Picture:" + fileSrc);

					// just read size
					Options options = new Options();
					options.inJustDecodeBounds = true;
					img = BitmapFactory.decodeFile(fileSrc, options);
					// scale size to read
					options.inSampleSize = Math.max(1, (int) Math.ceil(Math
							.max((double) options.outWidth / 1024f,
									(double) options.outHeight / 1024f)));
					options.inJustDecodeBounds = false;
					img = BitmapFactory.decodeFile(fileSrc, options);
					Matrix matrix = new Matrix();
					matrix.postRotate(FileTools.getExifOrientation(fileSrc));
					int width = img.getWidth();
					int height = img.getHeight();
					img = Bitmap.createBitmap(img, 0, 0, width, height, matrix,
							true);
					imageView.setImageBitmap(img);
					btn3.setBackgroundResource(R.drawable.btn_3);
					btn3.setEnabled(true);
					// img.recycle();
				} else {

				}
			} else if (requestCode == CAPTURE_CHOOSE) {
				File f = new File(Environment.getExternalStorageDirectory(),
						"tmpe.png");
				String fileSrc = f.getAbsolutePath();
				Options options = new Options();
				options.inJustDecodeBounds = true;
				img = BitmapFactory.decodeFile(fileSrc, options);
				// scale size to read
				options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
						(double) options.outWidth / 1024f,
						(double) options.outHeight / 1024f)));
				options.inJustDecodeBounds = false;
				img = BitmapFactory.decodeFile(fileSrc, options);
				Matrix matrix = new Matrix();
				matrix.postRotate(FileTools.getExifOrientation(f.getPath()));
				int width = img.getWidth();
				int height = img.getHeight();
				img = Bitmap.createBitmap(img, 0, 0, width, height, matrix,
						true);

				imageView.setImageBitmap(img);
				btn3.setBackgroundResource(R.drawable.btn_3);
				btn3.setEnabled(true);
			}
		}
	}
	

	private class FaceppDetect {
		DetectCallback callback = null;

		public void setDetectCallback(DetectCallback detectCallback) {
			callback = detectCallback;
		}

		public void detect(final Bitmap image) {

			new Thread(new Runnable() {

				public void run() {
					HttpRequests httpRequests = new HttpRequests(
							"4480afa9b8b364e30ba03819f3e9eff5",
							"Pz9VFT8AP3g_Pz8_dz84cRY_bz8_Pz8M", true, false);
					// Log.v(TAG, "image size : " + img.getWidth() + " " +
					// img.getHeight());
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					float scale = Math.min(
							1,
							Math.min(600f / img.getWidth(),
									600f / img.getHeight()));
					Matrix matrix = new Matrix();
					matrix.postScale(scale, scale);

					Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0,
							img.getWidth(), img.getHeight(), matrix, false);
					// Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " "
					// + imgSmall.getHeight());

					imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] array = stream.toByteArray();

					try {
						// detect
						JSONObject result = httpRequests
								.detectionDetect(new PostParameters()
										.setAttribute(
												"age,gender,race,smiling,glass,pose")
										.setImg(array));
						// finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						MainActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								dialog.dismiss();
								showToast("网络连接失败,请重试"); 
							}
						});
					}

				}
			}).start();
		}
	}
	//返回json
	private interface DetectCallback {
		void detectResult(JSONObject rst);
	}
	//创建一个对话框
	public Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.view_progress_dialog, null);// 得到加载view
		ViewFlipper viewFlipper = (ViewFlipper) v
				.findViewById(R.id.viewFlipper);// 加载布局
		if (!viewFlipper.isFlipping()) {
			viewFlipper.startFlipping();
		}
		TextView tv = (TextView) v.findViewById(R.id.text_message);
		tv.setText(msg);
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCanceledOnTouchOutside(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return loadingDialog;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (img != null) {
			img.recycle();
			img = null;
		}
		super.onDestroy();
	}

	
	//话对话框
	private Dialog sharedialog;
	private void showdialog(String msg, final String age) {
		sharedialog = AlertBuilder.createDialog(this, msg,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (v.getId() == R.id.button_negative) {
							sharedialog.dismiss();
						} else if (v.getId() == R.id.button_positive) {
							dialog = createLoadingDialog(MainActivity.this,
									"分享中...");
							sharedialog.dismiss();
							upload(age);

							dialog.show();
						}
					}
				});
		sharedialog.show();
	}

	BDLocationListener dbLocationListener;
	//获取坐标信息
	private void dingwei() {
		final LocationClient locationClient = new LocationClient(this);
		locationClient
				.registerLocationListener(dbLocationListener = new BDLocationListener() {

					@Override
					public void onReceivePoi(BDLocation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onReceiveLocation(BDLocation db) {
						// TODO Auto-generated method stub
						lat = db.getLatitude();
						longt = db.getLongitude();
						radius = db.getRadius();
						locationClient
								.unRegisterLocationListener(dbLocationListener);
					}
				});
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000);
		locationClient.setLocOption(option);
		locationClient.start();
	}
	
	private void upload(String age) {
		AVObject avo = new AVObject("MFaceUser");
		avo.put("lat", lat);
		avo.put("longat", longt);
		avo.put("msg", gender + "     " + age + "岁");
		AVFile av = new AVFile("bmp", FileTools.Bitmap2Bytes(img));
		// /av.saveInBackground();
		avo.put("bmp", av);
		avo.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException avexce) {
				// TODO Auto-generated method stub
				if (avexce == null) {
					showToast("分享成功"); 
				} else {
					showToast("分享失败"); 
				}
				dialog.dismiss();
			}
		});
	}


	// 重写按键松开事件 返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder alert=new AlertDialog.Builder(this);
			alert.setTitle("退出?");
			alert.setMessage("真的要退出?");
			alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			alert.setNegativeButton("取消", null);
			alert.create().show();
		}
		return super.onKeyDown(keyCode, event);
	}
}
