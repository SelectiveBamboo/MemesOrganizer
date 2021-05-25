package net.darold.jules.memesorganizer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "keywords_table")
public class Keyword {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "keyword")
    private String keyword;

    public Keyword(String keyword) { this.keyword = keyword; }

    public String getKeyword() { return keyword; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword1 = (Keyword) o;
        return keyword.equals(keyword1.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }

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