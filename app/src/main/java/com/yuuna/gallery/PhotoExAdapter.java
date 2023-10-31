package com.yuuna.gallery;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

public class PhotoExAdapter extends RecyclerView.Adapter<PhotoExAdapter.Holder> {
    private JSONArray jsonArrayData;
    private Context mContext;

    public PhotoExAdapter(JSONArray jsonArray, Context context) {
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
            String photo = jsonArrayData.getJSONObject(position).getJSONObject("photoData").getString("photo");
            Glide.with(mContext).load(photo).centerCrop().into(holder.ivPhoto);
            holder.ivPhoto.setOnClickListener(v -> {
                photoDialog(photo);

                // Delete file
//                try {
//                    File file = new File(photo);
//                    if (file.exists()) {
//                        Log.d("ADA?", "YA");
//                        if (file.delete()) Log.d("HAPUS?", "YA");
//                        else Log.d("HAPUS?", "TIDAK");
//                    } else Log.d("ADA?", "TIDAK");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void photoDialog(String s) {
        Dialog dPhoto = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar);
//        dPhoto.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dPhoto.setContentView(R.layout.dialog_photo);
//        dPhoto.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dPhoto.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );

        SubsamplingScaleImageView ssiv = dPhoto.findViewById(R.id.pPhoto);
        Glide.with(mContext)
                .asBitmap()
                .load(s)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
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
