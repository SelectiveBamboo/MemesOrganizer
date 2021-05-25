package net.darold.jules.memesorganizer;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

public class ImageRepository {

    private ImageDAO mImageDAO;
    private KeywordDAO mKeywordDAO;

    private List<Image> mAllImages;
    private List<Keyword> mAllKeyword;
    private KeywordsImagesCrossRefDAO mKeyImaCrossRefDAO;

    private static ImageRoomDatabase db;

    ImageRepository(Context context) {
        //ImageRoomDatabase db = ImageRoomDatabase.getDatabase(application);
        if (db == null) {
            db = Room.databaseBuilder(context, ImageRoomDatabase.class, "image_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        mImageDAO = db.imageDAO();
        mKeywordDAO = db.keywordDAO();
        mKeyImaCrossRefDAO = db.keywordsImagesCrossRefDAO();
    }


    List<Image> getAllImages(){
        return mImageDAO.getAllImages();
    }

    List<Keyword> getAllKeywords(){
        return mKeywordDAO.getAllKeywords();
    }

    List<Keyword> getAllKeywordsNotIn(String[] notInKeywords){
        return mKeywordDAO.getAllKeywordsNotIn(notInKeywords);
    }

    List<KeywordsImagesCrossRef.ImageWithKeywords> getImageWithKeywords(){
        return mKeyImaCrossRefDAO.getAllImagesWithKeywords();
    }

    List<KeywordsImagesCrossRef.KeywordWithImages> getKeywordWithImages(){
        return mKeyImaCrossRefDAO.getAllKeywordsWithImages();
    }

    KeywordsImagesCrossRef.ImageWithKeywords getImageWithKeywordsById(long imageId){
        return mKeyImaCrossRefDAO.getImageWithKeywordsById(imageId);
    }

    List<Image> getAllImagesByName(String name){
       /* ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mImageDAO.searchAllImagesByName(name);
        });*/

        return mImageDAO.searchAllImagesByName(name);
    }

    List<Image> searchAllImagesWithKeywords(String matchQuery){return mImageDAO.searchAllImagesWithKeywords(matchQuery);}

    Image getImageByPath(String path){return mImageDAO.searchImageByPath(path);}

    List<Image> getAllImagesWithKeywords(String matchQuery){return mImageDAO.searchAllImagesWithKeywords(matchQuery);}

    List<Keyword> getAlikeKeywords(String keyword){
        /*ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mKeywordDAO.searchAlikeKeywords(keyword);
        });*/
        return mKeywordDAO.searchAlikeKeywords(keyword);
    }

    void deleteImage(Image image){
        ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mImageDAO.deleteImage(image);
        });
    }

    void deleteKeyword(Keyword... keywords){
        ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mKeywordDAO.deleteKeyword(keywords);
        });
        Log.e("88888888888888888888", "deleteKeyword: " + keywords.length);
    }

    void insertImages(Image... images){
        ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mImageDAO.insertImages(images);
        });
    }

    void insertKeywords(Keyword... keywords){
        ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mKeywordDAO.insertKeywords(keywords);
        });

    }

    void updateImageWithKeywords(long imageId, String[] keywords) {
        for (int i = 0; i < keywords.length ; i++) {
            mKeyImaCrossRefDAO.insertImageWithKeyword(imageId, keywords[i]);
        }

        mKeyImaCrossRefDAO.deleteKeywordsInImages(imageId, keywords);

        String strKeywords = StringArrayTools.joinStringArrayIntoString(keywords, " ");
        mImageDAO.insertKeywordsImageForFTS(strKeywords, imageId);
//        ImageRoomDatabase.databaseWriteExecutor.execute(()-> {
//            mKeyImaCrossRefDAO.insertImageWithKeyword(imageId, keyword);
//        });
    }
}
