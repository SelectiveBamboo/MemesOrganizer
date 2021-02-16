package net.darold.jules.memesorganizer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(Image... images);

    @Delete
    void deleteImage(Image image);

    @Query("SELECT * FROM images_table")
    List<Image> getAllImages();

    @Query("SELECT * FROM images_table WHERE imageURI = :URI LIMIT 1")
    Image searchImageByURI(String URI);

    @Query("SELECT * FROM images_table WHERE imageName LIKE :alikeName ORDER BY imageName ASC")
    List<Image> searchAllImagesByName(String alikeName);

    @Query("SELECT * FROM images_table JOIN image_fts ON images_table.imageId = image_fts.imageId WHERE image_fts MATCH :matchQuery")
    List<Image> searchAllImagesWithKeywords(String matchQuery);


}
