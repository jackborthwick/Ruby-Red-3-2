package com.example.loafsmac.rubyred;

/**
 * Created by bertolopez-cruz on 2/8/16.
 */
public class GridMenu {

    public String title;

    public int icon;
    public GridMenu(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}