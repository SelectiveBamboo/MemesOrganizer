package net.darold.jules.memesorganizer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface KeywordDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertKeywords(Keyword... keywords);

    @Delete
    void deleteKeyword(Keyword... keyword);

    @Query("SELECT * FROM keywords_table ORDER BY keyword ASC")
    List<Keyword> getAllKeywords();

    @Query("SELECT * FROM keywords_table WHERE keyword NOT IN (:notInKeywords) ORDER BY keyword ASC")
    List<Keyword> getAllKeywordsNotIn(String[] notInKeywords);

    @Query("SELECT * FROM keywords_table WHERE keyword LIKE :alikeKeyword ORDER BY keyword ASC")
    List<Keyword> searchAlikeKeywords(String alikeKeyword);
}
