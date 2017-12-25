package com.fmning.wpi_csa.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public SGFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final CustomDrawer view = (CustomDrawer)inflater.inflate(R.layout.fragment_sg, container, false);

        final RecyclerView tableView = (RecyclerView) view.findViewById(R.id.SGList);
        final SGListAdapter sgListAdapter = new SGListAdapter(getActivity());
        sgListAdapter.setListener(new SGListAdapter.SGListListener() {
            @Override
            public void OnPrevArticleShown() {
                tableView.scrollToPosition(0);
            }

            @Override
            public void OnNextArticleShown() {
                tableView.scrollToPosition(0);
            }
        });
        tableView.setAdapter(sgListAdapter);


        final ImageView coverImage = (ImageView) view.findViewById(R.id.SGCoverImage);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coverImage.setVisibility(View.GONE);
                Database db = new Database(getActivity());
                db.open();
                Article article = db.getArticle(1);
                //article = new Article("更多详情请见http://www.wpi.edu/academics/imgd.html，上面有详细的课程介绍和每年的选课流程，感兴趣的同学可以去看看更多详情请见http://www.wpi.edu/academics/imgd.html， 上面有详细的课程介绍和每年的选课流程，感兴趣的同学可以去看看");
                db.close();

                sgListAdapter.setAndProcessArticle(article);
                sgListAdapter.notifyDataSetChanged();

            }
        });

        RecyclerView menuView = (RecyclerView) view.findViewById(R.id.SGMenu);
        final MenuListAdapter menuListAdapter = new MenuListAdapter(getActivity(), new MenuListAdapter.SGMenuListListener() {
            @Override
            public void OnOpenArticle(int menuId) {
                if (coverImage.getVisibility() == View.VISIBLE) {
                    coverImage.setVisibility(View.GONE);
                }
                view.closeDrawer(Gravity.START);
                Database db = new Database(getActivity());
                db.open();
                Article article = db.getArticle(menuId);
                db.close();

                sgListAdapter.setAndProcessArticle(article);
                sgListAdapter.notifyDataSetChanged();
            }
        });
        menuView.setAdapter(menuListAdapter);

        double width = Utils.paddingFullWidth * 0.8;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menuView.getLayoutParams();
        params.width = (int)width;


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
