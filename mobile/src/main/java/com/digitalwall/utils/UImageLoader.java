package com.digitalwall.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

/**
 * Created by ${VIDYA}
 */
public class UImageLoader {


    public static void URLPicLoading(ImageView ivImageView, String ImageUrl, int placeholder) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(placeholder)
                .showImageOnFail(placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();

        ImageLoader.getInstance().displayImage(ImageUrl, ivImageView, options);
    }

    public static void URLPicLoadingFile(ImageView ivImageView, String fileUrl, int placeholder) {
        fileUrl = fileUrl.replace(" ", "%20");

        String decodedImgUri = Uri.fromFile(new File(fileUrl)).toString();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(placeholder)
                .showImageOnFail(placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();

        ImageLoader.getInstance().displayImage(decodedImgUri, ivImageView, options);
    }

}
