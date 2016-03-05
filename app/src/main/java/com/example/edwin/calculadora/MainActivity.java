package com.example.edwin.calculadora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    TextView res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res =(TextView) findViewById(R.id.txt_res);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.acerca) {
            Intent  i = new Intent(this, Informacion.class);
            startActivity(i);
        }
        if (id == R.id.historial) {
            Intent  i = new Intent(this, historial.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    public void concatenar(View v){
        String op = v.getTag().toString();
        if(op.equals("ret")) res.setText("");
        res.setText(res.getText() + v.getTag().toString());
    }
    public void grabar(View v){
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "operaciones.txt", Activity.MODE_APPEND));
            archivo.write("\n"+res.getText().toString());
            archivo.flush();
            archivo.close();
        }
        catch (IOException e) {
        }
    }
}
