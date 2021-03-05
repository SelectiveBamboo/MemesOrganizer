package net.darold.jules.memesorganizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
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
    private TagGroup allKeywords_TagGroup;


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

        getActivity().findViewById(R.id.searchView_ImagesKeywords).setVisibility(View.INVISIBLE);

        if (getArguments() != null) {
            pictureName = getArguments().getString(ARG_PARAM_NAME);
            picturePath = getArguments().getString(ARG_PARAM_PATH);
            imageURI = getArguments().getString(ARG_PARAM_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_keywords, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        allKeywords_TagGroup = (TagGroup) view.findViewById(R.id.tag_group_list);

        //Fine customisations of first group (image's own keywords)
        imageKeywords_TagGroup.setTags(imageKeywords);
        imageKeywords_TagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String tag) {
                /**
                 * Things to do when a new tag is created (append)
                 * ---Remove from the other group if existing
                 */

                String[] newTagsList = StringArrayTools.removeStringFromStrArray(allKeywords_TagGroup.getTags(), tag);
                allKeywords_TagGroup.setTags(newTagsList);
            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {
                /**
                 * Things to do when tag's deleted in the group
                 *
                 *---- Add it to the other group
                 */

                String[] newTagsList = StringArrayTools.addStringToStrArray(allKeywords_TagGroup.getTags(), tag);
                allKeywords_TagGroup.setTags(newTagsList);
            }
        });


        //Get String Array of all keywords
        String[] allKeywords = Keyword.getStrArrayFromKwrdsList(imgRepo.getAllKeywords());

        //Fine customisations of second group (all keywords available)
        if (allKeywords != null)
        { allKeywords_TagGroup.setTags(allKeywords); }

        allKeywords_TagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                /**
                 * Things to do when this a tag is clicked
                 * ---Add to other group
                 * ---remove from this group
                 */

                String[] newTagsApplying = StringArrayTools.addStringToStrArray(imageKeywords_TagGroup.getTags(), tag);
                imageKeywords_TagGroup.setTags(newTagsApplying);

                String[] newTagsList = StringArrayTools.removeStringFromStrArray(allKeywords_TagGroup.getTags(), tag);
                allKeywords_TagGroup.setTags(newTagsList);
            }
        });

    }



    void confirmAddKeywords()
    {
        imgRepo = new ImageRepository(getContext());

        String[] strKeywords = imageKeywords_TagGroup.getTags();

        //May the current image never have been in the database before
        imgRepo.insertImages(new Image(imageURI, pictureName, picturePath, StringArrayTools.joinStringArrayIntoString(strKeywords, " "), ""));

        ArrayList<Keyword> keywordsArraylist = new ArrayList<Keyword>();
        for (int i = 0; i < strKeywords.length; i++) {
            keywordsArraylist.add(new Keyword(strKeywords[i]));
        }

        Keyword[] keywords = keywordsArraylist.toArray(new Keyword[keywordsArraylist.size()]);

        //TODO - All this part should be performed through a transaction
        //May new keywords have been created, better to have them in the database so
        imgRepo.insertKeywords(keywords);

        Image currentImage = imgRepo.getImageByPath(picturePath);

        String[] keywordsAsStrList =  StringArrayTools.StrListToStrArray( Keyword.getStrListFromKwrdsList( Arrays.asList(keywords) ) );
        imgRepo.updateImageWithKeywords(currentImage.getImageId(), keywordsAsStrList);

        pictureBrowserFragment.hasAddKeywordBtnBeenClicked = false;
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }
}
