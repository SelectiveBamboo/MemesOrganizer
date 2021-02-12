package net.darold.jules.memesorganizer;

import androidx.lifecycle.LiveData;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;

import java.util.List;

public interface keywordsImagesCrossRefDAO {

    class ImageWithKeywords {
        @Embedded
        public Image image;
        @Relation(
                parentColumn = "imageId",
                entityColumn = "keyword",
                associateBy = @Junction(KeywordsImagesCrossRef.class)
        )
        public List<Keyword> keywords;
    }

     class KeywordWithImages {
        @Embedded public Keyword keyword;
        @Relation(
                parentColumn = "keyword",
                entityColumn = "imageId",
                associateBy = @Junction(KeywordsImagesCrossRef.class)
        )
        public List<Image> images;
    }

    @Transaction
    @Query("SELECT * FROM images_table")
    LiveData<List<ImageWithKeywords>> getImageWithKeywords();

    @Transaction
    @Query("SELECT * FROM keywords_table")
     LiveData<List<KeywordWithImages>> getKeywordWithImages();


}
