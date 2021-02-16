package net.darold.jules.memesorganizer;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.security.Key;
import java.util.List;

public class ImageRepository {

    private ImageDAO mImageDAO;
    private KeywordDAO mKeywordDAO;

    private List<Image> mAllImages;
    private List<Keyword> mAllKeyword;
    private KeywordsImagesCrossRefDAO mKeyImaCrossRefDAO;


    ImageRepository(Context context) {
        //ImageRoomDatabase db = ImageRoomDatabase.getDatabase(application);
        ImageRoomDatabase db = Room.databaseBuilder(context, ImageRoomDatabase.class, "image_database")
                                .allowMainThreadQueries()
                                .build();

        mImageDAO = db.imageDAO();
        mKeywordDAO = db.keywordDAO();
        mKeyImaCrossRefDAO = db.keywordsImagesCrossRefDAO();

        mAllImages = mImageDAO.getAllImages();
        mAllKeyword = mKeywordDAO.getAllKeywords();
    }


    List<Image> getAllImages(){
        return mAllImages;
    }

    List<Keyword> getAllKeywords(){
        return mAllKeyword;
    }

    List<KeywordsImagesCrossRef.ImageWithKeywords> getImageWithKeywords(){
        return mKeyImaCrossRefDAO.getImageWithKeywords();
    }

    List<KeywordsImagesCrossRef.KeywordWithImages> getKeywordWithImages(){
        return mKeyImaCrossRefDAO.getKeywordWithImages();
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

    Image getImageByURI(String URI){return mImageDAO.searchImageByURI(URI);}

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
