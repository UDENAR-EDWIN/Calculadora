package com.example.edwin.calculadora;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class historial extends AppCompatActivity {
    TextView histo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        String[] archivos = fileList();
        histo = (TextView) findViewById(R.id.txt_his);

        if (existe(archivos, "operaciones.txt"))
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("operaciones.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String todo = "";
                while (linea != null) {
                    todo = todo + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                histo.setText(todo);
            } catch (IOException e) {
            }
    }
    private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }
    public void retornar(View v){
        finish();
    }
}
