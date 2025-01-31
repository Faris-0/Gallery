package com.yuuna.gallery;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DateExAdapter extends RecyclerView.Adapter<DateExAdapter.Holder> {

    private JSONObject jsonObjectData;
    private ArrayList<String> dateDataList;
    private Context mContext;

    public DateExAdapter(JSONObject jsonObject, Context context, ArrayList<String> dateArrayList) {
        this.jsonObjectData = jsonObject;
        this.mContext = context;
        this.dateDataList = dateArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int value3 = (int) mContext.getResources().getDimension(com.intuit.sdp.R.dimen._3sdp);
        int value25 = (int) mContext.getResources().getDimension(com.intuit.sdp.R.dimen._25sdp);
        if (position != 0) params.setMargins(value3, value25, value3, value3);
        else params.setMargins(value3, value3, value3, value3);
        holder.tvDate.setLayoutParams(params);
        holder.tvDate.setText(dateDataList.get(position));
        try {
            JSONArray jsonArray = jsonObjectData.getJSONArray(dateDataList.get(position));
            RecyclerView rvPhoto = holder.itemView.findViewById(R.id.dPhoto);
            rvPhoto.setLayoutManager(new CustomGridLayoutManager(mContext, calculateNoOfColumns(140)));
            rvPhoto.setAdapter(new PhotoExAdapter(jsonArray, mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int calculateNoOfColumns(float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    @Override
    public int getItemCount() {
        return dateDataList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvDate;

        public Holder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.dDate);
        }
    }
}
