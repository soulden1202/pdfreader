package roomdb;
import android.content.Context;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {filePath.class}, version = 1, exportSchema = false)

public abstract class FilePathDatabase extends RoomDatabase {

    public interface filePathListener{
        void  onFilePathReturned(filePath filePaths);
    }


    public abstract filePathDAO filePathDAO();

    private static FilePathDatabase INSTANCE;




    public static FilePathDatabase getDatabase(final Context context){


        if (INSTANCE == null) {
            synchronized (FilePathDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FilePathDatabase.class, "file_path_database")
                            .addCallback(createFilePathDatabaseCallback)
                            .build();
                }
            }
        }




        return INSTANCE;
    }


    private static RoomDatabase.Callback createFilePathDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
    };





    public static void insert(filePath filePath){


    (new Thread(() -> INSTANCE.filePathDAO().insert(filePath))).start();

    }


    public static void getFilePath(int id, filePathListener listener){
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message m){
                super.handleMessage(m);
                listener.onFilePathReturned((filePath) m.obj);
            }
        };
        (new Thread(()->{
            Message m  = handler.obtainMessage();
            m.obj = INSTANCE.filePathDAO().getById(id);
            handler.sendMessage(m);
        })).start();
    }

    public static void delete(int fileID){
        (new Thread(()-> INSTANCE.filePathDAO().delete(fileID))).start();
    }


    public static void update(filePath filePath){
        (new Thread(()-> INSTANCE.filePathDAO().update(filePath))).start();
    }




}






