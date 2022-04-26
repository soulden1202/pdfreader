package roomdb;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class filepathViewModel extends AndroidViewModel {
    private LiveData<List<filePath>> filepath;

    public filepathViewModel (Application application) {
        super(application);
        filepath = FilePathDatabase.getDatabase(getApplication()).filePathDAO().getAll();
    }



    public LiveData<List<filePath>> getAllFilePaths() {
        return filepath;
    }
}
