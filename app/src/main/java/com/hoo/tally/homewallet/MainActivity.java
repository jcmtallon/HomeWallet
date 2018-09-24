package com.hoo.tally.homewallet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements categoryDialog.myDialogListener{

    //Database classes
    MyDBHandler dbHandler;
    MyCatDBHandler catdbHandler;
    MyHistoryDBHandler historyDB;

    //Used for saving logs in the console
    private static final String TAG = "MainActivity";

    //Declare UI elements
    TextView resultTextView;
    SwipeMenuListView listView;
    Button insertBtn;
    EditText quantityEditText;
    Spinner categorySpinner;

    //Create empty array that will be used for the item list.
    List<HashMap<String,String>> listItems = new ArrayList<>();

    //ArrayList used to load the spinner.
    List<String> categoryList = new ArrayList<>();

    //This adapter will be used to reflect the changes in the item array
    //into the list view.
    private SimpleAdapter theAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Retrieve UI elements from activity
        resultTextView = findViewById(R.id.textView4);
        listView = findViewById(R.id.listView);
        quantityEditText =  findViewById(R.id.editText);
        categorySpinner = findViewById(R.id.spinner1);
        insertBtn = findViewById(R.id.button);


        //Create adapter to reflect array in the list view
        theAdapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line","Second Line","Third Line","Forth Line"},
                new int[]{R.id.text1,R.id.text2,R.id.text3,R.id.text4});



        //Apply adapter to list view
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setAdapter(theAdapter);


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
        listView.setMenuCreator(creator);

        //Click and hold category spinner.
        categorySpinner.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                Log.i(TAG,"Worked");
                openDialog();
                return true;
            }
        });

        //When clicking a list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Toast.makeText(MainActivity.this,listItems.get(position).get("Second Line"),Toast.LENGTH_SHORT).show();     //HERE!!!!!!!!!!!!!!!!!!!!!!!11
            }
        });



        //Insert button.
        //Gets current date, inserted quantity and domain and
        //creates a list item with this information.
        insertBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //Get quantity and current result values to make calculation
                String quantity = quantityEditText.getText().toString();
                String result = resultTextView.getText().toString();
                int seletedItemOfMySpinner=categorySpinner.getSelectedItemPosition();
                String actualPositionOfMySpinner = (String) categorySpinner.getItemAtPosition(seletedItemOfMySpinner);

                if(quantity.isEmpty()){
                    Toast.makeText( MainActivity.this,"Insert a quantity first",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(seletedItemOfMySpinner==-1){
                    Toast.makeText( MainActivity.this,"Select a category first",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!quantity.isEmpty() && seletedItemOfMySpinner!=-1) {

                    //Get current date and save in currentDate string
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT).format(calendar.getTime());

                    //Add a expense to the database
                    Expenses expense = new Expenses(quantity);
                    expense.set_category(categorySpinner.getSelectedItem().toString());
                    expense.set_date(currentDate);
                    dbHandler.addExpense(expense);
                    int finalResult = dbHandler.databaseToArray(listItems);

                    //Reflect sum result into the result textview in the activity layout
                    //and clear the quantity field
                    resultTextView.setText(("" + finalResult));
                    quantityEditText.setText("");

                    //Apply changes in data base to listview
                    theAdapter.notifyDataSetChanged();

                    //Scroll list view to show list item
                    listView.setSelection(theAdapter.getCount()-1);

                    //Call function to close soft keyboard
                    closeKeyboard();

                }
            }

        });


        //Delete button
        //Removes selected item from list view
        //and extracts removed quantity from final result.
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:


                        //Get id from selected item.
                        String expenseId = listItems.get(position).get("Forth Line");

                        //Remove item from database
                        dbHandler.deleteExpense(expenseId);

                        //Update listItems array with new database info
                        int finalResult = dbHandler.databaseToArray(listItems);

                        //Reflect array state into list view.
                        theAdapter.notifyDataSetChanged();

                        //Reflect new final result in UI
                        resultTextView.setText(("" + finalResult));
                        return true;

                }
                // false : close the menu; true : not close the menu
                return false;
            }

        });

        //Set category database
        catdbHandler = new MyCatDBHandler(this,null,null,1);

        //Set history database
        historyDB = new MyHistoryDBHandler(this,null,null,1);

        //Push category db data into categoryList array.
        catdbHandler.databaseToArray(categoryList);

        //Spinner (drop down list) code.
        //Get category list from strings.xml
        ArrayAdapter<String> myCategoryAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,categoryList);
                myCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(myCategoryAdapter);


        //Set database class
        dbHandler = new MyDBHandler(this,null,null,1);
        int finalResult = dbHandler.databaseToArray(listItems);
        resultTextView.setText(("" + finalResult));
        theAdapter.notifyDataSetChanged();

    }


    //Create Top Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //MENU BUTTONS.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Menu BUtton "clear all".
        if(item.getItemId()==R.id.clearAll){

            //Migrate data to history database
            dbHandler.migrateToHistory(historyDB);


            //Clear database table.
            dbHandler.clearTable();

            //Update listItems array with new database info
            int finalResult = dbHandler.databaseToArray(listItems);

            theAdapter.notifyDataSetChanged();
            resultTextView.setText(("" + finalResult));
            quantityEditText.setText("");
            closeKeyboard();

        //Menu Button "clear categories".
        }else if(item.getItemId()==R.id.clearCats){
            catdbHandler.clearTable();
            //catdbHandler.databaseToArray(categoryList);
            categorySpinner.setAdapter(null);

        }else if(item.getItemId()==R.id.goToCats){
            Intent startIntent = new Intent(getApplicationContext(), categoryScreen.class);
            //startIntent.putExtra("test", "HELLO WORLD!");
            startActivity(startIntent);

        }else if(item.getItemId()==R.id.goToHistory){
            Intent goToHistoryIntent = new Intent(getApplicationContext(), historyScreen.class);
            startActivity(goToHistoryIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    //Code to hide soft keyboard
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Code to hide soft keyboard
    public void showKeyboard(){
        EditText categoryEditText =findViewById(R.id.edit_category);
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(categoryEditText,InputMethodManager.SHOW_IMPLICIT);
        }
    }


    //Code to display the category popup
    public void openDialog(){
        categoryDialog catDialog = new categoryDialog();
        catDialog.show(getSupportFragmentManager(), "example dialog");

    }

    @Override
    public void applyCategory(String username) {
        Categories category = new Categories(username);
        catdbHandler.addCategory(category);
        catdbHandler.databaseToArray(categoryList);

        ArrayAdapter<String> myCategoryAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,categoryList);
        myCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(myCategoryAdapter);

        categorySpinner.setSelection(myCategoryAdapter.getCount()-1);

    }
}
