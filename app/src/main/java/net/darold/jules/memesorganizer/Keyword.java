package net.darold.jules.memesorganizer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "keywords_table")
public class Keyword {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "keyword")
    private String keyword;

    public Keyword(String keyword) { this.keyword = keyword; }

    public String getKeyword() { return keyword; }

    static List<String> getStrListFromKwrdsList(List<Keyword> kwrds)
    {
        List<String> listStr = new ArrayList<String>();
        for (Keyword kwrd : kwrds) { listStr.add(kwrd.getKeyword()); }

        return listStr;
    }

    static String[] getStrArrayFromKwrdsList(List<Keyword> kwrds)
    {
        List<String> listStr = new ArrayList<String>();
        for (Keyword kwrd : kwrds) { listStr.add(kwrd.getKeyword()); }

        return StringArrayTools.StrListToStrArray(listStr);
    }
}