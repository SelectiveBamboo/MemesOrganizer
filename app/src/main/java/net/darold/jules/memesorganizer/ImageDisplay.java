package net.darold.jules.memesorganizer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 *
 * This Activity get a path to a folder that contains images from the MainActivity Intent and displays
 * all the images in the folder inside a RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String folderPath;
    String folderName;
    pictureBrowserFragment browser;
    String TAG = "Image Display Activity";

    ImageRepository imgRepo;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        imgRepo = new ImageRepository(getApplicationContext());

        imageRecycler = findViewById(R.id.recycler);

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        toolbar = findViewById(R.id.imageDisplayToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorContrast));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        allpictures = new ArrayList<>(0);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);

        // Get the intent, verify the action and get extras
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra("query");

            load.setVisibility(View.VISIBLE);
            if (query.length() > 0)
                allpictures = getPictureFacerOnSearchQuery(query);

            getSupportActionBar().setTitle("Results: " + allpictures.size());
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);;
                    startActivity(goBackIntent);
                    finish();
                }
            });

            imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
            load.setVisibility(View.GONE);
        }
        else if (Intent.ACTION_SEND.equals(action) && type != null)
        {
            if (type.startsWith("image/"))
            {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    handleSendImage(imageUri);
                }
            }
        }
        else
        {
            if (folderPath == null)
                folderPath =  getIntent().getStringExtra("folderPath");
            if (folderName == null)
                folderName =  getIntent().getStringExtra("folderName");

            getSupportActionBar().setTitle(folderName);

            if(allpictures.isEmpty())
            {
                load.setVisibility(View.VISIBLE);
                allpictures = getAllImagesByFolder(folderPath);
                imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
                load.setVisibility(View.GONE);
            }
        }
    }

    private void handleSendImage(Uri imageUri)
    {
        String picturePath = imageUri.getPath().substring(6);
        try{
            int lastPartIndex = picturePath.lastIndexOf("/");

            if (picturePath.length() > 0 && lastPartIndex != -1) {
                Bundle bundle = new Bundle();
                bundle.putString("picturePath", picturePath);
                bundle.putString("imageURI", imageUri.toString());
                bundle.putString("pictureName", picturePath.substring(lastPartIndex));

                folderPath = picturePath.substring(0, lastPartIndex);
                Toast.makeText(getApplicationContext(), folderPath, Toast.LENGTH_LONG).show();

                int position = 0;
                ArrayList<pictureFacer> pics = getAllImagesByFolder(folderPath);

                while (pics.get(position).getPicturePath() != picturePath) {
                    Log.d(TAG, "handleSendImage: picturePath expected: " + pics.get(position).getPicturePath());
                    position++;
                    if (position >= pics.size())
                        throw new Exception(picturePath);
                }

                browser = pictureBrowserFragment.newInstance(pics, position, ImageDisplay.this);

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_image_display, browser)
                        .add(R.id.fragment_container_image_display, addKeywordsFragment.class, bundle)
                        .addToBackStack(null)
                        .commit();
            } else
                throw new Exception("");
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Null path \n error with the sent intent", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "handleSendImage: " + "picturePath = " + picturePath);
            Log.e(TAG, "handleSendImage: " + "imageURI = " + imageUri.toString());
            Log.e(TAG, "handleSendImage: " + "folderPath = " + folderPath );
            finish();
        }
    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStackImmediate();
            if (pictureBrowserFragment.hasAddKeywordBtnBeenClicked == true)
                pictureBrowserFragment.hasAddKeywordBtnBeenClicked = false;
        }

    }

    /**
     *
     * @param holder The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics An ArrayList of all the items in the Adapter
     */
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        browser = pictureBrowserFragment.newInstance(pics,position,ImageDisplay.this);

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position+"picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();
    }



    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) { }

    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictureFacer a custom object that holds data of a given image
     * @param path a String corresponding to a folder path on the device external storage
     */
    public ArrayList<pictureFacer> getAllImagesByFolder(String path) {
        ArrayList<pictureFacer> images = new ArrayList<>();
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA , MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = ImageDisplay.this.getContentResolver().query( allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);
        try {
            cursor.moveToFirst();
            do{
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

              //  pic.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow((MediaStore.Images.Media.EXTERNAL_CONTENT_URI).toString())));

                images.add(pic);
            }while(cursor.moveToNext());
            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();
            for(int i = images.size()-1;i > -1;i--){
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    /**
     * This Method gets all the images matching the fts3 query passed in parameter
     * and adapt it to a list of pictureFacer
     * @param query a String fts query to search images on their associated keywords
     */
    private ArrayList<pictureFacer> getPictureFacerOnSearchQuery(String query) {

        List<Image> images = imgRepo.searchAllImagesWithKeywords(query);

        ArrayList<pictureFacer> picsArray = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            pictureFacer pic = new pictureFacer();

            pic.setImageUri(images.get(i).getImageURI());
            pic.setPicturePath(images.get(i).getImagePath());
            pic.setPicturName(images.get(i).getImageName());

            picsArray.add(pic);
        }
        return picsArray;
    }


    /**
     * Method called by the menu icon addKeywords once clicked on
     * Use the launch AddKeywordsFragment method from the instantiated pictureBrowserFragment
     */
    public void addKeywordsFromBrowserFragment(MenuItem menuItem)
    {
        browser.launchAddKeywordsFragment();
    }

    public void sharePicture(MenuItem menuItem)
    {
        browser.sharePicture();
    }
}
