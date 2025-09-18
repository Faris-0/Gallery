package com.yuuna.gallery;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStoragePermission();
    }

    private void checkStoragePermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = { Build.VERSION.SDK_INT >= 33 ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE };
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
    }

    private void loadAllPhoto() {
//        // Using Cache
//        PhotoLoader.load(this, new PhotoLoader.Callback() {
//            @Override
//            public void onLoaded(JSONObject jsonObject, ArrayList<String> dateList) {
//                RecyclerView rvPhoto = findViewById(R.id.mPhoto);
//                rvPhoto.setLayoutManager(new CustomLinearLayoutManager(MainActivity.this));
//                rvPhoto.setAdapter(new DateAdapter(jsonObject, MainActivity.this, dateList));
//            }
//        });

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED };
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy)) {
            if (cursor == null || cursor.getCount() == 0) return;

            int count = cursor.getCount();
            ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
            Map<String, Long> dateMap = new HashMap<>();
            ArrayList<GalleryData> galleryDataArrayList = new ArrayList<>();

            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int dateColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                Long lDate = cursor.getLong(dateColumnIndex);
                String sDate = sdf.format(new Date(lDate < 100000000000L ? lDate * 1000L : lDate));
                dateMap.put(sDate, lDate);

                try {
                    JSONObject json = new JSONObject();
                    json.put("data", cursor.getString(dataColumnIndex));
                    json.put("date", cursor.getString(dateColumnIndex));
                    jsonObjectArrayList.add(json);
                } catch (JSONException e) {
                    Log.e("GalleryLoader", "Failed to parse photo JSON", e);
                }

                galleryDataArrayList.add(new GalleryData(sDate, new PhotoData(cursor.getString(dataColumnIndex))));
            }

            List<Map.Entry<String, Long>> sortedDates = new ArrayList<>(dateMap.entrySet());
            Collections.sort(sortedDates, (a, b) -> Long.compare(b.getValue(), a.getValue()));

            ArrayList<String> dateArrayList = new ArrayList<>();
            for (Map.Entry<String, Long> entry : sortedDates) {
                dateArrayList.add(entry.getKey());
            }

            // Reference Link
            // https://stackoverflow.com/questions/39204438/grouping-json-response-with-keys-in-java-android-studio
            HashMap<String, ArrayList<GalleryData>> hashMap = new HashMap<>();
            for (int i = 0; i < galleryDataArrayList.size(); i++) {
                String byDate = galleryDataArrayList.get(i).getDate();
                if (hashMap.containsKey(byDate)) {
                    hashMap.get(byDate).add(galleryDataArrayList.get(i));
                } else {
                    ArrayList<GalleryData> emptyList = new ArrayList<>();
                    emptyList.add(galleryDataArrayList.get(i));
                    hashMap.put(byDate, emptyList);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            StringWriter result = new StringWriter();
            try {
                mapper.writeValue(result, hashMap);
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                RecyclerView rvPhoto = findViewById(R.id.mPhoto);
                rvPhoto.setLayoutManager(new CustomLinearLayoutManager(this));
                rvPhoto.setAdapter(new DateAdapter(jsonObject, this, dateArrayList));
            } catch (IOException | JSONException e) {
                Log.e("GalleryAdapter", "Failed to bind grouped data", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) if (grantResults[0] == PackageManager.PERMISSION_GRANTED) loadAllPhoto();
    }
}