package com.yuuna.gallery;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
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

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.Holder> {

    private JSONObject jsonObjectData;
    private ArrayList<String> dateDataList;
    private Context mContext;

    public DateAdapter(JSONObject jsonObject, Context context, ArrayList<String> dateArrayList) {
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
        holder.tvDate.setLayoutParams(getDateMarginParams(position));
        holder.tvDate.setText(dateDataList.get(position));
        String key = dateDataList.get(position);
        if (jsonObjectData.has(key)) {
            try {
                JSONArray jsonArray = jsonObjectData.getJSONArray(dateDataList.get(position));
                RecyclerView rvPhoto = holder.itemView.findViewById(R.id.dPhoto);
                rvPhoto.setLayoutManager(new CustomGridLayoutManager(mContext, calculateNoOfColumns(140)));
                rvPhoto.setAdapter(new PhotoAdapter(jsonArray, mContext));
            } catch (JSONException e) {
                Log.e("DateAdapter", "Failed to bind photo list for date: " + dateDataList.get(position), e);
            }
        } else {
            Log.w("DateAdapter", "Missing key in JSON: " + key);
        }
    }

    private LinearLayout.LayoutParams getDateMarginParams(int position) {
        int value3 = (int) mContext.getResources().getDimension(com.intuit.sdp.R.dimen._3sdp);
        int value25 = (int) mContext.getResources().getDimension(com.intuit.sdp.R.dimen._25sdp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(value3, position != 0 ? value25 : value3, value3, value3);
        return params;
    }

    public int calculateNoOfColumns(float columnWidthDp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
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
