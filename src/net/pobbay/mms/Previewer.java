package net.pobbay.mms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;

import net.pobbay.util.ContextAll;
import net.pobbay.util.Share;
import net.pobbay.view.ChoicenessViewActivity;
import net.pobbay.view.MyButton;



import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 作品预览界面
 *
 * @author yu
 *
 */
public class Previewer extends FragmentActivity implements OnClickListener,
		Runnable {
	private Thread thread;
	private static Bitmap previewBitmap;
	public static void setPreviewBitmap(Bitmap previewBitmap) {
		Previewer.previewBitmap = previewBitmap;
	}

	String downURL = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (previewBitmap == null)
			throw new NullPointerException();
		thread = new Thread(this);
		thread.start();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previewer);
		Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/font1.ttf");
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_sms).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);

		btn = (MyButton) findViewById(R.id.btn_clear);
		btn.setVisibility(View.INVISIBLE);
		((ImageView) findViewById(R.id.imageView1))
				.setImageBitmap(previewBitmap);
		TextView textView1=(TextView) findViewById(R.id.tv_titlebar_title);
		textView1.setText(R.string.title_previewer);
		textView1.setTypeface(typeface);

	}

	@Override
	protected void onDestroy() {
		previewBitmap = null;
		System.gc();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!saveed) { // 等待保存完
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		switch (v.getId()) {
			case R.id.btn_save: {
				saveToImage();
				break;
			}

			case R.id.btn_sms: {
				sharedBySms();
				break;
			}

			case R.id.btn_share: {

				showShareDialog();
				break;
			}

			case R.id.btn_titlebar_left:
				finish();
				break;
		}
	}

	private String imgName;
	private String tempFilePath;
	private boolean saveed = false;

	private File file;

	private MyButton btn;

	private File path;

	/**
	 * 压缩 bmp 到 png 的线程
	 */
	@Override
	public void run() {
		saveImage();
	}

	public String time2Name() {
		// 根据当前时间生成图片名称
		Calendar c = Calendar.getInstance();
		String name = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
				+ c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY)
				+ c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".png";
		return name;
	}

	public void saveToImage() {
		if(saveed==false){
			saveImage();
		}else{
			Toast.makeText(this, "保存成功！\n文件保存在：" + tempFilePath,
					Toast.LENGTH_SHORT).show();
		}
	}
	public InputStream Bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}
	public void saveImage(){
		saveed = false;
		imgName = time2Name();
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		path = new File(path, "pobaby");
		File f = new File(path, imgName);
		FileOutputStream fos = null;
		try {
			path.mkdirs();
			fos = new FileOutputStream(f);
			// 将 bitmap 压缩成其他格式的图片数据
			previewBitmap.compress(CompressFormat.PNG, 50, fos);
			tempFilePath = f.getAbsolutePath();
			Log.e("", "保存路径是"+tempFilePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		saveed = true;
	}

	public void sharedBySms() {
		try {
			Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
			sendMSGIntent.setClassName("com.android.mms",
					"com.android.mms.ui.ComposeMessageActivity");
			sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendMSGIntent.putExtra("sms_body", ""); // 预设内容
			sendMSGIntent.putExtra("compose_mode", false);
			sendMSGIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file:///" + tempFilePath));
			sendMSGIntent.setType("image/*");
			startActivity(sendMSGIntent);
		} catch (Exception e) {
			Toast.makeText(Previewer.this, "非android原生系统，无法找到发送单独彩信组件。",
					Toast.LENGTH_LONG).show();
			Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
			sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendMSGIntent.putExtra("sms_body", ""); // 预设内容
			sendMSGIntent.putExtra("compose_mode", false);
			sendMSGIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file:///" + tempFilePath));
			sendMSGIntent.setType("image/*");
			startActivity(Intent.createChooser(sendMSGIntent,
					"非android原生系统，<请选择短信发送彩信>"));
		}
	}
	public String getPath(){
		String path1=path+""+imgName;
		return path1;
	}
	private void showShareDialog() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
		intent.putExtra(Intent.EXTRA_TEXT,
				"私人订制APP,http://www.pobaby.net/");
		Log.e("", "文件路径是："+tempFilePath);
		File f = new File(tempFilePath);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, this
				.getResources().getString(R.string.title_share)));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = Share.mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 分享对话框
	 *
	 * @author Yichou
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
														SHARE_MEDIA arg0, int arg1,
														SocializeEntity arg2) {
													// TODO Auto-generated method stub
													if(arg1 == StatusCode.ST_CODE_SUCCESSED){
														Toast.makeText(ContextAll
																.getContext(), "分享成功",Toast.LENGTH_SHORT).show();
													}else{
														Toast.makeText(ContextAll
																.getContext(), "分享失败",Toast.LENGTH_SHORT).show();
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
