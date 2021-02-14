package net.darold.jules.memesorganizer;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_fts")
@Fts4(contentEntity = Image.class)
public class ImageFTS {

    @ColumnInfo(name = "imageId")
    private long imageId;

    @ColumnInfo(name = "imageKeywords")
    private String imageKeywords;

    public long getImageId() {return imageId;}
    public String getImageKeywords() {return imageKeywords;}

    public void setImageId(long imageId) {this.imageId = imageId;}

    public void setImageKeywords(String imageKeywords) {this.imageKeywords = imageKeywords;}
}
