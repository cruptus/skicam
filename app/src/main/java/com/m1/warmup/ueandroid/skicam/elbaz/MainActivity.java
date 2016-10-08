package com.m1.warmup.ueandroid.skicam.elbaz;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialogChargement;
    private String TAG = MainActivity.class.getSimpleName();
    private JSONObject jsonObj;
    private ListView listView;
    private ArrayList<HashMap<String, Object>> list_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        list_json = new ArrayList<>();
        new GetContacts().execute();
    }

    private void charger() {
//        progressDialogChargement = new ProgressDialog(MainActivity.this);
//        progressDialogChargement.setTitle("Veuillez patienter");
//        progressDialogChargement.setMessage("Chargement ...");
//        progressDialogChargement.setCancelable(false);
//        progressDialogChargement.setButton(DialogInterface.BUTTON_NEUTRAL, "Recharger", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                progressDialogChargement.setTitle("retry");
//            }
//        });
//
//        progressDialogChargement.setMessage(HttpHandler.getJSON("http://mntrns.esy.es/SkiCam/l3skicam.php"));
//        progressDialogChargement.show();

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogChargement = new ProgressDialog(MainActivity.this);
            progressDialogChargement.setTitle("Veuillez patienter");
            progressDialogChargement.setMessage("Chargement ...");
            progressDialogChargement.setCancelable(false);
            progressDialogChargement.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.getJSON("http://mntrns.esy.es/SkiCam/l3skicam.php");


            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);
                    JSONArray jsonArray = jsonObj.getJSONArray("webcams");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject json = jsonArray.getJSONObject(i);

                        HashMap<String, Object> hashmap = new HashMap<>();
                        if(json.has("nom"))
                            hashmap.put("nom", json.getString("nom"));
                        if(json.has("url")) {
                            hashmap.put("url", getImageFromURL(json.getString("url")));
                        }
                        if(json.has("place")) {
                            try {
                                JSONObject jsontemp = new JSONObject(sh.getJSON("http://api.openweathermap.org/data/2.5/weather?q=" +
                                        json.getString("place") +
                                        "&appid=6249b78c999b2ddbea6e7bbc7cfcd33f&units=metric")

                                );
                                hashmap.put("place", jsontemp.getJSONObject("main").getString("temp")+"°C");
                                hashmap.put("coord", json.getString("place"));
                            }catch (Exception e){
                                hashmap.put("place", "ERROR");
                            }
                        }
                        if(json.has("phone"))
                            hashmap.put("phone", json.getString("phone"));

                        list_json.add(hashmap);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
//            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, list_json,R.layout.item_list_cam,
//                    new String[]{"url", "nom", "place"}, new int[]{R.id.imageView, R.id.title_textview, R.id.temperature_textView}
//                    );
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);

            CustomAdapter adapter = new CustomAdapter(MainActivity.this, list_json);

            listView.setAdapter(adapter);
            if(list_json.size() == 0){
                progressDialogChargement.setMessage("Erreur ...");
            }else {
                progressDialogChargement.dismiss();
            }
        }

        protected Bitmap getImageFromURL(String url) {
            Bitmap bitmap = null;
            try {
                InputStream is = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
                Log.e(TAG, "getImageFromURL : " + e.getMessage());
            }
            return bitmap;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    this.finish();
                    System.exit(0);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
