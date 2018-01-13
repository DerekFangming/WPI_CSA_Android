package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
import com.fmning.wpi_csa.cache.Database;
import com.fmning.wpi_csa.objects.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangmingning
 * On 12/12/17.
 */

public class MenuListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private SGMenuListListener listener;
    private List<Menu> menuList;
    private List<Menu> searchResults;
    private int visibleCellCount = 1;

    private String previousText = "";
    private String currentText = "";

    private EditText searchBar;

    public MenuListAdapter(Context context, SGMenuListListener listener) {
        this.context = context;
        this.listener = listener;
        menuList = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_search_bar, parent, false);
                return new ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_menu, parent, false);
                return new ViewHolder(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        View cell = holder.itemView;
        if (position == 0) {
            searchBar = (EditText)cell.findViewById(R.id.SGSearchBar);
            searchBar.requestFocus();
            searchBar.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentText = s.toString();
                    if (!currentText.equals(previousText)) {
                        previousText = currentText;

                        if (currentText.equals("")) {
                            notifyDataSetChanged();
                        } else {
                            searchForArticles(currentText);
                        }
                    }
                }
            });
        } else {

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentText.equals("")) {
                        toggleSelectedMenu(menuList, holder.getLayoutPosition() - 1);
                    } else {
                        toggleSelectedMenu(searchResults, holder.getLayoutPosition() - 1);
                    }
                }
            });

            if (!currentText.equals("")) {
                ((TextView) cell.findViewById(R.id.SGMenuTitle)).setText(searchResults.get(position - 1).name);
                ((ImageView) cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(null);
                return;
            }

            Menu menu = getSelectedMenu(menuList, position - 1);
            if (menu != null) {
                ((TextView) cell.findViewById(R.id.SGMenuTitle)).setText(menu.name);

                if (menu.isParentMenu) {
                    if (menu.isOpened) {
                        ((ImageView) cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(context.getDrawable(R.drawable.menu_expanded));
                    } else {
                        ((ImageView) cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(context.getDrawable(R.drawable.menu_collapsed));
                    }
                } else {
                    ((ImageView) cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(null);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (!currentText.equals("")) {
            return searchResults.size() + 1;
        }
        return visibleCellCount;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
        visibleCellCount = calculateVisibleCellNumber(menuList) + 1;
    }

    private int calculateVisibleCellNumber(List<Menu> menuList) {
        int count = menuList.size();
        for (Menu m : menuList) {
            if (m.isOpened) {
                count += calculateVisibleCellNumber(m.subMenus);
            }
        }
        return count;
    }

    private Menu getSelectedMenu(List<Menu> menuList, int index) {
        int counter = 0;
        for (Menu m : menuList) {
            counter ++;
            if (counter - 1 == index) {
                return m;
            } else if (m.isOpened) {
                int subMenusCount = calculateVisibleCellNumber(m.subMenus);
                if (counter + subMenusCount - 1 >= index) {
                    return getSelectedMenu(m.subMenus, index - counter);
                } else {
                    counter += subMenusCount;
                }
            }
        }
        return null;
    }

    private void toggleSelectedMenu(List<Menu> menuList, int index) {
        int counter = 0;

        for (Menu m : menuList) {
            counter ++;
            if (counter - 1 == index) {
                if (m.isParentMenu) {
                    m.isOpened = !m.isOpened;
                    visibleCellCount = calculateVisibleCellNumber(this.menuList) + 1;
                    notifyDataSetChanged();

                } else {
                    if (searchBar != null && searchBar.hasFocus()) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                        }
                    }
                    listener.OnOpenArticle(m.id);
                }
                return;
            } else if (m.isOpened) {
                int subMenusCount = calculateVisibleCellNumber(m.subMenus);
                if (counter + subMenusCount - 1 >= index) {
                    toggleSelectedMenu(m.subMenus, index - counter);
                    return;
                } else {
                    counter += subMenusCount;
                }
            }
        }
    }

    private void searchForArticles(String keyword) {
        Database db = new Database(context);
        db.open();
        searchResults = db.searchArticles(keyword);
        db.close();

        notifyDataSetChanged();
    }

    public interface SGMenuListListener {
        void OnOpenArticle(int menuId);
    }
}
