package com.example.pdfreader;

import static android.os.Build.VERSION.SDK_INT;

import static roomdb.FilePathDatabase.insert;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import roomdb.FilePathDatabase;
import roomdb.filePath;


public class MainActivity extends AppCompatActivity {



    private static final int PERMISSION_REQUEST_CODE = 200;
    private roomdb.filepathViewModel filepathViewModel;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String themetoPass;
    private HashMap<String, String> data = new HashMap<String, String>();

    public List<pdfList> pdfs = new ArrayList<pdfList>();
    public ArrayList<String> key = new ArrayList<String>();
    public static WeakReference<MainActivity> weakActivity;



    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weakActivity = new WeakReference<>(MainActivity.this);

        //check permission
        if (checkPermission()) {

            RecyclerView recyclerView = findViewById(R.id.rv);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            View mainac = findViewById(R.id.mainactivity);

            //Getting current theme
            themetoPass = prefs.getString("themePref", "");
            if( themetoPass.equals("1"))
            {
                mainac.setBackgroundColor(getResources().getColor(R.color.white));
            }
            else if( themetoPass.equals("2"))
            {
                mainac.setBackgroundColor(getResources().getColor(R.color.black));
            }

            //listener when theme setting changed
            listener = (prefs1, key) -> {
                if (key.equals("themePref")) {
                    String theme = prefs1.getString("themePref", "");
                    Log.e("Theme", theme);
                    if( theme.equals("1"))
                    {
                        mainac.setBackgroundColor(getResources().getColor(R.color.white));
                       themetoPass = "1";
                    }
                    else if( theme.equals("2"))
                    {
                        mainac.setBackgroundColor(getResources().getColor(R.color.black));
                        themetoPass = "2";
                    }
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(listener);

            //setting up adapter
            MainAdapter adapter = new MainAdapter(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            setLanguages();
            filepathViewModel = new ViewModelProvider(this).get(roomdb.filepathViewModel.class);
            filepathViewModel.getAllFilePaths().observe(this, adapter::setfilePaths);


        } else {
            requestPermission();
        }




    }

    //Setting menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //theme selection
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("theme", themetoPass);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    //list of languages for translation
    public void setLanguages(){
        InputStream text = this.getResources().openRawResource(R.raw.langlist);
        Scanner sc = new Scanner(text);
        while(sc.hasNext())
        {
            String line = sc.nextLine();
            line = line.replaceAll("\\s", "");
            String textsplit[] = line.split(",",2);
            data.put(textsplit[1], textsplit[0]);
            key.add(textsplit[0]);

        }




    }




    public void viewPdf(int id) throws IOException {

        FilePathDatabase.getFilePath(id, filePath -> {
                    Intent intent = new Intent(MainActivity.this, pdfViewActivity.class);
                    intent.putExtra("item_path", filePath.path);
                    intent.putExtra("lang-key", key);
                    intent.putExtra("map", data);
                    intent.putExtra("theme", themetoPass);
                    intent.putExtra("id", filePath.id);
                    intent.putExtra("lastpage", filePath.lastPage);
                    startActivity(intent);
                }
        );

        }







    public boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                    setLanguages();
                    Search_Dir(Environment.getExternalStorageDirectory());

                    RecyclerView recyclerView = findViewById(R.id.rv);


                    //setting the theme
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    View mainac = findViewById(R.id.mainactivity);
                    themetoPass = prefs.getString("themePref", "");
                    if( themetoPass.equals("1"))
                    {
                        mainac.setBackgroundColor(getResources().getColor(R.color.white));

                    }
                    else if( themetoPass.equals("2"))
                    {
                        mainac.setBackgroundColor(getResources().getColor(R.color.black));

                    }

                    listener = (prefs1, key) -> {
                        if (key.equals("themePref")) {
                            String theme = prefs1.getString("themePref", "");
                            Log.e("Theme", theme);
                            if( theme.equals("1"))
                            {
                                mainac.setBackgroundColor(getResources().getColor(R.color.white));
                                themetoPass = "1";

                            }
                            else if( theme.equals("2"))
                            {
                                mainac.setBackgroundColor(getResources().getColor(R.color.black));
                                themetoPass = "2";

                            }


                        }
                    };

                    prefs.registerOnSharedPreferenceChangeListener(listener);


                    MainAdapter adapter = new MainAdapter(this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    filepathViewModel = new ViewModelProvider(this).get(roomdb.filepathViewModel.class);
                    filepathViewModel.getAllFilePaths().observe(this, adapter::setfilePaths);
                    insetingToDB(pdfs);
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }





    public static MainActivity getmInstanceActivity() {
        return weakActivity.get();
    }

    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";
        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern)) {
                        pdfs.add(new pdfList(FileList[i].getName(),FileList[i].getAbsolutePath(),FileList[i]));
                    }
                }
            }
        }
    }



    public void insetingToDB(List<pdfList> pdfs){
        for(int i = 0; i< pdfs.size(); i++){
            insert(new filePath(0, pdfs.get(i).getDisplayName(),pdfs.get(i).getFilePath(),0));
        }

    }


}