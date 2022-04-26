package roomdb;

import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface filePathDAO {


    @Query("Select * from file_path order by rowid asc")
    LiveData<List<filePath>> getAll();

    @Query("SELECT * FROM file_path WHERE rowid = :fileId")
    filePath getById(int fileId);


    @Insert
    void insert(filePath... filePaths);


    @Update
    void update(filePath... filePath);


    @Delete
    void delete(filePath... filePath);


    @Query("DELETE FROM file_path WHERE rowid = :fileId")
    void delete(int fileId);
}