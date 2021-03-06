package com.fmning.wpi_csa.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.activities.ReportActivity;
import com.fmning.wpi_csa.adapters.MenuListAdapter;
import com.fmning.wpi_csa.adapters.SGListAdapter;
import com.fmning.wpi_csa.cache.Database;
import com.fmning.wpi_csa.helpers.AppMode;
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
    private int currentMenuId = 0;

    private OnSGListener listener;

    public SGFragment(){}

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
            public void OnPrevArticleShown(int color, int menuId) {
                tableView.scrollToPosition(0);
                currentMenuId = menuId;
                if (color != -1) {
                    navigationBar.setBackgroundColor(color);
                }
            }

            @Override
            public void OnNextArticleShown(int color, int menuId) {
                tableView.scrollToPosition(0);
                currentMenuId = menuId;
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

                currentMenuId = article.menuId;
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

                currentMenuId = article.menuId;
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

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (Utils.appMode == AppMode.LOGGED_ON) {
                    Intent intent = new Intent(getActivity(), ReportActivity.class);
                    intent.putExtra("menuId", currentMenuId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false).setTitle(null)
                            .setMessage(getActivity().getString(R.string.report_not_loggedin))
                            .setPositiveButton(getActivity().getString(R.string.login_register), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (listener != null) {
                                        listener.OnGotoSettingPage();
                                    }
                                }
                            })
                            .setNeutralButton(getActivity().getString(R.string.cancel), null)
                            .setNegativeButton(getActivity().getString(R.string.report_anyway), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getActivity(), ReportActivity.class);
                                    intent.putExtra("menuId", currentMenuId);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                }
                            }).show();
                }
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

    public void setOnSGListener (OnSGListener listener) {
        this.listener = listener;
    }

    public interface OnSGListener {
        void OnGotoSettingPage();
    }
}
