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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 * Author CodeBoy722
 *
 * This Activity get a path to a folder that contains images from the MainActivity Intent and displays
 * all the images in the folder inside a RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String folderPath;
    TextView noSearchTextView;
    pictureBrowserFragment browser;
    String TAG = "Image Display Activity";

    FloatingActionButton fabSearch;
    SearchView searchView;
    ScrollView scrollview_Search;
    TagGroup taggroup_suggestion;
    TagGroup taggroup_selection;

    String[] allKeywords;

    ImageRepository imgRepo;

    private Toolbar toolbar;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        imgRepo = new ImageRepository(getApplicationContext());

        imageRecycler = findViewById(R.id.recycler);

        //searchView = findViewById(R.id.searchView_ImagesKeywords);
        scrollview_Search = findViewById(R.id.scrollView_search_imageDisplayActivity);

        noSearchTextView = findViewById(R.id.no_search_selection_textView);

//        fabSearch = findViewById(R.id.fab_search);
//        fabSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSearchRequested();
//            }
//        });


        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        toolbar = (Toolbar) findViewById(R.id.imageDisplayToolbar);
        setSupportActionBar(toolbar);

        allpictures = new ArrayList<>(0);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
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
                    Intent goBackIntent = new Intent(getApplicationContext(), ImageDisplay.class);
                    goBackIntent.putExtra("folderPath", intent.getStringExtra("folderPath"));
                    goBackIntent.putExtra("folderName", intent.getStringExtra("folderName") );
                    startActivity(goBackIntent);
                    finish();
                }
            });

            imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
            load.setVisibility(View.GONE);
        }
        else
        {
            getSupportActionBar().setTitle(getIntent().getStringExtra("folderName"));
            folderPath =  getIntent().getStringExtra("folderPath");

            if(allpictures.isEmpty())
            {
                load.setVisibility(View.VISIBLE);
                allpictures = getAllImagesByFolder(folderPath);
                imageRecycler.setAdapter(new picture_Adapter(allpictures,ImageDisplay.this,this));
                load.setVisibility(View.GONE);
            }
        }
    }

    private void handleSearchView(Menu menu) {

        searchItem = menu.getItem(0);

        allKeywords = Keyword.getStrArrayFromKwrdsList(imgRepo.getAllKeywords());

        taggroup_selection = findViewById(R.id.tag_group_selection);
        taggroup_suggestion = findViewById(R.id.tag_group_suggestion);


        taggroup_suggestion.setTags(allKeywords);
        taggroup_suggestion.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                String[] newTagsApplying = StringArrayTools.addStringToStrArray(taggroup_selection.getTags(), tag);

                allKeywords = StringArrayTools.removeStringFromStrArray(allKeywords, tag);

                taggroup_selection.setTags(newTagsApplying);
                taggroup_suggestion.setTags(allKeywords);

                noSearchTextView.setVisibility(View.INVISIBLE);
            }
        });

        taggroup_selection.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                allKeywords = StringArrayTools.addStringToStrArray(allKeywords, tag);

                String[] newTagsInSelection = StringArrayTools.removeStringFromStrArray(taggroup_selection.getTags(), tag);

                taggroup_selection.setTags(newTagsInSelection);
                taggroup_suggestion.setTags(allKeywords);

                if (newTagsInSelection.length < 1)
                    noSearchTextView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                scrollview_Search.setVisibility(View.INVISIBLE);
                imageRecycler.setVisibility(View.VISIBLE);

                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollview_Search.setVisibility(View.VISIBLE);
                    imageRecycler.setVisibility(View.INVISIBLE);
                }
                else
                {
                    scrollview_Search.setVisibility(View.INVISIBLE);
                    imageRecycler.setVisibility(View.VISIBLE);
                    searchItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                String[] matchingArray = StringArrayTools.getStrArrayContainingStr(allKeywords, newText);
                taggroup_suggestion.setTags(matchingArray);
                return false;
            }
        });
    }

    public void launchSearch(View view){
        String[] tagSelected = taggroup_selection.getTags();


        String searchQuery;
        if (tagSelected.length>0)
            searchQuery = createSearchQuery(tagSelected);
        else
           searchQuery = "";

        Intent intent = new Intent(this, ImageDisplay.class);
        intent.putExtra("query", searchQuery);
        intent.putExtra("folderName", toolbar.getTitle());
        intent.putExtra("folderPath", folderPath);
        intent.setAction(Intent.ACTION_SEARCH);

        searchView.setVisibility(View.INVISIBLE);
        imageRecycler.setVisibility(View.VISIBLE);

        startActivity(intent);
        //finish();
    }

    private String createSearchQuery(String[] elementsQuery) {
        StringBuffer strBuffer = new StringBuffer();

        if (elementsQuery.length > 0) {
            strBuffer.append(elementsQuery[0]);
            for (int i = 1; i < elementsQuery.length; i++) {
                strBuffer.append(' ');
                strBuffer.append(elementsQuery[i]);
            }
        }
        return strBuffer.toString();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_display_menu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.search_ImagesKeywords);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        handleSearchView(menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.search_ImagesKeywords) {
//            Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_LONG).show();
//            onSearchRequested();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {

    }

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
