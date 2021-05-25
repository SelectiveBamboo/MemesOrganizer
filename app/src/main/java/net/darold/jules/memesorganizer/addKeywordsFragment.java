package net.darold.jules.memesorganizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

import me.gujun.android.taggroup.TagGroup;

/**
 * A fragment to add keywords to the currently viewed pic in picture_browser
 */
public class addKeywordsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_PATH = "picturePath";
    private static final String ARG_PARAM_URI = "imageURI";
    private static final String ARG_PARAM_NAME = "pictureName";

    private ImageRepository imgRepo;

    private String picturePath;
    private String imageURI;
    private String pictureName;

    //First tagGroup, to display the keywords that WILL be associated to the image
    private TagGroup imageKeywords_TagGroup;
    //Second tagGroup, to display the keywords that COULD be associated to the image
    private TagGroup listKeywords_TagGroup;


    public addKeywordsFragment() {
        super(R.layout.fragment_add_keywords);
    }

    /**
     * To create new instance of this fragment (so no need of fragment manager?)
     * @param picturePath
     * @param imageURI
     * @param pictureName
     * @return
     */
    public static addKeywordsFragment newInstance(String picturePath, String imageURI, String pictureName) {
        addKeywordsFragment fragment = new addKeywordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_PATH, picturePath);
        args.putString(ARG_PARAM_URI, imageURI);
        args.putString(ARG_PARAM_NAME, pictureName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pictureName = getArguments().getString(ARG_PARAM_NAME);
            picturePath = getArguments().getString(ARG_PARAM_PATH);
            imageURI = getArguments().getString(ARG_PARAM_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_add_keywords, container, false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        FloatingActionButton fab = view.findViewById(R.id.fab_confirm_AddKeywords);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAddKeywords();
            }
        });

        imgRepo = new ImageRepository(getContext());

        Image img = imgRepo.getImageByPath(picturePath);

        String[] imageKeywords = new String[] {};
        //Get all the Keywords associated to the current image, if any
        if (img != null)
        {
            KeywordsImagesCrossRef.ImageWithKeywords imageWithKeywords = imgRepo.getImageWithKeywordsById(img.getImageId());

            imageKeywords = StringArrayTools.StrListToStrArray(
                    Keyword.getStrListFromKwrdsList(imageWithKeywords.keywords)
            );
        }


        imageKeywords_TagGroup = (TagGroup) view.findViewById(R.id.tag_group_applying);
        listKeywords_TagGroup = (TagGroup) view.findViewById(R.id.tag_group_list);

        //Fine customisations of first group (image's own keywords)
        imageKeywords_TagGroup.setTags(imageKeywords);
        imageKeywords_TagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String tag) {
                String[] newTagsList = StringArrayTools.removeStringFromStrArray(listKeywords_TagGroup.getTags(), tag);
                listKeywords_TagGroup.setTags(newTagsList);
            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {
                String[] newTagsList = StringArrayTools.addStringToStrArray(listKeywords_TagGroup.getTags(), tag);
                listKeywords_TagGroup.setTags(newTagsList);
            }
        });


        //Get String Array of all keywords
        String[] allKeywords = Keyword.getStrArrayFromKwrdsList(imgRepo.getAllKeywords());

        //Fine customisations of second group (all keywords available)
        if (allKeywords != null)
        {
            //Set the tag group with all the keywords but those already associated to the current image
            String[] listKeywords = StringArrayTools.removeStrArrayFromStrArray(allKeywords, imageKeywords);
            listKeywords_TagGroup.setTags(listKeywords);
        }

        listKeywords_TagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                /**
                 * Things to do when this a tag is clicked
                 * ---Add to other group
                 * ---remove from this group
                 */

                String[] newTagsApplying = StringArrayTools.addStringToStrArray(imageKeywords_TagGroup.getTags(), tag);
                imageKeywords_TagGroup.setTags(newTagsApplying);

                String[] newTagsList = StringArrayTools.removeStringFromStrArray(listKeywords_TagGroup.getTags(), tag);
                listKeywords_TagGroup.setTags(newTagsList);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    void confirmAddKeywords()
    {
        imgRepo = new ImageRepository(getContext());

        String[] strKeywords = imageKeywords_TagGroup.getTags();

        //May the current image never has been added in the database
        imgRepo.insertImages(new Image(imageURI, pictureName, picturePath, StringArrayTools.joinStringArrayIntoString(strKeywords, " "), ""));

        ArrayList<Keyword> keywordsArraylist = new ArrayList<Keyword>();
        for (int i = 0; i < strKeywords.length; i++) {
            keywordsArraylist.add(new Keyword(strKeywords[i]));
        }

        Keyword[] keywords = keywordsArraylist.toArray(new Keyword[keywordsArraylist.size()]);

        //TODO - All this part should be performed through a transaction
        //May new keywords have been created, better to have them in the database so
        imgRepo.insertKeywords(keywords);

        Image currentImage;
        do {
           currentImage = imgRepo.getImageByPath(picturePath);
        }while(currentImage == null);

        String[] keywordsAsStrList =  StringArrayTools.StrListToStrArray( Keyword.getStrListFromKwrdsList( Arrays.asList(keywords) ) );
        imgRepo.updateImageWithKeywords(currentImage.getImageId(), keywordsAsStrList);

        pictureBrowserFragment.hasAddKeywordBtnBeenClicked = false;
        getParentFragmentManager().popBackStackImmediate();
    }
}
