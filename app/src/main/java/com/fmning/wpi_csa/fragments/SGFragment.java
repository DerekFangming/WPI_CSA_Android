package com.fmning.wpi_csa.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.MenuListAdapter;
import com.fmning.wpi_csa.adapters.SGListAdapter;
import com.fmning.wpi_csa.cache.Database;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.objects.Article;
import com.fmning.wpi_csa.objects.Menu;
import com.pixplicity.htmlcompat.HtmlCompat;

import java.util.ArrayList;
import java.util.List;


public class SGFragment extends Fragment {

    private List<Menu> menuList = new ArrayList<>();

    public SGFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final DrawerLayout view = (DrawerLayout)inflater.inflate(R.layout.fragment_sg, container, false);
        //view.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        RecyclerView tableView = (RecyclerView) view.findViewById(R.id.SGList);
        final SGListAdapter sgListAdapter = new SGListAdapter(getActivity(), new SGListAdapter.SGListListener() {
            @Override
            public void OnPrevArticleClicked() {

            }

            @Override
            public void OnNextArticleClicked() {

            }
        });
        tableView.setAdapter(sgListAdapter);



        RecyclerView menuView = (RecyclerView) view.findViewById(R.id.SGMenu);
        final MenuListAdapter menuListAdapter = new MenuListAdapter(getActivity(), new MenuListAdapter.SGMenuListListener() {
            @Override
            public void OnOpenArticle(int menuId) {
                Utils.logLong(Integer.toString(menuId));
                view.closeDrawer(Gravity.START);
            }
        });
        menuView.setAdapter(menuListAdapter);



        double width = getResources().getDisplayMetrics().widthPixels * 0.8;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menuView.getLayoutParams();
        params.width = (int)width;

        final ImageView coverImage = (ImageView) view.findViewById(R.id.SGCoverImage);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coverImage.setVisibility(View.GONE);
                Database db = new Database(getActivity());
                db.open();
                //Spanned fromHtml = HtmlCompat.fromHtml(getActivity(), "<font size=\"40px\" color=\"00FF00\">kjdshfsdj</font>", 0);
                final Article article = new Article("<div color=\"468499\"><br><br><br><h1><big><big><font color=\"#FFFFFF\">写在前面的话</font></big></big></h1></div>&emsp;&emsp;Hello 亲爱的学弟学妹们！<br><br><p align=\"right\">Cyan 谢珊珊 2018 ECE </p>");//db.getArticle(5);
                db.close();

                sgListAdapter.setAndProcessArticle(article);
                sgListAdapter.notifyDataSetChanged();

            }
        });



        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Database db = new Database(getActivity());
                db.open();
                menuList = db.getSubMenus(0, "");
                db.close();

                menuListAdapter.setMenuList(menuList);
                menuListAdapter.notifyDataSetChanged();
            }

        });

        return view;
    }

    private class LoadArticleTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
//
//            this.sgListAdapter.setAndProcessArticle(article);
//            sgListAdapter.notifyDataSetChanged();
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            //Log.d(TAG + " onPostExecute", "" + result);
        }
    }

}
