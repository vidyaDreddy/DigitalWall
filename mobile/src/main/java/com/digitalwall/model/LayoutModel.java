package com.digitalwall.model;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 */

public class LayoutModel {

    private int layoutId;
    private String layoutName;
    private int layoutHeight;
    private int layoutWidth;
    private int layoutXSize;
    private int layoutYSize;
    private String  layoutColor;
    private ArrayList<TileModel> aList;


    public LayoutModel(int layoutId, String layoutName, int layoutHeight, int layoutWidth,
                       int layoutXSize, int layoutYSize,String layoutColor,ArrayList<TileModel> aList) {
        this.layoutId = layoutId;
        this.layoutName = layoutName;
        this.layoutHeight = layoutHeight;
        this.layoutWidth = layoutWidth;
        this.layoutXSize = layoutXSize;
        this.layoutYSize = layoutYSize;
        this.layoutColor=layoutColor;
        this.aList=aList;
    }

    public ArrayList<TileModel> getaList() {
        return aList;
    }

    public void setaList(ArrayList<TileModel> aList) {
        this.aList = aList;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public int getLayoutHeight() {
        return layoutHeight;
    }

    public void setLayoutHeight(int layoutHeight) {
        this.layoutHeight = layoutHeight;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public void setLayoutWidth(int layoutWidth) {
        this.layoutWidth = layoutWidth;
    }

    public int getLayoutXSize() {
        return layoutXSize;
    }

    public void setLayoutXSize(int layoutXSize) {
        this.layoutXSize = layoutXSize;
    }

    public int getLayoutYSize() {
        return layoutYSize;
    }

    public void setLayoutYSize(int layoutYSize) {
        this.layoutYSize = layoutYSize;
    }

    public String getLayoutColor() {
        return layoutColor;
    }

    public void setLayoutColor(String layoutColor) {
        this.layoutColor = layoutColor;
    }
}
