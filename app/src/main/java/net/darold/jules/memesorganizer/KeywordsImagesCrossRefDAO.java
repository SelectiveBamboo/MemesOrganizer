package net.darold.jules.memesorganizer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Junction;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface KeywordsImagesCrossRefDAO {

    @Transaction
    @Query("SELECT images_table.* FROM images_table " +
            "INNER JOIN images_keywords_join " +
            "ON images_table.imageId = images_keywords_join.imageId")
    List<KeywordsImagesCrossRef.ImageWithKeywords> getAllImagesWithKeywords();

    @Transaction
    @Query("SELECT * FROM images_table WHERE imageId = :imageId LIMIT 1")
    KeywordsImagesCrossRef.ImageWithKeywords getImageWithKeywordsById(long imageId);

    @Transaction
    @Query("SELECT keywords_table.* FROM keywords_table " +
            "INNER JOIN images_keywords_join " +
            "ON keywords_table.keyword = images_keywords_join.keyword")
    List<KeywordsImagesCrossRef.KeywordWithImages> getAllKeywordsWithImages();

    @Insert
    void insertImagesWithKeywords(KeywordsImagesCrossRef ... joins);

    @Delete
    void deleteImagesWithKeywords(KeywordsImagesCrossRef... joins);
}