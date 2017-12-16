package com.fmning.wpi_csa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmning.wpi_csa.R;
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
    private int visibleCellCount = 0;

    public MenuListAdapter(Context context, SGMenuListListener listener) {
        this.context = context;
        this.listener = listener;
        menuList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sg_menu, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        View cell = holder.itemView;
        Menu menu = getSelectedMenu(menuList, position);
        if (menu != null) {
            ((TextView)cell.findViewById(R.id.SGMenuTitle)).setText(menu.name);

            if (menu.isParentMenu) {
                if (menu.isOpened) {
                    ((ImageView)cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(context.getDrawable(R.drawable.menu_expanded));
                } else {
                    ((ImageView)cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(context.getDrawable(R.drawable.menu_collapsed));
                }
            } else {
                ((ImageView)cell.findViewById(R.id.SGMenuStatus)).setImageDrawable(null);
            }
        }

        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelectedMenu(menuList, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return visibleCellCount;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
        visibleCellCount = calculateVisibleCellNumber(menuList);
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
                    visibleCellCount = calculateVisibleCellNumber(this.menuList);
                    notifyDataSetChanged();

                } else {
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

    public interface SGMenuListListener {
        void OnOpenArticle(int menuId);
    }
}
