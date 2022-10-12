package com.transmodelo.user.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.transmodelo.user.R;
import com.transmodelo.user.data.network.model.BannerModel;


import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    private final ArrayList<BannerModel> imageList;
    Context mContext;

    public ImageAdapter(Context context, ArrayList<BannerModel> imageList) {
        this.mContext = context;
        this.imageList = imageList;
    }

    /*   @Override
       public boolean isViewFromObject(View view, Object object) {
           return view == ((ImageView) object);
       }*/
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view==((LinearLayout)o);
    }
    // private int[] sliderImageId =

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.image_slider_layout, container, false);
        ImageView imgAd = view.findViewById(R.id.imgSlide);
        // ImageView imageView = new ImageView(mContext);
        //   imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String url=imageList.get(position).getBanner_url();
        Glide.with(mContext)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.color.white)
                        .dontAnimate()
                        .error(R.color.white))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imgAd);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        if (imageList != null && !imageList.isEmpty()) {
            return imageList.size();
        } else {
            return 0;
        }
    }
}