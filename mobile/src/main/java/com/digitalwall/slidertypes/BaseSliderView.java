package com.digitalwall.slidertypes;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.digitalwall.R;
import com.digitalwall.utils.UImageLoader;
import com.digitalwall.views.TextureVideoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;


public abstract class BaseSliderView {

    protected Context mContext;

    private Bundle mBundle;

    private String mUrl;
    private File mFile;
    private int mRes;

    private String slideType;
    private boolean mErrorDisappear;


    private ImageLoadListener mLoadListener;


    private Picasso mPicasso;

    /**
     * Scale type of the image.
     */
    private ScaleType mScaleType = ScaleType.Fit;

    public enum ScaleType {
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    protected BaseSliderView(Context context) {
        mContext = context;
    }


    /**
     * set a url as a image that preparing to load
     *
     * @param url
     * @return
     */
    public BaseSliderView image(String url) {
        if (mFile != null || mRes != 0) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**
     * set a file as a image that will to load
     *
     * @param file
     * @return
     */
    public BaseSliderView imageFile(File file) {

        mFile = file;
        return this;
    }

    public BaseSliderView image(int res) {
        if (mUrl != null || mFile != null) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    public BaseSliderView errorDisappear(boolean disappear) {
        mErrorDisappear = disappear;
        return this;
    }

    public boolean isErrorDisappear() {
        return mErrorDisappear;
    }

    /**
     * lets users add a bundle of additional information
     *
     * @param bundle
     * @return
     */
    public BaseSliderView bundle(Bundle bundle) {
        mBundle = bundle;
        return this;
    }

    public String getUrl() {
        return mUrl;
    }


    public Context getContext() {
        return mContext;
    }


    /**
     * When you want to implement your own slider view, please call this method in the end in `getView()` method
     *
     * @param v               the whole view
     * @param targetImageView where to place image
     * @param tv_video
     */
    protected void bindEventAndShow(final View v, ImageView targetImageView,
                                    FrameLayout fl_video, final TextureVideoView tv_video) {
        final BaseSliderView me = this;


        if (slideType.equals("video")) {
            targetImageView.setVisibility(View.GONE);
            fl_video.setVisibility(View.VISIBLE);

            try {
               /* String fileName = "android.resource://" + getContext().getPackageName() + "/raw/sample";
                tv_video.setVideoURI(Uri.parse(fileName));*/

                tv_video.setVisibility(View.VISIBLE);
                tv_video.requestFocus();
                //tv_video.setVideoURI(Uri.parse(mUrl));
                /*if (mFile != null) {
                    tv_video.setVideoPath("file:///"+mFile.toString());
                }else{
                    tv_video.setVideoURI(Uri.parse(mUrl));
                }*/
                tv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        tv_video.setVisibility(View.VISIBLE);
                        tv_video.start();
                        try {
                            mp.setVolume(1, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.setVolume(0, 0);
                            }
                        });
                    }
                });
                tv_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //mp.setVolume(0, 0);
                    }
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } else {
            targetImageView.setVisibility(View.VISIBLE);
            fl_video.setVisibility(View.GONE);


            /*if (mUrl.contains("http")) {
                UImageLoader.URLPicLoading(targetImageView, mUrl, R.drawable.icon_no_video);
            } else {
                UImageLoader.URLPicLoadingFile(targetImageView, mUrl, R.drawable.icon_no_video);
            }*/

            if (mLoadListener != null) {
                mLoadListener.onStart(me);
            }

            Picasso p = (mPicasso != null) ? mPicasso : Picasso.with(mContext);
            RequestCreator rq = null;
            if (mFile != null) {
                rq = p.load(mFile);
            } else if (mUrl != null) {
                rq = p.load(mUrl);
            } else {
                return;
            }

            if (rq == null) {
                return;
            }

            rq.into(targetImageView, new Callback() {
                @Override
                public void onSuccess() {
                    if (v.findViewById(R.id.loading_bar) != null) {
                        v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onError() {
                    if (mLoadListener != null) {
                        mLoadListener.onEnd(false, me);
                    }
                    if (v.findViewById(R.id.loading_bar) != null) {
                        v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                    }
                }
            });

        }


    }


    public BaseSliderView setScaleType(ScaleType type) {
        mScaleType = type;
        return this;
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     *
     * @return
     */
    public abstract View getView();

    /**
     * set a listener to get a message , if load error.
     *
     * @param l
     */
    public void setOnImageLoadListener(ImageLoadListener l) {
        mLoadListener = l;
    }


    /**
     * when you have some extra information, please put it in this bundle.
     *
     * @return
     */
    public Bundle getBundle() {
        return mBundle;
    }

    public interface ImageLoadListener {
        public void onStart(BaseSliderView target);

        public void onEnd(boolean result, BaseSliderView target);
    }

    /**
     * Get the last instance set via setPicasso(), or null if no user provided instance was set
     *
     * @return The current user-provided Picasso instance, or null if none
     */
    public Picasso getPicasso() {
        return mPicasso;
    }

    /**
     * Provide a Picasso instance to use when loading pictures, this is useful if you have a
     * particular HTTP cache you would like to share.
     *
     * @param picasso The Picasso instance to use, may be null to let the system use the default
     *                instance
     */
    public void setPicasso(Picasso picasso) {
        mPicasso = picasso;
    }

    public String getSlideType() {
        return slideType;
    }

    public void setSlideType(String type) {
        slideType = type;
    }

}
