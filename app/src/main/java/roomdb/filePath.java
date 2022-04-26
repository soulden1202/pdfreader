package roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;

@Entity(tableName="file_path")
public class filePath {



    public filePath(int id, String title, String path, int lastPage) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.lastPage = lastPage;

    }



    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "last_page")
    public int lastPage;



















} 