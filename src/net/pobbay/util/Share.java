package net.pobbay.util;

import net.pobbay.mms.Previewer;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;

public class Share {

	public static final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);
	public static void share01(){
		//设置分享内容
		mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
		//设置分享图片
		mController.setShareMedia(new UMImage(ContextAll.getContext(), "http://www.umeng.com/images/pic/banner_module_social.png"));
		//直接分享
		mController.directShare(ContextAll.getContext(), SHARE_MEDIA.SINA,
				new SnsPostListener() {

					@Override
					public void onStart() {
						Toast.makeText(ContextAll.getContext(), "分享开始",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(SHARE_MEDIA platform,int eCode, SocializeEntity entity) {
						if(eCode == StatusCode.ST_CODE_SUCCESSED){
							Toast.makeText(ContextAll.getContext(), "分享成功",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(ContextAll.getContext(), "分享失败",Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
	public static void share02(){
		//设置分享内容
		mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
		//设置分享图片
		mController.setShareMedia(new UMImage(ContextAll.getContext(), "http://www.umeng.com/images/pic/banner_module_social.png"));
		//直接分享
		mController.directShare(ContextAll.getContext(), SHARE_MEDIA.QZONE,
				new SnsPostListener() {

					@Override
					public void onStart() {
						Toast.makeText(ContextAll.getContext(), "分享开始",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(SHARE_MEDIA platform,int eCode, SocializeEntity entity) {
						if(eCode == StatusCode.ST_CODE_SUCCESSED){
							Toast.makeText(ContextAll.getContext(), "分享成功",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(ContextAll.getContext(), "分享失败",Toast.LENGTH_SHORT).show();
						}
					}

				});
	}
	public static void share03(){
		//设置分享内容
		mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
		//设置分享图片
		mController.setShareMedia(new UMImage(ContextAll.getContext(), "http://www.umeng.com/images/pic/banner_module_social.png"));
		//直接分享
		mController.directShare(ContextAll.getContext(), SHARE_MEDIA.RENREN,
				new SnsPostListener() {

					@Override
					public void onStart() {
						Toast.makeText(ContextAll.getContext(), "分享开始",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(SHARE_MEDIA platform,int eCode, SocializeEntity entity) {
						if(eCode == StatusCode.ST_CODE_SUCCESSED){
							Toast.makeText(ContextAll.getContext(), "分享成功",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(ContextAll.getContext(), "分享失败",Toast.LENGTH_SHORT).show();
						}
					}

				});
	}
	public static void share04(){
		//设置分享内容
		mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
		//设置分享图片
		mController.setShareMedia(new UMImage(ContextAll.getContext(), "http://www.umeng.com/images/pic/banner_module_social.png"));
		//直接分享
		mController.directShare(ContextAll.getContext(), SHARE_MEDIA.EMAIL,
				new SnsPostListener() {

					@Override
					public void onStart() {
						Toast.makeText(ContextAll.getContext(), "分享开始",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(SHARE_MEDIA platform,int eCode, SocializeEntity entity) {
						if(eCode == StatusCode.ST_CODE_SUCCESSED){
							Toast.makeText(ContextAll.getContext(), "分享成功",Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(ContextAll.getContext(), "分享失败",Toast.LENGTH_SHORT).show();
						}
					}

				});
	}


	public static void oauth(){

	}

}
