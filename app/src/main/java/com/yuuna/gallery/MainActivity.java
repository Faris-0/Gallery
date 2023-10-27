package com.yuuna.gallery;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 33) {
                String[] PERMISSIONS_STORAGE = {
                        android.Manifest.permission.READ_MEDIA_IMAGES
                };
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            } else {
                String[] PERMISSIONS_STORAGE = {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                };
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            }
        }

        loadAllPhoto();
    }

    private void loadAllPhoto() {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED };
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String[] data = new String[count];
        String[] date = new String[count];

        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        ArrayList<String> dateArrayList = new ArrayList<>();

        // Mboh lah
        ArrayList<GalleryData> galleryDataArrayList = new ArrayList<>();
        ArrayList<PhotoData> photoDataArrayList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int dateColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            //Store the path of the image
            data[i] = cursor.getString(dataColumnIndex);
            date[i] = cursor.getString(dateColumnIndex);
            //
            Long lDate = cursor.getLong(dateColumnIndex);
            String sDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date(lDate * 1000L));
            dateArrayList.add(sDate);
            //
            String json = "{\"data\":\""+cursor.getString(dataColumnIndex)+"\",\"date\":\""+cursor.getString(dateColumnIndex)+"\"}";
            try {
                jsonObjectArrayList.add(new JSONObject(json));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.i("PATH", cursor.getString(dataColumnIndex));

            // MBOH LAH
//            photoDataArrayList.add(new PhotoData(cursor.getString(dataColumnIndex)));
            galleryDataArrayList.add(new GalleryData(sDate, new PhotoData(cursor.getString(dataColumnIndex))));
        }
        // The cursor should be freed up after use with close()
        cursor.close();

        // Remove Same Value
        HashSet<String> hashSet = new HashSet<>(dateArrayList);
        dateArrayList.clear();
        dateArrayList.addAll(hashSet);

        // Convert to milliseconds and sort to the current date
        ArrayList<Long> cDateArrayList = new ArrayList<>();
        for (int i = 0; i < dateArrayList.size(); i++) {
            try {
                Date cDate = new SimpleDateFormat("dd MMMM yyyy").parse(dateArrayList.get(i));
                long ms = cDate.getTime();
                cDateArrayList.add(ms/1000L);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(cDateArrayList, (date1, date2) -> date2.compareTo(date1));

        // Convert to Date
        dateArrayList = new ArrayList<>();
        for (int i = 0; i < cDateArrayList.size(); i++) {
            Long lDate = cDateArrayList.get(i);
            String sDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date(lDate * 1000L));
            dateArrayList.add(sDate);
        }

        // Category By Date but Not Recommendation if have many photos
//        RecyclerView rvPhoto = findViewById(R.id.mPhoto);
//        rvPhoto.setLayoutManager(new CustomLinearLayoutManager(this));
//        rvPhoto.setAdapter(new DateAdapter(jsonObjectArrayList, this, dateArrayList));

        // No Category By Date
//        RecyclerView rvPhoto = findViewById(R.id.mPhoto);
//        rvPhoto.setLayoutManager(new CustomGridLayoutManager(this, calculateNoOfColumns(140)));
//        rvPhoto.setAdapter(new PhotoAdapter(jsonObjectArrayList, this));

        //
        HashMap<String, ArrayList<GalleryData>> ggggg = new HashMap<String, ArrayList<GalleryData>>();
        for (int i = 0; i < galleryDataArrayList.size(); i++) {
            String dateaa = galleryDataArrayList.get(i).getDate();

            if (ggggg.containsKey(dateaa)) {
                ggggg.get(dateaa).add(galleryDataArrayList.get(i));
            } else {
                ArrayList<GalleryData> emptyList = new ArrayList<>();
                emptyList.add(galleryDataArrayList.get(i));
                ggggg.put(dateaa, emptyList);
            }
        }
        // LINK NYA
        // https://stackoverflow.com/questions/39204438/grouping-json-response-with-keys-in-java-android-studio
        ObjectMapper mapper = new ObjectMapper();
        StringWriter result = new StringWriter();
        try {
            mapper.writeValue(result, ggggg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = String.valueOf(result);
        try {
            JSONObject jsonObject = new JSONObject(json);
            RecyclerView rvPhoto = findViewById(R.id.mPhoto);
            rvPhoto.setLayoutManager(new CustomLinearLayoutManager(this));
            rvPhoto.setAdapter(new DateExAdapter(jsonObject, this, dateArrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ///

        // ommiting other recycler view set up, such as adapter and Layout manager set up ..
//        ViewTreeObserver viewTreeObserver = rvPhoto.getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener(() -> calculateCellSize(rvPhoto));
    }

    //
    private HashMap<String, List<JSONObject>> groupDataIntoHashMap(List<JSONObject> listOfPojosOfJsonArray) {

        HashMap<String, List<JSONObject>> groupedHashMap = new HashMap<>();

        for (JSONObject pojoOfJsonArray : listOfPojosOfJsonArray) {

            String hashMapKey = null;
            try {
                hashMapKey = pojoOfJsonArray.getString("date");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(pojoOfJsonArray);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<JSONObject> list = new ArrayList<>();
                list.add(pojoOfJsonArray);
                groupedHashMap.put(hashMapKey, list);
            }
        }


        return groupedHashMap;
    }
    //

    public int calculateNoOfColumns(float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

//    private void calculateCellSize(RecyclerView rvPhoto) {
//        int spanCount = (int) Math.floor(rvPhoto.getWidth() / convertDPToPixels(120));
//        ((GridLayoutManager) rvPhoto.getLayoutManager()).setSpanCount(spanCount);
//    }
//
//    private float convertDPToPixels(int dp) {
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        float logicalDensity = metrics.density;
//        return dp * logicalDensity;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) if (grantResults[0] == PackageManager.PERMISSION_GRANTED) loadAllPhoto();
    }
}