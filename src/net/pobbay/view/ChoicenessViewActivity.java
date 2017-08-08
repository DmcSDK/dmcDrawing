package net.pobbay.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;

import net.pobbay.mms.HomePage;
import net.pobbay.mms.R;
import net.pobbay.util.BitmapManager;
import net.pobbay.util.ContextAll;
import net.pobbay.util.Share;
import net.pobbay.view.GifViewActivity.ShareDialogFragment;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class ChoicenessViewActivity extends FragmentActivity implements
		OnClickListener {
	private ViewGroup group;
	private MyButton button;
	private String url;
	private Bitmap defaultBitmap;
	private BitmapManager bitmapManager;
	private BitmapManager bm;
	private Uri uri;
	private int w, h;
	private Bitmap bitmap;
	private String imgName;
	private String tempFilePath;
	private boolean saveed = false;
	private ImageView imageView;
	private String savePath;

	// { "items": [{"title": "小破孩表情秀", "new": "1", "count": 95, "lock":
	// 0,"path": "gif/pdtx/","class": "pdtx","gif": "1"},...], "content": 1}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif);
		bitmapManager = new BitmapManager(defaultBitmap);
		bm = new BitmapManager();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		url = bundle.getString("url");
		button = (MyButton) findViewById(R.id.btn_clear);
		button.setVisibility(View.INVISIBLE);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(R.string.title_choiceness);
		textView.setTypeface(typeface);
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		group = (ViewGroup) findViewById(R.id.contener_gif);
		w = group.getWidth();
		h = group.getHeight();
		imageView = new ImageView(this);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				try {
					uri = bm.getImageURI2(url);
					if (uri != null) {
						message.what = 1;
					} else {
						message.what = 2;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(message);
			}
		});
		thread.start();
		group.addView(imageView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gif_btn_preview).setOnClickListener(this);
		findViewById(R.id.gif_btn_save).setOnClickListener(this);
		findViewById(R.id.gif_btn_sms).setOnClickListener(this);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					imageView.setImageURI(uri);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					break;
				case 2:

					break;
				case 3:
					Toast.makeText(ChoicenessViewActivity.this, "保存路径" + savePath,
							Toast.LENGTH_LONG).show();
					break;
				case 4:

					break;
			}
		}
	};

	private File file;


	private void showShareDialog() {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		// Create and show the dialog.
		DialogFragment f = new ShareDialogFragment();
		ShareDialogFragment.context1 = this;

		ShareDialogFragment.tempFilePath =Environment.getExternalStorageDirectory().toString()
				+ "/android/data/pobaby_png_c.jpg";
		f.show(ft, "dialog");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_titlebar_left:
				finish();
				startActivity(new Intent(this, HomePage.class));
				break;
			case R.id.gif_btn_preview:
				showShareDialog();
				break;
			case R.id.gif_btn_save:
				runSave();
				break;
			case R.id.gif_btn_sms:
				sharedBySms();
				break;
		}

	}

	/**
	 * 压缩 的线程
	 */
	private void runSave() {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saveed = false;
				imgName = time2Name();
				File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				File f = new File(path, imgName);
				FileOutputStream fos = null;
				InputStream is;
				try {
					is = new FileInputStream(Environment
							.getExternalStorageDirectory().toString()
							+ "/android/data/pobaby_png_c.jpg");
					fos = new FileOutputStream(f);
					savePath=f.getAbsolutePath();

					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					Message message = new Message();
					message.what = 3;
					handler.sendMessage(message);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveed = true;
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public String time2Name() {
		// 根据当前时间生成图片名称
		Calendar c = Calendar.getInstance();
		String name = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
				+ c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY)
				+ c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".jpg";
		return name;
	}

	/**
	 * 短信
	 */
	public void sharedBySms() {
		try {
			file = new File(Environment.getExternalStorageDirectory()
					.toString() + "/android/data/pobaby_png_c.jpg");
			Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
			sendMSGIntent.setClassName("com.android.mms",
					"com.android.mms.ui.ComposeMessageActivity");
			sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendMSGIntent.putExtra("sms_body", ""); // 预设内容
			sendMSGIntent.putExtra("compose_mode", false);
			sendMSGIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			sendMSGIntent.setType("image/*");
			startActivity(sendMSGIntent);
		} catch (Exception e) {
			Toast.makeText(ChoicenessViewActivity.this,
					"非android原生系统，无法找到发送单独彩信组件。", Toast.LENGTH_LONG).show();
			Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
			sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendMSGIntent.putExtra("sms_body", ""); // 预设内容
			sendMSGIntent.putExtra("compose_mode", false);
			sendMSGIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			sendMSGIntent.setType("image/*");
			startActivity(Intent.createChooser(sendMSGIntent,
					"非android原生系统，<请选择短信发送彩信>"));
		}

	}

	/**
	 * 分享对话框
	 *
	 * @author yu
	 *
	 */
	public static class ShareDialogFragment extends DialogFragment implements
			OnClickListener {

		public static Context context1;
		public static String tempFilePath;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_close:
					dismiss();
					break;

				case R.id.btn_share4: {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("image/*");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					intent.putExtra(Intent.EXTRA_TEXT,
							"私人定制APP,http://www.pobaby.net/");
					File f = new File(Environment.getExternalStorageDirectory()
							.toString() + "/android/data/pobaby_png_c.jpg");
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(Intent.createChooser(intent, getActivity()
							.getResources().getString(R.string.title_share)));
					dismiss();
					break;
				}

				case R.id.btn_share0:

					// 设置分享内容
					Share.mController
							.setShareContent("来自私人定制·小破孩APP 下载地址http://www.pobaby.net/");
					Share.mController.setShareMedia(new UMImage(context1,
							BitmapFactory.decodeFile(tempFilePath)));
					// 设置分享图片
					Share.mController.doOauthVerify(context1, SHARE_MEDIA.SINA,
							new UMAuthListener() {
								@Override
								public void onStart(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权开始",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onError(SocializeException e,
													SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权错误",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onComplete(Bundle value,
													   SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权完成",
											Toast.LENGTH_SHORT).show();
									// 获取相关授权信息或者跳转到自定义的分享编辑页面
									String uid = value.getString("uid");
									Share.mController.directShare(context1,
											SHARE_MEDIA.SINA,
											new SnsPostListener() {

												@Override
												public void onStart() {
													Toast.makeText(
															ContextAll.getContext(),
															"分享开始",
															Toast.LENGTH_SHORT)
															.show();
												}

												@Override
												public void onComplete(
														SHARE_MEDIA platform,
														int eCode,
														SocializeEntity entity) {
													if (eCode == StatusCode.ST_CODE_SUCCESSED) {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享成功",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											});
								}

								@Override
								public void onCancel(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权取消",
											Toast.LENGTH_SHORT).show();
								}
							});
					// 直接分享
					break;
				case R.id.btn_share1:
					Share.mController
							.setShareContent("来自私人定制·小破孩APP 下载地址http://www.pobaby.net/");
					Share.mController.setShareMedia(new UMImage(context1,
							BitmapFactory.decodeFile(tempFilePath)));
					// 设置分享图片
					Share.mController.doOauthVerify(context1, SHARE_MEDIA.TENCENT,
							new UMAuthListener() {
								@Override
								public void onStart(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权开始",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onError(SocializeException e,
													SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权错误",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onComplete(Bundle value,
													   SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权完成",
											Toast.LENGTH_SHORT).show();
									// 获取相关授权信息或者跳转到自定义的分享编辑页面
									String uid = value.getString("uid");
									Share.mController.directShare(context1,
											SHARE_MEDIA.TENCENT,
											new SnsPostListener() {

												@Override
												public void onStart() {
													Toast.makeText(
															ContextAll.getContext(),
															"分享开始",
															Toast.LENGTH_SHORT)
															.show();
												}

												@Override
												public void onComplete(
														SHARE_MEDIA platform,
														int eCode,
														SocializeEntity entity) {
													if (eCode == StatusCode.ST_CODE_SUCCESSED) {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享成功",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											});
								}

								@Override
								public void onCancel(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权取消",
											Toast.LENGTH_SHORT).show();
								}
							});
					// 直接分享
					break;
				case R.id.btn_share2:
					Share.mController
							.setShareContent("来自私人定制·小破孩APP 下载地址http://www.pobaby.net/");
					Share.mController.setShareMedia(new UMImage(context1,
							BitmapFactory.decodeFile(tempFilePath)));
					// 设置分享图片
					Share.mController.doOauthVerify(context1, SHARE_MEDIA.RENREN,
							new UMAuthListener() {
								@Override
								public void onStart(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权开始",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onError(SocializeException e,
													SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权错误",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onComplete(Bundle value,
													   SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权完成",
											Toast.LENGTH_SHORT).show();
									// 获取相关授权信息或者跳转到自定义的分享编辑页面
									String uid = value.getString("uid");
									Share.mController.directShare(context1,
											SHARE_MEDIA.RENREN,
											new SnsPostListener() {

												@Override
												public void onStart() {
													Toast.makeText(
															ContextAll.getContext(),
															"分享开始",
															Toast.LENGTH_SHORT)
															.show();
												}

												@Override
												public void onComplete(
														SHARE_MEDIA platform,
														int eCode,
														SocializeEntity entity) {
													if (eCode == StatusCode.ST_CODE_SUCCESSED) {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享成功",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											});
								}

								@Override
								public void onCancel(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权取消",
											Toast.LENGTH_SHORT).show();
								}
							});
					break;
				case R.id.btn_share3:
					Share.mController
							.setShareContent("来自私人定制·小破孩APP 下载地址http://www.pobaby.net/");
					Share.mController.setShareMedia(new UMImage(context1,
							BitmapFactory.decodeFile(tempFilePath)));
					// 设置分享图片
					Share.mController.doOauthVerify(context1, SHARE_MEDIA.QZONE,
							new UMAuthListener() {
								@Override
								public void onStart(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权开始",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onError(SocializeException e,
													SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权错误",
											Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onComplete(Bundle value,
													   SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权完成",
											Toast.LENGTH_SHORT).show();
									// 获取相关授权信息或者跳转到自定义的分享编辑页面
									String uid = value.getString("uid");
									Share.mController.directShare(context1,
											SHARE_MEDIA.QZONE,
											new SnsPostListener() {

												@Override
												public void onStart() {
													Toast.makeText(
															ContextAll.getContext(),
															"分享开始",
															Toast.LENGTH_SHORT)
															.show();
												}

												@Override
												public void onComplete(
														SHARE_MEDIA platform,
														int eCode,
														SocializeEntity entity) {
													if (eCode == StatusCode.ST_CODE_SUCCESSED) {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享成功",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														Toast.makeText(
																ContextAll
																		.getContext(),
																"分享失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											});
								}

								@Override
								public void onCancel(SHARE_MEDIA platform) {
									Toast.makeText(ContextAll.getContext(), "授权取消",
											Toast.LENGTH_SHORT).show();
								}
							});
					break;
			}
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
									 Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			/** 使用SSO授权必须添加如下代码 */
			UMSsoHandler ssoHandler = Share.mController.getConfig()
					.getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.dialog2, container, false);

			final int[] ids = { R.id.btn_share0, R.id.btn_share1,
					R.id.btn_share2, R.id.btn_share3, R.id.btn_share4,
					R.id.btn_close };

			for (int id : ids) {
				view.findViewById(id).setOnClickListener(this);
			}

			return view;
		}
	}

}
