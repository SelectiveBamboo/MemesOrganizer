package net.darold.jules.memesorganizer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface imageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(Image... images);

    @Delete
    void deleteImage(Image image);

    @Query("SELECT * FROM images_table")
    List<Image> getAllImages();

    @Query("SELECT * FROM images_table WHERE imageName LIKE :alikeName ORDER BY imageName ASC")
    LiveData<List<Image>> searchAllImagesByName(String alikeName);



}
