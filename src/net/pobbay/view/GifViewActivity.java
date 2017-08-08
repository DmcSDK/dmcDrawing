package net.pobbay.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;

import net.pobbay.gif.GifView;
import net.pobbay.mms.HomePage;
import net.pobbay.mms.ImagePicker;
import net.pobbay.mms.Previewer;
import net.pobbay.mms.R;
import net.pobbay.mms.Previewer.ShareDialogFragment;
import net.pobbay.util.BitmapManager;
import net.pobbay.util.ContextAll;
import net.pobbay.util.NetClient;
import net.pobbay.util.Share;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GifViewActivity extends FragmentActivity implements
		OnClickListener {
	private ViewGroup group;
	private MyButton button;
	private String url;
	private Bitmap defaultBitmap;
	private BitmapManager bitmapManager;
	private BitmapManager bm;
	private Bitmap bitmap;
	private Uri uri;
	private int w, h;
	private GifView child;
	DisplayMetrics dm;
	private String savePath;
	private ProgressBar bar;
	// { "items": [{"title": "С�ƺ�������", "new": "1", "count": 95, "lock":
	// 0,"path": "gif/pdtx/","class": "pdtx","gif": "1"},...], "content": 1}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif2);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels;
		h = dm.heightPixels;
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
		textView.setText(R.string.title_gif);
		textView.setTypeface(typeface);
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		group = (ViewGroup) findViewById(R.id.contener_gif);	
		child = new GifView(this);	
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				try {
					uri = bm.getImageURI(url);
					if (uri != null) {
						message.what = 1;
					} else {
						message.what = 2;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendMessage(message);
			}
		});
		thread.start();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParams.width = 300;
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.topMargin = h / 2 / 2;
		layoutParams.height = 300;
		group.addView(child, layoutParams);
		child.setGifImage(R.drawable.wait);
		child.setLoopAnimation();
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gif_btn_preview).setOnClickListener(this);
		findViewById(R.id.gif_btn_save).setOnClickListener(this);
		findViewById(R.id.gif_btn_sms).setOnClickListener(this);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			startActivity(new Intent(this, HomePage.class));
		}
		return super.onKeyDown(keyCode, event);
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				child.setGifImage(Environment.getExternalStorageDirectory()
						.toString() + "/android/data/pobaby_gif.gif");
			//	bar.setVisibility(View.INVISIBLE);
				child.setLoopAnimation();
				break;
			case 2:

				break;
			case 3:
				Toast.makeText(GifViewActivity.this, "����·��" + savePath,
						Toast.LENGTH_LONG).show();
				break;
			case 4:

				break;
			}
		}
	};

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
				+ "/android/data/pobaby_gif.gif"; 
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
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/*");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
			intent.putExtra(Intent.EXTRA_TEXT,
					"˽�˶���APP,http://www.pobaby.net/");
			File f = new File(Environment.getExternalStorageDirectory()
					.toString() + "/android/data/pobaby_gif.gif");
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(intent,this
					.getResources().getString(R.string.title_share)));
			break;
		case R.id.gif_btn_save:
			runSave();
			break;
		case R.id.gif_btn_sms:
			sharedBySms();
			break;
		}
	}

	private String imgName;
	private String tempFilePath;
	private boolean saveed = false;
	private File file;
	

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
							+ "/android/data/pobaby_gif.gif");
					fos = new FileOutputStream(f);
					savePath = f.getAbsolutePath();
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
		// ���ݵ�ǰʱ������ͼƬ����
		Calendar c = Calendar.getInstance();
		String name = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
				+ c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY)
				+ c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".gif";
		return name;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("", "����GIF������Դ");
		if(child.getGifDecoder()){
		child.destroy();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		child.pauseGifAnimation();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		child.restartGifAnimation();
	}

	/**
	 * ����
	 */
	public void sharedBySms() {
		try {
			file = new File(Environment.getExternalStorageDirectory()
					.toString() + "/android/data/pobaby_gif.gif");
			Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
			sendMSGIntent.setClassName("com.android.mms",
					"com.android.mms.ui.ComposeMessageActivity");
			sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendMSGIntent.putExtra("sms_body", ""); // Ԥ������
			sendMSGIntent.putExtra("compose_mode", false);
			sendMSGIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			sendMSGIntent.setType("image/*");
			startActivity(sendMSGIntent);
		} catch (Exception e) {
			Toast.makeText(GifViewActivity.this, "��androidԭ��ϵͳ���޷��ҵ����͵������������",
					Toast.LENGTH_LONG).show();
			Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
			sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sendMSGIntent.putExtra("sms_body", ""); // Ԥ������
			sendMSGIntent.putExtra("compose_mode", false);
			sendMSGIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			sendMSGIntent.setType("image/*");
			startActivity(Intent.createChooser(sendMSGIntent,
					"��androidԭ��ϵͳ��<��ѡ����ŷ��Ͳ���>"));
		}

	}

	private List<ResolveInfo> getShareTargets() {
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("image/*");
		PackageManager pm = this.getPackageManager();
		return pm.queryIntentActivities(intent,
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
	}

	private void getall() {
		PackageManager pManager = this.getPackageManager();
		/********************* ��ѯ�ֻ�������֧�ַ����Ӧ�� *********************/
		List<ResolveInfo> resolveList = getShareTargets();
		for (int i = 0; i < resolveList.size(); i++) {
			ResolveInfo resolve = resolveList.get(i);
			String d = resolve.loadLabel(pManager).toString();
			// set Package Name
			String s = resolve.activityInfo.packageName;
			Log.e("", "�ܷ���ͼƬ��APP" + d);
		}
	}

	/**
	 * ����Ի���
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

				dismiss();
				break;
			}

			case R.id.btn_share0:

				// ���÷�������
				Share.mController
						.setShareContent("����˽�˶��ơ�С�ƺ�APP ���ص�ַhttp://www.pobaby.net/");
				Share.mController.setShareMedia(new UMImage(context1,
						BitmapFactory.decodeFile(tempFilePath)));
				// ���÷���ͼƬ
				Share.mController.doOauthVerify(context1, SHARE_MEDIA.SINA,
						new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ��ʼ",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ����",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ���",
										Toast.LENGTH_SHORT).show();
								// ��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
								String uid = value.getString("uid");
								Share.mController.directShare(context1,
										SHARE_MEDIA.SINA,
										new SnsPostListener() {

											@Override
											public void onStart() {
												Toast.makeText(
														ContextAll.getContext(),
														"����ʼ",
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
															"����ɹ�",
															Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast.makeText(
															ContextAll
																	.getContext(),
															"����ʧ��",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩȡ��",
										Toast.LENGTH_SHORT).show();
							}
						});
				// ֱ�ӷ���
				break;
			case R.id.btn_share1:
				Share.mController
						.setShareContent("����˽�˶��ơ�С�ƺ�APP ���ص�ַhttp://www.pobaby.net/");
				Share.mController.setShareMedia(new UMImage(context1,
						BitmapFactory.decodeFile(tempFilePath)));
				// ���÷���ͼƬ
				Share.mController.doOauthVerify(context1, SHARE_MEDIA.TENCENT,
						new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ��ʼ",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ����",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ���",
										Toast.LENGTH_SHORT).show();
								// ��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
								String uid = value.getString("uid");
								Share.mController.directShare(context1,
										SHARE_MEDIA.TENCENT,
										new SnsPostListener() {

											@Override
											public void onStart() {
												Toast.makeText(
														ContextAll.getContext(),
														"����ʼ",
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
															"����ɹ�",
															Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast.makeText(
															ContextAll
																	.getContext(),
															"����ʧ��",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩȡ��",
										Toast.LENGTH_SHORT).show();
							}
						});
				// ֱ�ӷ���
				break;
			case R.id.btn_share2:
				Share.mController
						.setShareContent("����˽�˶��ơ�С�ƺ�APP ���ص�ַhttp://www.pobaby.net/");
				Share.mController.setShareMedia(new UMImage(context1,
						BitmapFactory.decodeFile(tempFilePath)));
				// ���÷���ͼƬ
				Share.mController.doOauthVerify(context1, SHARE_MEDIA.RENREN,
						new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ��ʼ",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ����",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ���",
										Toast.LENGTH_SHORT).show();
								// ��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
								String uid = value.getString("uid");
								Share.mController.directShare(context1,
										SHARE_MEDIA.RENREN,
										new SnsPostListener() {

											@Override
											public void onStart() {
												Toast.makeText(
														ContextAll.getContext(),
														"����ʼ",
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
															"����ɹ�",
															Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast.makeText(
															ContextAll
																	.getContext(),
															"����ʧ��",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩȡ��",
										Toast.LENGTH_SHORT).show();
							}
						});
				break;
			case R.id.btn_share3:
				Share.mController
						.setShareContent("����˽�˶��ơ�С�ƺ�APP ���ص�ַhttp://www.pobaby.net/");
				Share.mController.setShareMedia(new UMImage(context1,
						BitmapFactory.decodeFile(tempFilePath)));
				// ���÷���ͼƬ
				Share.mController.doOauthVerify(context1, SHARE_MEDIA.QZONE,
						new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ��ʼ",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ����",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩ���",
										Toast.LENGTH_SHORT).show();
								// ��ȡ�����Ȩ��Ϣ������ת���Զ���ķ���༭ҳ��
								String uid = value.getString("uid");
								Share.mController.directShare(context1,
										SHARE_MEDIA.QZONE,
										new SnsPostListener() {

											@Override
											public void onStart() {
												Toast.makeText(
														ContextAll.getContext(),
														"����ʼ",
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
															"����ɹ�",
															Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast.makeText(
															ContextAll
																	.getContext(),
															"����ʧ��",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								Toast.makeText(ContextAll.getContext(), "��Ȩȡ��",
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
			/** ʹ��SSO��Ȩ����������´��� */
			UMSsoHandler ssoHandler = Share.mController.getConfig()
					.getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.dialog3, container, false);

			final int[] ids = {  R.id.btn_share4,
					R.id.btn_close };

			for (int id : ids) {
				view.findViewById(id).setOnClickListener(this);
			}

			return view;
		}
	}

}
