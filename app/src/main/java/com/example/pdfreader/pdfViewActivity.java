package com.example.pdfreader;



import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import roomdb.FilePathDatabase;




public class pdfViewActivity extends AppCompatActivity {

    public static WeakReference<pdfViewActivity> weakActivity1;

   private String itemPath;
   private ArrayList<String> key;
   private HashMap<String, String> data;
   private String theme;
   private int id;
   private int lastpage;


   private lastPagevisit lastPagevisit = new lastPagevisit();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);

        Bundle extras = getIntent().getExtras();
        itemPath = extras.getString("item_path");
        key = extras.getStringArrayList("lang-key");
        data = (HashMap<String, String>) extras.getSerializable("map");
        theme = extras.getString("theme");
        id = extras.getInt("id");
        lastpage =extras.getInt("lastpage");



        lastPosition lastPosition = new lastPosition();
        lastPosition.setI(-1);








        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, key);







        weakActivity1 = new WeakReference<>(pdfViewActivity.this);


        View pdfacac = findViewById(R.id.pdfac);

        if( theme.equals("1"))
        {
            pdfacac.setBackgroundColor(getResources().getColor(R.color.white));

        }
        else if( theme.equals("2"))
        {
            pdfacac.setBackgroundColor(getResources().getColor(R.color.black));

        }


        RecyclerView recyclerView = findViewById(R.id.rv_view);
        pdfAdapter adapter = new pdfAdapter(this, adapter1);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //setting up translation service
        Trans trans = new Trans(this);
        trans.getTranslateService();
        Translate translate = trans.getTranslation();




        //auto scroll to the last page that user visit
        try {
            adapter.setRenderer(itemPath, translate,data ,lastPosition, theme, lastPagevisit);
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this){
                @Override protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(lastpage);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            layoutManager.startSmoothScroll(smoothScroller);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void closeBtn(LinearLayoutCompat translator, TextView pdfPage){
        translator.setVisibility(View.GONE);
        pdfPage.bringToFront();
    }

    void swBtn(Spinner sp, Spinner sp1, EditText ed, EditText ed1){

        String temp = ed1.getText().toString();
        ed1.setText(ed.getText());
        ed.setText(temp);

        int i = sp.getSelectedItemPosition();
        int y = sp1.getSelectedItemPosition();

        sp.setSelection(y);
        sp1.setSelection(i);




    }

    void translateBtn(Spinner spinner1, EditText etext, EditText etext1, Translate translate, HashMap<String, String> data){
        String originalText = etext.getText().toString();
        String targetLang = spinner1.getSelectedItem().toString();

        String targetkey = getKeyFromValue(data, targetLang);
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(targetkey), Translate.TranslateOption.model("base"));

        etext1.setText(translation.getTranslatedText());

    }

    public static pdfViewActivity getmInstanceActivity() {
        return weakActivity1.get();
    }


    public static String getKeyFromValue(HashMap<String, String> data, String value) {
        for (String o : data.keySet()) {
            if (data.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }




    //if back button pressed get the last page that user visit and update it to db
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int lastPage = lastPagevisit.getLastPage();
        FilePathDatabase.getFilePath(id, filePath -> {
            filePath.lastPage = lastPage;
            FilePathDatabase.update(filePath);
        }
        );




    }


}