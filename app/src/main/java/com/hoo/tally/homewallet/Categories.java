package com.hoo.tally.homewallet;

public class Categories {

    private String _categoryName;

    public Categories(){

    }

    public Categories(String categoryName) {
        this._categoryName = categoryName;
    }

    public void set_categoryName(String _categoryName) {
        this._categoryName = _categoryName;
    }

    public String get_categoryName() {
        return _categoryName;
    }
}
