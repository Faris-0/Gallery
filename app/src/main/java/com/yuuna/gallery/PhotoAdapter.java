package com.yuuna.gallery;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder> implements Filterable {
    private ArrayList<JSONObject> jsonObjectDataList, listPhoto;
    private Context mContext;

    public PhotoAdapter(ArrayList<JSONObject> jsonObjectArrayList, Context context) {
        this.jsonObjectDataList = jsonObjectArrayList;
        this.mContext = context;
        this.listPhoto = jsonObjectArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        try {
            String data = listPhoto.get(position).getString("data");
            Glide.with(mContext).load(data).centerCrop().into(holder.ivPhoto);
            holder.ivPhoto.setOnClickListener(v -> photoDialog(data));
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
        return listPhoto.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString().toLowerCase().trim();

                if (searchString.isEmpty()) {
                    listPhoto = jsonObjectDataList;
                } else {
                    ArrayList<JSONObject> tempFilteredList = new ArrayList<>();
                    for (JSONObject jsonObject : jsonObjectDataList) {
                        try {
                            Long lDate = jsonObject.getLong("date");
                            String sDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date(lDate * 1000L));
                            if (sDate.toLowerCase().contains(searchString)) tempFilteredList.add(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listPhoto = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listPhoto;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listPhoto = (ArrayList<JSONObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public Holder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.pPhoto);
        }
    }
}
