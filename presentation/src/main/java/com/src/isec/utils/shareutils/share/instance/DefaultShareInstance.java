package com.src.isec.utils.shareutils.share.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.src.isec.R;
import com.src.isec.utils.shareutils.share.ImageDecoder;
import com.src.isec.utils.shareutils.share.ShareImageObject;
import com.src.isec.utils.shareutils.share.ShareListener;

import org.reactivestreams.Subscription;

import java.io.File;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.LongConsumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by shaohui on 2016/11/18.
 */

public class DefaultShareInstance implements ShareInstance {

    @Override
    public void shareText(int platform, String text, Activity activity, ShareListener listener) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent,
                activity.getResources().getString(R.string.vista_share_title)));
    }

    @Override
    public void shareMedia(int platform, String title, String targetUrl, String summary,
                           ShareImageObject shareImageObject, Activity activity, ShareListener listener) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", title, targetUrl));
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent,
                activity.getResources().getString(R.string.vista_share_title)));
    }

    @Override
    public void shareImage(int platform, final ShareImageObject shareImageObject,
                           final Activity activity, final ShareListener listener) {

        Flowable.create(new FlowableOnSubscribe<Uri>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Uri> emitter) throws Exception {
                Uri uri = Uri.fromFile(new File(ImageDecoder.decode(activity, shareImageObject)));
                emitter.onNext(uri);
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnRequest(new LongConsumer() {
                    @Override
                    public void accept(long t) throws Exception {
                        listener.shareRequest();
                    }
                })
                .subscribe(new FlowableSubscriber<Uri>() {

                    private Subscription s;

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        this.s = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Uri uri) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/jpeg");
                        activity.startActivity(Intent.createChooser(shareIntent,
                                activity.getResources().getText(R.string.vista_share_title)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.shareFailure(new Exception(e));
                        if(this.s != null){
                            this.s.cancel();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(this.s != null){
                            this.s.cancel();
                        }
                    }
                });

    }

    @Override
    public void handleResult(Intent data) {
        // Default share, do nothing
    }

    @Override
    public boolean isInstall(Context context) {
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        return context.getPackageManager()
//                .resolveActivity(shareIntent, PackageManager.MATCH_DEFAULT_ONLY) != null;

        return true;
    }

    @Override
    public void recycle() {

    }
}
