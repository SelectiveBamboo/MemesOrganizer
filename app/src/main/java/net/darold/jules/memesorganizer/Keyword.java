package net.darold.jules.memesorganizer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "keywords_table")
public class Keyword {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "keyword")
    private String keyword;

    public Keyword(String keyword) { this.keyword = keyword; }

    public String getKeyword() { return keyword; }
}