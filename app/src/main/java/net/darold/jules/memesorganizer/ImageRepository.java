package net.darold.jules.memesorganizer;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.security.Key;
import java.util.List;

public class ImageRepository {

    private ImageDAO mImageDAO;
    private KeywordDAO mKeywordDAO;
    private keywordsImagesCrossRefDAO mKeyImaCrossRefDAO;

    private LiveData<List<Image>> mAllImages;
    private LiveData<List<Keyword>> mAllKeyword;


    ImageRepository(Application application) {
        ImageRoomDatabase db = ImageRoomDatabase.getDatabase(application);

        mImageDAO = db.imageDAO();
        mKeywordDAO = db.keywordDAO();
        mKeyImaCrossRefDAO = db.keywordsImagesCrossRefDAO();

        mAllImages = mImageDAO.getAllImages();
        mAllKeyword = mKeywordDAO.getAllKeywords();
    }


    LiveData<List<Image>> getAllImages(){
        return mAllImages;
    }

    LiveData<List<Keyword>> getAllKeywords(){
        return mAllKeyword;
    }

    LiveData<List<keywordsImagesCrossRefDAO.ImageWithKeywords>> getImageWithKeywords(){
        return mKeyImaCrossRefDAO.getImageWithKeywords();
    }

    LiveData<List<keywordsImagesCrossRefDAO.KeywordWithImages>> getKeywordWithImages(){
        return mKeyImaCrossRefDAO.getKeywordWithImages();
    }

    List<Image> getAllImagesByName(String name){
       /* ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mImageDAO.searchAllImagesByName(name);
        });*/
        return mImageDAO.searchAllImagesByName(name);
    }

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

    void deleteKeyword(Keyword keyword){
        ImageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mKeywordDAO.deleteKeyword(keyword);
        });
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
}
