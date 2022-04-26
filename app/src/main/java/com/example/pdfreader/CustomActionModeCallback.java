package com.example.pdfreader;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import java.util.HashMap;

public class CustomActionModeCallback implements ActionMode.Callback {

    private TextView mTextView;
    private LinearLayoutCompat translator;
    private EditText transf;
    private Translate translate;
    private HashMap<String, String> data;
    private  Spinner spinner;
    private  Spinner spinner1;
    private lastPosition lastPosition;









    public CustomActionModeCallback( TextView mTextView, LinearLayoutCompat translator, EditText transf, Translate translation, HashMap<String, String> data, Spinner spinner, Spinner spinner1, lastPosition lastPosition) {

        this.mTextView = mTextView;
        this.translator = translator;
        this.transf =transf;
        this.translate = translation;
        this.data = data;
        this.spinner =spinner;
        this.spinner1 = spinner1;
        this.lastPosition = lastPosition;

    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        actionMode.getMenuInflater().inflate(R.menu.menu_custom, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {


        //get the text selected
        if (menuItem.getItemId() == R.id.custom_translate) {
            int min = 0;
            int max = mTextView.getText().length();
            if (mTextView.isFocused()) {
                final int selStart = mTextView.getSelectionStart();
                final int selEnd = mTextView.getSelectionEnd();

                min = Math.max(0, Math.min(selStart, selEnd));
                max = Math.max(0, Math.max(selStart, selEnd));
            }

            final CharSequence selectedText = mTextView.getText().subSequence(min, max);


            //set the translation menu to visible and bring it to the front
            translator.setVisibility(View.VISIBLE);
            translator.bringToFront();

            //language detection
            Detection detect = translate.detect(selectedText.toString());

            //get the language
            String currentLang = data.get(detect.getLanguage());

            //get language index in spinner
            int pos = getIndex(spinner, currentLang);

            //set spinner selection to the language index
            spinner.setSelection(pos);

            //set selected text to the text view
            transf.setText(selectedText.toString());

            //set the second spinner to the last selection
            int pos1 = lastPosition.getI();

            if (pos1 == -1)
            {
                spinner1.setSelection(0);
            }
            else {
                spinner1.setSelection(pos1);
            }
            actionMode.finish();



            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){


                return i;
            }
        }

        return 0;
    }
}
