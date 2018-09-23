package com.hoo.tally.homewallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class categoryScreen extends AppCompatActivity {

    Button addBtn;
    private Toolbar toolbar;
    SwipeMenuListView catList;
    MyCatDBHandler catdbHandler;
    EditText catInput;

    //Create empty array that will be used for the item list.
    List<HashMap<String,String>> listItems = new ArrayList<>();

    //This adapter will be used to reflect the changes in the item array
    //into the list view.
    private SimpleAdapter theAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_screen);


        //Save UI elements into variables
        addBtn = findViewById(R.id.addCategoryBtn);
        catList = findViewById(R.id.categoryListView);
        catInput = findViewById(R.id.CategoryInput);


        //Create adapter to reflect array in the list view
        theAdapter = new SimpleAdapter(this, listItems, R.layout.cat_list_item,
                new String[]{"ID Line","Category Line"},
                new int[]{R.id.textField_id,R.id.textField_category});


        //Apply adapter to list view
        catList.setCloseInterpolator(new BounceInterpolator());
        catList.setAdapter(theAdapter);


        //Create list view swipe delete Button
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());

                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));

                // set item width
                deleteItem.setWidth(200);


                // set a icon
                deleteItem.setIcon(R.drawable.ic_remove);

                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        //Set button into list view
        catList.setMenuCreator(creator);


        //Insert button.
        //Gets current date, inserted quantity and domain and
        //creates a list item with this information.
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //Get category from editText
                String newCategory = catInput.getText().toString();

                if(!newCategory.isEmpty()) {

                    //Add a expense to the database
                    Categories thecategory = new Categories(newCategory);
                    catdbHandler.addCategory(thecategory);
                    catdbHandler.databaseToArrayIncludingID(listItems);

                    //Clear input Edittext
                    catInput.setText("");


                    //Apply changes in data base to listview
                    theAdapter.notifyDataSetChanged();

                    //Scroll list view to show list item
                    catList.setSelection(theAdapter.getCount()-1);

                    //Call function to close soft keyboard
                    closeKeyboard();

                }
            }

        });

        //Delete button
        //Removes selected item from list view
        //and extracts removed quantity from final result.
        catList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:


                        //Get id from selected item.
                        String categoryId = listItems.get(position).get("ID Line");

                        //Remove item from database
                        catdbHandler.deleteCategory(categoryId);

                        //Update listItems array with new database info
                        catdbHandler.databaseToArrayIncludingID(listItems);

                        //Reflect array state into list view.
                        theAdapter.notifyDataSetChanged();

                        return true;

                }
                // false : close the menu; true : not close the menu
                return false;
            }

        });

        //Set database class
        catdbHandler = new MyCatDBHandler(this,null,null,1);
        catdbHandler.databaseToArrayIncludingID(listItems);
        theAdapter.notifyDataSetChanged();
    }


    //Create Top Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categories,menu);
        return super.onCreateOptionsMenu(menu);
    }


    //Actions for toolbar buttons.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Menu Button "return".
        if(item.getItemId()==R.id.returnBtn){
            Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(startIntent);

        }else if(item.getItemId()==R.id.clearAll){

            //Clear database table.
            catdbHandler.clearTable();

            //Update listItems array with new database info
            catdbHandler.databaseToArrayIncludingID(listItems);

            theAdapter.notifyDataSetChanged();
            catInput.setText("");
            closeKeyboard();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    //Code to hide soft keyboard
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


