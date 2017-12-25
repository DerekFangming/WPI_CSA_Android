package com.fmning.wpi_csa.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.adapters.MenuListAdapter;
import com.fmning.wpi_csa.adapters.SGListAdapter;
import com.fmning.wpi_csa.cache.Database;
import com.fmning.wpi_csa.helpers.CustomDrawer;
import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.objects.Article;
import com.fmning.wpi_csa.objects.Menu;

import java.util.ArrayList;
import java.util.List;


public class SGFragment extends Fragment {

    private List<Menu> menuList = new ArrayList<>();
    private boolean isShowingNavBar = true;
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    public SGFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final CustomDrawer drawer = (CustomDrawer)inflater.inflate(R.layout.fragment_sg, container, false);

        final View navigationBar = drawer.findViewById(R.id.SGNavigationBar);
        final RecyclerView tableView = (RecyclerView) drawer.findViewById(R.id.SGList);
        final ImageView coverImage = (ImageView) drawer.findViewById(R.id.SGCoverImage);

        final Button menuButton = (Button) drawer.findViewById(R.id.SGMenuButton);
        final Button reportButton = (Button) drawer.findViewById(R.id.SGReportButton);

        final SGListAdapter sgListAdapter = new SGListAdapter(getActivity());
        sgListAdapter.setListener(new SGListAdapter.SGListListener() {
            @Override
            public void OnPrevArticleShown(int color) {
                tableView.scrollToPosition(0);
                if (color != -1) {
                    navigationBar.setBackgroundColor(color);
                }
            }

            @Override
            public void OnNextArticleShown(int color) {
                tableView.scrollToPosition(0);
                if (color != -1) {
                    navigationBar.setBackgroundColor(color);
                }
            }
        });
        tableView.setAdapter(sgListAdapter);


        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coverImage.setVisibility(View.GONE);
                Database db = new Database(getActivity());
                db.open();
                Article article = db.getArticle(1);
                db.close();

                sgListAdapter.setAndProcessArticle(article);
                sgListAdapter.notifyDataSetChanged();

                if (article.themeColor != -1) {
                    navigationBar.setBackgroundColor(article.themeColor);
                }

            }
        });

        RecyclerView menuView = (RecyclerView) drawer.findViewById(R.id.SGMenu);
        final MenuListAdapter menuListAdapter = new MenuListAdapter(getActivity(), new MenuListAdapter.SGMenuListListener() {
            @Override
            public void OnOpenArticle(int menuId) {
                if (coverImage.getVisibility() == View.VISIBLE) {
                    coverImage.setVisibility(View.GONE);
                }
                drawer.closeDrawer(Gravity.START);
                Database db = new Database(getActivity());
                db.open();
                Article article = db.getArticle(menuId);
                db.close();

                sgListAdapter.setAndProcessArticle(article);
                sgListAdapter.notifyDataSetChanged();

                if (article.themeColor != -1) {
                    navigationBar.setBackgroundColor(article.themeColor);
                }
            }
        });
        menuView.setAdapter(menuListAdapter);

        double width = Utils.paddingFullWidth * 0.8;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menuView.getLayoutParams();
        params.width = (int)width;


        tableView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (isShowingNavBar) {
                        isShowingNavBar = false;
                        navigationBar.animate().y(-Utils.padding72);
                    }
                } else {
                    if (!isShowingNavBar) {
                        isShowingNavBar = true;
                        navigationBar.animate().y(0);
                    }
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                drawer.openDrawer(Gravity.START);
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
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        menuListAdapter.notifyDataSetChanged();
                    }
                });
            }

        });

        return drawer;
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
