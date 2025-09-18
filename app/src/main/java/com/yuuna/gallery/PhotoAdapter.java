package com.yuuna.gallery;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONArray;
import org.json.JSONException;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder> {

    private JSONArray jsonArrayData;
    private Context mContext;

    public PhotoAdapter(JSONArray jsonArray, Context context) {
        this.jsonArrayData = jsonArray;
        this.mContext = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        try {
            // Using Cache
            // String photo = jsonArrayData.getString(position);
            String photo = jsonArrayData.getJSONObject(position).getJSONObject("photoData").getString("photo");
            Glide.with(mContext).load(photo).centerCrop().into(holder.ivPhoto);
            holder.ivPhoto.setOnClickListener(v -> photoDialog(photo));
        } catch (JSONException e) {
            Log.e("PhotoAdapter", "Failed to bind photo at position " + position, e);
        }
    }

    private void photoDialog(String s) {
        Dialog dPhoto = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar);
        dPhoto.setContentView(R.layout.dialog_photo);

        SubsamplingScaleImageView ssiv = dPhoto.findViewById(R.id.pPhoto);
        Glide.with(mContext)
                .asBitmap()
                .load(s)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        ssiv.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {

                    }
                });

        dPhoto.show();
    }

    @Override
    public int getItemCount() {
        return jsonArrayData.length();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public Holder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.pPhoto);
        }
    }
}
