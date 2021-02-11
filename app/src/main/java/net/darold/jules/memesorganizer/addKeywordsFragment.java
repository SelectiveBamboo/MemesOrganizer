package net.darold.jules.memesorganizer;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.darold.jules.memesorganizer.StringArrayTools;

import me.gujun.android.taggroup.TagGroup;

/**
 * A fragment to add keywords to the currently viewed pic in picture_browser
 */
public class addKeywordsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_PATH = "picturePath";
    private static final String ARG_PARAM_URI = "imageURI";
    private static final String ARG_PARAM_NAME = "pictureName";

    private StringArrayTools sarrTools = new StringArrayTools();

    private String picturePath;
    private String imageURI;
    private String pictureName;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_keywords, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //First tagGroup, to display the keywords that WILL be associated to the image
        TagGroup applying_TagGroup = (TagGroup) view.findViewById(R.id.tag_group_applying);
        //Second tagGroup, to display the keywords that COULD be associated to the image
        TagGroup list_TagGroup = (TagGroup) view.findViewById(R.id.tag_group_list);

        //Fine customisations of first group
        applying_TagGroup.setTags(new String[]{"Toug"});
        applying_TagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String tag) {
                /**
                 * Things to do when a new tag is created (append)
                 * ---Add to database if needed
                 * ---Remove from the other group if existing
                 */

                //TODO --- Add to database
                String newTagsList[] = sarrTools.removeStringFromStrArray(list_TagGroup.getTags(), tag);
                list_TagGroup.setTags(newTagsList);
            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {
                /**
                 * Things to do when tag's deleted in the group
                 *
                 *---- Add it to the other group
                 */

                String newTagsList[] = sarrTools.addStringToStrArray(list_TagGroup.getTags(), tag);
                list_TagGroup.setTags(newTagsList);
            }
        });


        //Fine customisations of second group
        list_TagGroup.setTags(new String[]{"Tag1", "Tag2", "Tag3"});
        list_TagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                /**
                 * Things to do when this a tag is clicked
                 * ---Add to other group
                 * ---remove from this group
                 */

                String newTagsApplying[] = sarrTools.addStringToStrArray(applying_TagGroup.getTags(), tag);
                applying_TagGroup.setTags(newTagsApplying);

                String newTagsList[] = sarrTools.removeStringFromStrArray(list_TagGroup.getTags(), tag);
                list_TagGroup.setTags(newTagsList);
            }
        });


//        int nbKeywords = 15;
//        for(int i = 0; i < nbKeywords; i++)
//        {
//
//            // create a new Button
//            Button btn = new Button(getContext());
//
//            // set button text
//            btn.setText("Button " + i);
//
//
//            // set gravity for text within button
//           // btn.setGravity(Gravity.TOP);
//
//            // set button background
//            btn.setBackgroundColor(Color.DKGRAY);
//
//            // set an OnClickListener for the button
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getContext(), "Hola from clicked button", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // declare and initialize LayoutParams for the layout
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//
//// decide upon the positioning of the button //
//// you will likely need to use the screen size to position the
//// button anywhere other than the four corners
//            params.setMargins(80, 80, 80, 0);
//
//
//// add the view
//            layout.addView(btn, params);
//
//            Log.e("DISPLAYING NAME", "Button " + i);
//        }
    }
}
