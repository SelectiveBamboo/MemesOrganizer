package net.darold.jules.memesorganizer;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Image.class, Keyword.class, ImageFTS.class, KeywordsImagesCrossRef.class}, version = 8, exportSchema = false)
public abstract class ImageRoomDatabase extends RoomDatabase {

    public abstract ImageDAO imageDAO();
    public abstract KeywordDAO keywordDAO();
    public abstract KeywordsImagesCrossRefDAO keywordsImagesCrossRefDAO();


    private static volatile ImageRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ImageRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ImageRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImageRoomDatabase.class, "image_keywords_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
