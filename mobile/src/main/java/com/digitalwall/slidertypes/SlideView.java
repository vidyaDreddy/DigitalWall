package com.digitalwall.slidertypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.digitalwall.R;
import com.digitalwall.activities.BaseActivity;
import com.digitalwall.activities.PlayerActivity;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.utils.Utils;
import com.digitalwall.views.TextureVideoView;
import com.squareup.picasso.Picasso;



/**
 * This is a slider with a description TextView.
 */
public class SlideView extends BaseSliderView {


    private AssetsModel model;
    private BaseActivity parent;

    public SlideView(BaseActivity parent, AssetsModel model) {
        super(parent);
        this.parent = parent;
        this.model = model;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.row_slider_item, null);


        /*ADD IMAGE TO THE VIEW*/
        ImageView iv_image = (ImageView) v.findViewById(R.id.iv_image);


        FrameLayout fl_video = (FrameLayout) v.findViewById(R.id.fl_video);
        ProgressBar pb_video = (ProgressBar) v.findViewById(R.id.pb_video);
        TextureVideoView tv_video = (TextureVideoView) v.findViewById(R.id.tv_video);


        String mediaType = model.getAssetType();
        if (!Utils.isValueNullOrEmpty(mediaType) && mediaType.equalsIgnoreCase("video")) {
            fl_video.setVisibility(View.VISIBLE);
            pb_video.setVisibility(View.GONE);
            iv_image.setVisibility(View.GONE);
        } else {
            fl_video.setVisibility(View.GONE);
            pb_video.setVisibility(View.GONE);

            iv_image.setVisibility(View.VISIBLE);
        }

        bindEventAndShow(parent,v, iv_image, fl_video, tv_video);
        return v;
    }
}
