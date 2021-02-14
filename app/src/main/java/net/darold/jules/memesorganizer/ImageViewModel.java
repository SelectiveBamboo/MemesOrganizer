package net.darold.jules.memesorganizer;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {

    private  ImageRepository mImgRepo;
    private final LiveData<List<Image>> mAllImages;
    private final LiveData<List<Keyword>> mAllKeyword;

    public ImageViewModel (Application application)
    {
        super(application);
        mImgRepo = new ImageRepository(application);

        mAllImages = mImgRepo.getAllImages();
        mAllKeyword = mImgRepo.getAllKeywords();
    }


    LiveData<List<Image>> getAllImages(){
        return mAllImages;
    }

    LiveData<List<Keyword>> getAllKeywords(){
        return mAllKeyword;
    }

    List<Image> getAllImagesByName(String name){
        return mImgRepo.getAllImagesByName(name);
    }

    Image getImageByURI(String URI){return mImgRepo.getImageByURI(URI);}

    List<Image> getAllImagesWithKeywords(String matchQuery){return mImgRepo.getAllImagesWithKeywords(matchQuery);}

    List<Keyword> getAlikeKeywords(String keyword){
        return mImgRepo.getAlikeKeywords(keyword);
    }

    void deleteImage(Image image){
        mImgRepo.deleteImage(image);
    }

    void deleteKeyword(Keyword keyword){
        mImgRepo.deleteKeyword(keyword);
    }

    void insertImages(Image... images){
        mImgRepo.insertImages(images);
    }

    void insertKeywords(Keyword... keywords){
        mImgRepo.insertKeywords(keywords);
    }
}
