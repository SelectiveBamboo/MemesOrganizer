package net.darold.jules.memesorganizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

public class keywordManagementActivity extends AppCompatActivity {

    private static final String TAG =  keywordManagementActivity.class.getSimpleName();
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private ImageRepository imgRepo;

    //tagGroup to manage keywords
    private TagGroup management_TagGroup;

    private String[] allKeywords;

    private List<String> tagsToAdd = new ArrayList<String>();
    private List<String>  tagsToDelete = new ArrayList<String>();

    private List<Keyword> keywordsToAdd = new ArrayList<Keyword>();
    private List<Keyword> keywordsToDelete = new ArrayList<Keyword>();

    private Drawer result;

    private boolean hasAlreadyClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_management);

        toolbar = findViewById(R.id.keywordManagementToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(TAG);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorContrast));


        imgRepo = new ImageRepository(getApplicationContext());
        allKeywords = Keyword.getStrArrayFromKwrdsList(imgRepo.getAllKeywords());

        management_TagGroup = findViewById(R.id.management_tagGroup);
        management_TagGroup.setTags(allKeywords);

        management_TagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String tag) {
                keywordsToDelete.remove(new Keyword(tag));
                keywordsToAdd.add(new Keyword(tag));
                hasAlreadyClicked = false;
            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {
                keywordsToAdd.remove(new Keyword(tag));
                keywordsToDelete.add(new Keyword(tag));
                hasAlreadyClicked = false;
            }
        });

        fab = findViewById(R.id.fab_confirm_keywordManagement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imgRepo.insertKeywords(Arrays.copyOf(keywordsToAdd.toArray(), keywordsToAdd.toArray().length, Keyword[].class));
                    imgRepo.deleteKeyword(Arrays.copyOf(keywordsToDelete.toArray(), keywordsToDelete.toArray().length, Keyword[].class));

                    keywordsToDelete.clear();
                    keywordsToAdd.clear();

                    Toast.makeText(keywordManagementActivity.this, "Done ! Keywords Table updated ! ", Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(keywordManagementActivity.this, "Failed ! Something wrong happened", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: ", e);
                }
            }
        });

        result = DrawerCreator.getDrawer(this, toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        result.setSelection(DrawerCreator.KEYWORDS_MANAGEMENT_DRAWER_ID);

    }

    @Override
    public void onBackPressed() {
        if (!keywordsToAdd.isEmpty() || !keywordsToDelete.isEmpty() && !hasAlreadyClicked)
        {
            Toast.makeText(keywordManagementActivity.this, "Changes have been made but not validated... \n Changes must be validated to take effect, try again to leave anyway", Toast.LENGTH_LONG).show();
            hasAlreadyClicked = true;
        }
        else
            super.onBackPressed();
    }
}