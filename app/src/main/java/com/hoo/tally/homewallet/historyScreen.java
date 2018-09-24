package com.hoo.tally.homewallet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class historyScreen extends AppCompatActivity {

    //Used for asking request to user about writting permissions
    private int REQUEST_CODE = 1;

    MyHistoryDBHandler historyDb;
    SwipeMenuListView listView;

    //Create empty array that will be used for the item list.
    List<HashMap<String,String>> listItems = new ArrayList<>();

    //This adapter will be used to reflect the changes in the item array
    //into the list view.
    private SimpleAdapter theAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);

        //Retrieve UI elements
        listView=findViewById(R.id.historyView);

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
                        historyDb.deleteExpense(expenseId);

                        //Refresh list
                        historyDb.databaseToArray(listItems);

                        //Reflect array state into list view.
                        theAdapter.notifyDataSetChanged();

                        return true;

                }
                // false : close the menu; true : not close the menu
                return false;
            }

        });

        //Set database class
        historyDb = new MyHistoryDBHandler(this,null,null,1);
        historyDb.databaseToArray(listItems);
        theAdapter.notifyDataSetChanged();
    }



    //Create Top Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history,menu);
        return super.onCreateOptionsMenu(menu);
    }



    //Actions for toolbar buttons.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Menu Button "return".
        if(item.getItemId()==R.id.returnBtn){
            Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(startIntent);

        }else if(item.getItemId()==R.id.exportCVS){
            checkForPermission();

        }
        return super.onOptionsItemSelected(item);
    }

    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(historyScreen.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "/codesss/");
            if (!exportDir.exists()) { exportDir.mkdirs(); }

            File file = new File(exportDir, "history.csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = historyDb.raw();
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    String arrStr[]=null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for(int i=0;i<curCSV.getColumnNames().length;i++)
                    {
                        mySecondStringArray[i] =curCSV.getString(i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();
                curCSV.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(historyScreen.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(historyScreen.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkForPermission(){
        //checking wether the permission is already granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

        // permission is already granted
            exportCSVfile();
        }else{

        //persmission is not granted yet
        //Asking for permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE){

        //checking if the permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


        //permission is granted,do your operation
                exportCSVfile();
            }else{

        // permission not granted
        //Display your message to let the user know that he requires permission to access the app
                Toast.makeText(this,"You denied the permission",Toast.LENGTH_LONG).show();

            }
        }
    }
    private void ShareFile() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/codesss/");
        String fileName = "history.csv";
        File sharingGifFile = new File(exportDir, fileName);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("application/csv");
        Uri uri = Uri.fromFile(sharingGifFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share CSV"));
    }

    private void exportCSVfile(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ExportDatabaseCSVTask().execute();
        }
        ShareFile();
    }

}
