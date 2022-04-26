package com.example.pdfreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import roomdb.FilePathDatabase;
import roomdb.filePath;

public class pdfAdapter extends RecyclerView.Adapter<pdfAdapter.pdfViewHolder> implements AdapterView.OnItemSelectedListener{


    private Translate translate;
    private HashMap<String, String> data;
    private ArrayAdapter<String> adapter;
    private lastPosition lastPosition;
    private String theme;
    private lastPagevisit lastPagevisit;
    public String textsp;
    public String textsp1;


    private final LayoutInflater layoutInflater;
    public pdfAdapter(Context context, ArrayAdapter<String> adapter) {
        layoutInflater = LayoutInflater.from(context);
        this.adapter = adapter;

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        if(adapterView.getId() == R.id.translate_spiner){
            textsp = adapterView.getItemAtPosition(i).toString();
        }
        //keep track of last language selected so it nor reset the selection
        if(adapterView.getId() == R.id.translate_spiner_1){
            textsp1 = adapterView.getItemAtPosition(i).toString();
            lastPosition.setI(adapterView.getSelectedItemPosition());

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public class pdfViewHolder extends RecyclerView.ViewHolder {


        public TextView pdfPage;
        public ImageView pdfPage_img;
        public LinearLayoutCompat translator;

        private pdfViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfPage = itemView.findViewById(R.id.pdf_page);
            pdfPage_img = itemView.findViewById(R.id.pdf_image);
            translator= itemView.findViewById(R.id.translator);


            //setting up spinner for language selection
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = (Spinner) itemView.findViewById(R.id.translate_spiner);
            Spinner spinner1 = (Spinner) itemView.findViewById(R.id.translate_spiner_1);
            spinner.setAdapter(adapter);
            spinner1.setAdapter(adapter);

            //translator

            translator.setVisibility(View.GONE);

            //closebtn
            Button closebtn = itemView.findViewById(R.id.closeBtn);
            closebtn.setOnClickListener(view-> {
                pdfViewActivity.getmInstanceActivity().closeBtn(translator, pdfPage);
            });

            //swbutn
            EditText etext = itemView.findViewById(R.id.trans_from);
            EditText etext1 = itemView.findViewById(R.id.trans_to);
            Button swbtn =  itemView.findViewById(R.id.swbtn);
            swbtn.setOnClickListener(view -> {
                pdfViewActivity.getmInstanceActivity().swBtn(spinner, spinner1, etext, etext1);
            });


            //translatebtn
            Button trbt = itemView.findViewById(R.id.transbt);
            trbt.setOnClickListener(view -> {
                pdfViewActivity.getmInstanceActivity().translateBtn(spinner1,etext, etext1, translate,data);
            });





            //callback for menu select
            CustomActionModeCallback callback = new CustomActionModeCallback(pdfPage, translator, etext, translate, data, spinner, spinner1, lastPosition);
            pdfPage.setCustomSelectionActionModeCallback(callback);

        }


    }





    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfReader read;

    @NonNull
    @Override
    public pdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = layoutInflater.inflate(R.layout.pdf_page2, parent, false);

        Spinner spinner = (Spinner) itemView.findViewById(R.id.translate_spiner);
        Spinner spinner1 = (Spinner) itemView.findViewById(R.id.translate_spiner_1);
        spinner.setOnItemSelectedListener(this);
        spinner1.setOnItemSelectedListener(this);

        return new pdfViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull pdfViewHolder holder, int position) {

        if(pdfRenderer != null)
        {
            PdfReaderContentParser parser = new PdfReaderContentParser(read);
            ImageDetector imageDetector = new ImageDetector();

            try {
                parser.processContent(position+1, imageDetector);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //image detected
            if (imageDetector.imageFound) {

                currentPage = pdfRenderer.openPage(position);
                Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                        Bitmap.Config.ARGB_8888);
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                holder.pdfPage.setVisibility(View.GONE);
                holder.pdfPage_img.setVisibility(View.VISIBLE);
                holder.translator.setVisibility(View.GONE);
                holder.pdfPage_img.setImageBitmap(bitmap);
                currentPage.close();



            }

            // There is no image rendered on page i
            else {
                String parsedText="";
                try {
                    parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(read, position+1);
                    holder.pdfPage.setText(parsedText);
                    if( theme.equals("1"))
                    {
                        holder.pdfPage.setTextColor(Color.BLACK);

                    }
                    else if( theme.equals("2"))
                    {
                        holder.pdfPage.setTextColor(Color.WHITE);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                holder.pdfPage_img.setVisibility(View.GONE);
                holder.pdfPage.setVisibility(View.VISIBLE);
                holder.translator.setVisibility(View.GONE);
                holder.pdfPage.bringToFront();
                holder.pdfPage.setTextIsSelectable(false);
                holder.pdfPage.measure(-1, -1);//you can specific other values.
                holder.pdfPage.setTextIsSelectable(true);
            }
        }

        //keep track of the last page that user visited
        int finalPosition = position;
        new Thread(()->{
            lastPagevisit.setlastPage(finalPosition);
        }).start();

    }



    @Override
    public int getItemCount() {
        return pdfRenderer.getPageCount();
    }

    public void setRenderer(String filepath, Translate translate, HashMap<String, String> data, lastPosition lastPosition, String theme, lastPagevisit lastPagevisit) throws IOException {

        File file = new File(filepath);

        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            read = new PdfReader(filepath);
            this.translate = translate;
            this.data = data;
            this.lastPosition = lastPosition;
            this.theme = theme;
            this.lastPagevisit = lastPagevisit;
        }
    }

}
