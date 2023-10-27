package com.yuuna.gallery;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.Holder> {

    private ArrayList<JSONObject> jsonObjectDataList;
    private ArrayList<String> dateDataList;
    private Context mContext;

    public DateAdapter(ArrayList<JSONObject> jsonObjectArrayList, Context context, ArrayList<String> dateArrayList) {
        this.jsonObjectDataList = jsonObjectArrayList;
        this.mContext = context;
        this.dateDataList = dateArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String sDate = dateDataList.get(position);
        holder.tvDate.setText(dateDataList.get(position));

        RecyclerView rvPhoto = holder.itemView.findViewById(R.id.dPhoto);
        rvPhoto.setLayoutManager(new CustomGridLayoutManager(mContext, calculateNoOfColumns(140)));
        PhotoAdapter pa = new PhotoAdapter(jsonObjectDataList, mContext);
        rvPhoto.setAdapter(pa);
        pa.getFilter().filter(sDate);
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
