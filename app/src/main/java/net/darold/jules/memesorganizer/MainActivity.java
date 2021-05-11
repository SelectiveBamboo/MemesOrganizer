package net.darold.jules.memesorganizer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;

/**
 * Author CodeBoy722
 *
 * The main Activity start and loads all folders containing images in a RecyclerView
 * this folders are gotten from the MediaStore by the Method getPicturePaths()
 */
public class MainActivity extends AppCompatActivity implements itemClickListener {

    private RecyclerView folderRecycler;
    private TextView empty;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SearchView searchView;
    private ScrollView scrollview_Search;
    private MenuItem searchItem;
    private TextView noSearchTextView;

    private TagGroup taggroup_suggestion;
    private TagGroup taggroup_selection;

    private ImageRepository imgRepo;

    String[] allKeywords;



    SharedPreferences sharedPref;
    static final String ORDERBY = "orderBy";

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * Request the user for permission to access media files and read images on the device
     * this will be useful as from api 21 and above, if this check is not done the Activity will crash
     *
     * Setting up the RecyclerView and getting all folders that contain pictures from the device
     * the getPicturePaths() returns an ArrayList of imageFolder objects that is then used to
     * create a RecyclerView Adapter that is set to the RecyclerView
     *
     * @param savedInstanceState saving the activity state
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        //____________________________________________________________________________________

        empty = findViewById(R.id.empty);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);

        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorContrast));

        scrollview_Search = findViewById(R.id.scrollView_search_mainActivity);
        noSearchTextView = findViewById(R.id.no_search_selection_textView);

        imgRepo = new ImageRepository(getApplicationContext());

        folderRecycler = findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(this));
        folderRecycler.hasFixedSize();

        sharedPref = getSharedPreferences(getString(R.string.sharedPrefs), MODE_PRIVATE);

        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    setAdapterFolders();
                    swipeRefreshLayout.setRefreshing(false);
                });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.hasFocus())
                {
                    searchView.clearFocus(); //Trigger onFocusChange method, dealing with the expected behavior
                }
                else
                    onBackPressed();
            }
        });

        setAdapterFolders(); //populate the view with folders containing pictures

        changeStatusBarColor();

        Drawer result = DrawerCreator.getDrawer(this, toolbar);
        result.setSelection(DrawerCreator.MAIN_ACTIVITY_DRAWER_ID);
    }

    private void setAdapterFolders() {
        String orderBy = sharedPref.getString(ORDERBY, null);
        ArrayList<imageFolder> folds = getPicturePaths(orderBy);

        if(folds.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds,MainActivity.this,this);
            folderRecycler.setAdapter(folderAdapter);
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
                swipeRefreshLayout.setVisibility(View.VISIBLE);

                return false;
            }
        });


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollview_Search.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    scrollview_Search.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    searchView.onActionViewCollapsed();
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
        intent.setAction(Intent.ACTION_SEARCH);

        searchView.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        startActivity(intent);
        finish();
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
     * @return
     * gets all folders with pictures on the device and loads each of them in a custom object imageFolder
     * the returns an ArrayList of these custom objects
     */
    private ArrayList<imageFolder> getPicturePaths(String orderBy){
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, orderBy);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                }
                else
                {
                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getPath().equals(folderpaths)){
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getPath()+" number of pics ="+picFolders.get(i).getNumberOfPics());
        }

        //reverse order ArrayList
       /* ArrayList<imageFolder> reverseFolders = new ArrayList<>();
        for(int i = picFolders.size()-1;i > reverseFolders.size()-1;i--){
            reverseFolders.add(picFolders.get(i));
        }*/

        return picFolders;
    }


    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     * @param pictureFolderPath a String corresponding to a folder path on the device external storage
     */
    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {
        Intent move = new Intent(MainActivity.this,ImageDisplay.class);
        move.putExtra("folderPath",pictureFolderPath);
        move.putExtra("folderName",folderName);

        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        startActivity(move);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.search_ImagesKeywords);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        handleSearchView(menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Default status bar height 24dp,with code API level 24
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor()
    {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
    }

    public void sortByNameAsc(MenuItem menuItem) {
        String orderQuery = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " ASC";
        sharedPref.edit().putString(ORDERBY,orderQuery).commit();
        setAdapterFolders();
    }

    public void sortByNameDesc(MenuItem menuItem) {
        String orderQuery = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " DESC";
        sharedPref.edit().putString(ORDERBY,orderQuery).commit();
        setAdapterFolders();
    }

    public void sortByDateAsc(MenuItem menuItem) {
        String orderQuery = MediaStore.Images.ImageColumns.DATE_ADDED + " ASC";
        sharedPref.edit().putString(ORDERBY,orderQuery).commit();
        setAdapterFolders();

    }

    public void sortByDateDesc(MenuItem menuItem) {
        String orderQuery = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";
        sharedPref.edit().putString(ORDERBY,orderQuery).commit();
        setAdapterFolders();
    }

    public void refresh(MenuItem menuItem) {
        swipeRefreshLayout.setRefreshing(true);
        setAdapterFolders();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void startKeywordsManagementActivity(MenuItem menuItem) {
        Intent move = new Intent(MainActivity.this,keywordManagementActivity.class);
        startActivity(move);
    }

}