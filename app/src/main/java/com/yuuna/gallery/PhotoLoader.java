package com.yuuna.gallery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PhotoLoader {

    public interface Callback {
        void onLoaded(JSONObject jsonObject, ArrayList<String> dateList);
    }

    public static void load(Context context, Callback callback) {
        File cacheFile = new File(context.getCacheDir(), "photo_cache.json");
        if (cacheFile.exists()) {
            try {
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(builder.toString());
                ArrayList<String> dateList = new ArrayList<>();
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    dateList.add(keys.next());
                }

                callback.onLoaded(jsonObject, dateList);
                return;
            } catch (IOException | JSONException e) {
                Log.e("PhotoLoader", "Failed to read cache", e);
            }
        }

        scanMediaStore(context, callback);
    }

    private static void scanMediaStore(Context context, Callback callback) {
        JSONObject jsonObject = new JSONObject();
        HashMap<String, Long> dateMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        final String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int dateColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            int count = cursor.getCount();

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                long rawDate = cursor.getLong(dateColumnIndex);
                long lDate = rawDate < 100000000000L ? rawDate * 1000L : rawDate;
                String sDate = sdf.format(new Date(lDate));
                dateMap.put(sDate, lDate);

                try {
                    JSONArray array = jsonObject.optJSONArray(sDate);
                    if (array == null) array = new JSONArray();
                    array.put(cursor.getString(dataColumnIndex));
                    jsonObject.put(sDate, array);
                } catch (JSONException e) {
                    Log.e("PhotoLoader", "Failed to group photo", e);
                }
            }

            cursor.close();
        }

        ArrayList<String> dateList = new ArrayList<>(dateMap.keySet());
        Collections.sort(dateList, (a, b) -> Long.compare(dateMap.get(b), dateMap.get(a)));

        // Simpan ke cache
        File cacheFile = new File(context.getCacheDir(), "photo_cache.json");
        try (FileWriter writer = new FileWriter(cacheFile)) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            Log.e("PhotoLoader", "Failed to write cache", e);
        }

        callback.onLoaded(jsonObject, dateList);
    }
}
