package net.darold.jules.memesorganizer;

import androidx.room.Entity;

@Entity(primaryKeys = {"keyword", "imageId"})
public class KeywordsImagesCrossRef {
    public long keyword;
    public long imageId;
}
