package net.darold.jules.memesorganizer;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 *Author CodeBoy722
 *
 * picture_Adapter's ViewHolder
 */

public class PicHolder extends RecyclerView.ViewHolder{

    public ImageView picture;

    PicHolder(@NonNull View itemView) {
        super(itemView);

        picture = itemView.findViewById(R.id.image);
    }
}
