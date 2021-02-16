package net.darold.jules.memesorganizer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface KeywordsImagesCrossRefDAO {

    @Transaction
    @Query("SELECT * FROM images_table")
    List<KeywordsImagesCrossRef.ImageWithKeywords> getImageWithKeywords();

    @Transaction
    @Query("SELECT * FROM images_table WHERE imageId = :imageId LIMIT 1")
    KeywordsImagesCrossRef.ImageWithKeywords getImageWithKeywordsById(long imageId);

    @Transaction
    @Query("SELECT * FROM keywords_table")
    List<KeywordsImagesCrossRef.KeywordWithImages> getKeywordWithImages();

}