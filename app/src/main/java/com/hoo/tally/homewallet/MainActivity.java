package com.hoo.tally.homewallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Used for saving logs in the console
    private static final String TAG = "MainActivity";

    //Declare UI elements
    TextView resultTextView;
    SwipeMenuListView listView;
    Button insertBtn;
    EditText quantityEditText;
    Spinner categorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        //Retrieve UI elements
        resultTextView = (TextView) findViewById(R.id.textView4);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        quantityEditText = (EditText) findViewById(R.id.editText);
        categorySpinner = (Spinner) findViewById(R.id.spinner1);
        insertBtn = (Button) findViewById(R.id.button);



//        final  HashMap<String,String> nameAddresses = new HashMap<>();
//        nameAddresses.put("900","Food   (2018/07/03)");
//        nameAddresses.put("1200","Leisure   (2018/07/03)");
//        nameAddresses.put("1500","Food   (2018/07/03)");
//        nameAddresses.put("12000","Leisure   (2018/07/03)");
//        nameAddresses.put("120","Leisure   (2018/07/03)");
//        nameAddresses.put("1500","Food   (2018/07/03)");
//        nameAddresses.put("7200","Leisure    (2018/07/03)");

        final  List<HashMap<String,String>> listItems = new ArrayList<>();
        final  SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line","Second Line","Third Line"},
                new int[]{R.id.text1,R.id.text2,R.id.text3});

//        Iterator it = nameAddresses.entrySet().iterator();
//        while (it.hasNext())
//        {
//            HashMap<String,String> resultsMap = new HashMap<>();
//            Map.Entry pair = (Map.Entry)it.next();
//            resultsMap.put("First Line", pair.getKey().toString());
//            resultsMap.put("Second Line", pair.getValue().toString());
//            listItems.add(resultsMap);
//        }

//        HashMap<String,String> resultsMap = new HashMap<>();
//            resultsMap.put("First Line", "12000");
//            resultsMap.put("Second Line", "Food");
//            resultsMap.put("Third Line", "2018/07/07");
//            listItems.add(resultsMap);

        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setAdapter(adapter);

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



        // set creator
        listView.setMenuCreator(creator);

        //Insert Button

        insertBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Calendar calendar = Calendar.getInstance();
                String currentDate = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT).format(calendar.getTime());

                String quantity = quantityEditText.getText().toString();
                String result = resultTextView.getText().toString();

                int sumandoUno = Integer.parseInt(quantity);
                int sumandoDos = Integer.parseInt(result);
                int resultadoSuma = sumandoDos + sumandoUno;


                resultTextView.setText((""+resultadoSuma));
                quantityEditText.setText("");

                HashMap<String,String> resultsMap = new HashMap<>();
                resultsMap.put("First Line", quantity);
                resultsMap.put("Second Line", categorySpinner.getSelectedItem().toString());
                resultsMap.put("Third Line", currentDate);
                listItems.add(resultsMap);
                adapter.notifyDataSetChanged();

                closeKeyboard();
            }

        });


        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        String moneyAmount = listItems.get(position).get("First Line");
                        listItems.remove(position);
                        adapter.notifyDataSetChanged();


                        String result = resultTextView.getText().toString();

                        int sumandoUno = Integer.parseInt(moneyAmount);
                        int sumandoDos = Integer.parseInt(result);
                        int resultadoSuma = sumandoDos - sumandoUno;

                        resultTextView.setText((""+resultadoSuma));
                        return true;

                }
                // false : close the menu; true : not close the menu
                return false;
            }




        });


        //Spinner code
        ArrayAdapter<String> myCategoryAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.categories));
        myCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(myCategoryAdapter);

    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
