package net.darold.jules.memesorganizer;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName="images_keywords_join",
        primaryKeys = {"keyword", "imageId"})
public class KeywordsImagesCrossRef {
    @NonNull
    public String keyword;
    @NonNull
    public long imageId;

    static class ImageWithKeywords {
        @Embedded
        public Image image;
        @Relation(
                parentColumn = "imageId",
                entityColumn = "keyword",
                associateBy = @Junction(KeywordsImagesCrossRef.class)
        )
        public List<Keyword> keywords;

        public ImageWithKeywords(Image image, List<Keyword> keywords) {
            this.image = image;
            this.keywords = keywords;
        }
    }

    static class KeywordWithImages {
        @Embedded public Keyword keyword;
        @Relation(
                parentColumn = "keyword",
                entityColumn = "imageId",
                associateBy = @Junction(KeywordsImagesCrossRef.class)
        )
        public List<Image> images;
    }
}
