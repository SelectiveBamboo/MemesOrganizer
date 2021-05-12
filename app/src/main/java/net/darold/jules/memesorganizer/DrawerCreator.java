package net.darold.jules.memesorganizer;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class DrawerCreator {

    public static final int MAIN_ACTIVITY_DRAWER_ID = 1;
    public static final int KEYWORDS_MANAGEMENT_DRAWER_ID = 2;

    public static Drawer getDrawer(final Activity activity, Toolbar toolbar) {

        ImageHolder backgroundHeaderHolder = new ImageHolder(R.drawable.abstract_black_yellow_lines);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(backgroundHeaderHolder)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem drawerEmptyItem = new PrimaryDrawerItem().withIdentifier(0).withName("");
        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem drawerItemHome = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.home).withIcon(R.drawable.ic_home);
        PrimaryDrawerItem drawerItemManageKeywords = new PrimaryDrawerItem().withIdentifier(2)
                .withName(R.string.startKeywordsManagementActivity).withIcon(R.drawable.ic_add_keywords);


        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(3)
                .withName(R.string.settings).withIcon(R.drawable.ic_settings);
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(4)
                .withName(R.string.about).withIcon(R.drawable.ic_info);
        SecondaryDrawerItem drawerItemHelp = new SecondaryDrawerItem().withIdentifier(5)
                .withName(R.string.help).withIcon(R.drawable.ic_help);
        SecondaryDrawerItem drawerItemDonate = new SecondaryDrawerItem().withIdentifier(6)
                .withName(R.string.donate).withIcon(R.drawable.ic_donate);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        drawerItemHome,
                        drawerItemManageKeywords,
                        new DividerDrawerItem(),
                        drawerItemHelp,
                        drawerItemSettings,
                        drawerItemDonate,
                        drawerItemAbout
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 2 && !(activity instanceof keywordManagementActivity)) {
                            Intent move = new Intent(activity, keywordManagementActivity.class);
                            view.getContext().startActivity(move);
                        }
                        else if (drawerItem.getIdentifier() == 1 && !(activity instanceof MainActivity)) {
                            Intent move = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(move);
                        }
                        return true;
                    }
                })
                .build();

        return result;
    }
}
