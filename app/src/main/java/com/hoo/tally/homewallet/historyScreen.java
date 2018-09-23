package com.hoo.tally.homewallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class historyScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);
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
            Toast.makeText(historyScreen.this,"En desarrollo",Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);
    }


}
