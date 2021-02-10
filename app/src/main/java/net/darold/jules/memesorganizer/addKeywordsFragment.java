package net.darold.jules.memesorganizer;

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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import me.gujun.android.taggroup.TagGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addKeywordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addKeywordsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_PATH = "picturePath";
    private static final String ARG_PARAM_URI = "imageURI";
    private static final String ARG_PARAM_NAME = "pictureName";

    private String picturePath;
    private String imageURI;
    private String pictureName;


    public addKeywordsFragment() {
        super(R.layout.fragment_add_keywords);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment addKeywordsFragment.
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

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.add_keywords_fragment_Layout);

        TagGroup mTagGroup = (TagGroup) view.findViewById(R.id.tag_group);
        mTagGroup.setTags(new String[]{"Tag1", "Tag2", "Tag3"});

        int nbKeywords = 15;
        for(int i = 0; i < nbKeywords; i++)
        {

            // create a new Button
            Button btn = new Button(getContext());

            // set button text
            btn.setText("Button " + i);


            // set gravity for text within button
           // btn.setGravity(Gravity.TOP);

            // set button background
            btn.setBackgroundColor(Color.DKGRAY);

            // set an OnClickListener for the button
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Hola from clicked button", Toast.LENGTH_SHORT).show();
                }
            });

            // declare and initialize LayoutParams for the layout
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

// decide upon the positioning of the button //
// you will likely need to use the screen size to position the
// button anywhere other than the four corners
            params.setMargins(80, 80, 80, 0);


// add the view
            layout.addView(btn, params);

            Log.e("DISPLAYING NAME", "Button " + i);
        }
    }
}
