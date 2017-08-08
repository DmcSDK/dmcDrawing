package net.pobbay.view;

import java.io.File;
import java.io.IOException;

import net.pobbay.mms.R;
import net.pobbay.util.ContextAll;
import net.pobbay.util.Share;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;

public class ShareDialogFragment extends DialogFragment implements
OnClickListener{

	public  Context context1;
	public  String tempFilePath;
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
