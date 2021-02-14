package net.darold.jules.memesorganizer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


/**
 * Image class for android room to handle database.
 * An image has an id (unique, autogenerate), a URI (unique), a path (unique), and a name
 *Even though URI and path are unique, an Id is useed as primary key in anticipation
 * of further evolutions not depending on path or URi
 */

@Entity(tableName = "images_table", indices = {@Index(value = {"imageURI"},
        unique = true), @Index(value = {"imagePath"},
        unique = true)})
public class Image {

    @PrimaryKey(autoGenerate = true)
    private long imageId;

    @ColumnInfo(name = "imageURI")
    private String imageURI;

    @ColumnInfo(name = "imageName")
    private String imageName;

    @ColumnInfo(name = "imagePath")
    private String imagePath;

    public Image(@NonNull String imageURI, String imageName, String imagePath)
    {
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.imageURI = imageURI;
    }

    public void setImageId(long imageId){this.imageId = imageId;}

    public long getImageId(){return imageId;}
    public String getImageURI(){return this.imageURI;}
    public String getImageName(){return this.imageName;}
    public String getImagePath(){return this.imagePath;}
}




