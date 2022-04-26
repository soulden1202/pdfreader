package com.example.pdfreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import roomdb.filePath;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {



    private final LayoutInflater layoutInflater;
    public MainAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);

    }


    public class MainViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public CardView cardView;
        public ImageView pdfImage;
        private filePath filePath;

        private MainViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.pdf_textName);
            cardView =itemView.findViewById(R.id.pdf_cardView);
            pdfImage = itemView.findViewById(R.id.pdf_imageView);
            
            cardView.setOnClickListener(view-> {
                try {
                    MainActivity.getmInstanceActivity().viewPdf(filePath.id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }




    private List<filePath> filePaths;
    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.rv_item, parent, false);
        return new MainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        if (filePaths != null) {
            filePath current = filePaths.get(position);
            File file = new File(current.path);
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (parcelFileDescriptor != null) {
                PdfRenderer pdfRenderer = null;
                try {
                    pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PdfRenderer.Page currentPage = pdfRenderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                        Bitmap.Config.ARGB_8888);

                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                holder.pdfImage.setImageBitmap(bitmap);

                holder.filePath = current;
                String[] splitTitle = current.title.split(".pdf", 2);
                holder.txtName.setSelected(true);
                holder.txtName.setText(splitTitle[0]);

            } else {
                holder.txtName.setText("...intializing...");


            }
        }
    }

    @Override
    public int getItemCount() {
        if (filePaths != null)
            return filePaths.size();
        else return 0;
    }


    void setfilePaths(List<roomdb.filePath> filePaths){
        this.filePaths = filePaths;
        notifyDataSetChanged();
    }
}
