package com.hoo.tally.homewallet;

public class Expenses {

    private int _id;
    private String _category;
    private String _quantity;
    private String _date;

    public Expenses(){
    }

    public Expenses (String quantity){
        this._quantity = quantity;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_category(String _category) {
        this._category = _category;
    }

    public void set_quantity(String _quantity) {
        this._quantity = _quantity;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public int get_id() {
        return _id;
    }

    public String get_category() {
        return _category;
    }

    public String get_quantity() {
        return _quantity;
    }

    public String get_date() {
        return _date;
    }
}
