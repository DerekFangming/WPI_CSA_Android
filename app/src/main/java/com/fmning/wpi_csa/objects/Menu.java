package com.fmning.wpi_csa.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangmingning
 * On 12/12/17.
 */

public class Menu {
    public int id;
    public String name;
    public boolean isOpened;
    public boolean isParentMenu;
    public List<Menu> subMenus;

    public Menu(int id, String name){
        this.id = id;
        this.name = name;
        this.isOpened = false;
        this.isParentMenu = false;
        this.subMenus = new ArrayList<>();
    }

}
