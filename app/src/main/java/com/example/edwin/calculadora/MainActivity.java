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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    TextView res;
    char [] opera = {'+','-','x','/'};
    char [] number = {'0','1','2','3','4','5','6','7','8','9'};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res =(TextView) findViewById(R.id.txt_res);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    //----------------------------------------------------------------------------------------------
    //CONCATENAR LO CARACTERES INGRESADOS
    public void concatenar(View v){
        String op = v.getTag().toString();
        String resact = res.getText().toString();
        char c2 = op.charAt(op.length()-1);

        if(!resact.isEmpty()){
            char c1 = resact.charAt(resact.length() - 1);
            if((isnumber(c1) || c1=='(' || c1==')') && (isnumber(c2) || (c2=='(' || c2==')')))res.setText(res.getText() + v.getTag().toString());
            else if ((isopera(c1) && isnumber(c2)) || (isnumber(c1) && isopera(c2))) res.setText(res.getText() + v.getTag().toString());
        }
        else {
            if(!isopera(c2)) res.setText(res.getText() + v.getTag().toString());
        }
    }
    //----------------------------------------------------------------------------------------------
    //GRABAR EN UN HISTORIAL TODAS LAS OPERACIONES INGRESADAS
    public void grabar(View v) {
        if(validparent(res.getText().toString())) {
            try {
                OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                        "operaciones.txt", Activity.MODE_APPEND));
                archivo.write("\n" + res.getText().toString());
                archivo.flush();
                archivo.close();
            } catch (IOException e) {}
            calcular(res.getText().toString());
        }
    }
    //----------------------------------------------------------------------------------------------
    //BORRAR TOD O SOLO UN CARACTER
    public void borrar(View v){
        String ope = v.getTag().toString();
        if(ope.equals("ret")){
            String resact = res.getText().toString();
            String cadres = "";
            for(int i=0; i<resact.length()-1; i++) cadres += resact.charAt(i);
            res.setText(cadres);
        }
        else res.setText("");
    }
    //----------------------------------------------------------------------------------------------
    //COMPROBAR SI ES UNA OPERACION O NO
    public boolean isopera(char car){
        for (int i=0; i<opera.length; i++)if(opera[i] == car) return true;
        return false;
    }
    //----------------------------------------------------------------------------------------------
    //COMPROBAR SI ES UNA NUMERO O NO
    public boolean isnumber(char cad){
        for (int i=0; i<number.length; i++)if(number[i] == cad) return true;
        return false;
    }
    //----------------------------------------------------------------------------------------------
    //COMPROBAR SI ES UNA OPERACION O NO
    public boolean validparent(String cad){
        int p1=0;
        int p2=0;
        for (int i=0; i<cad.length(); i++){
            if(cad.charAt(i) == '(') p1+=1;
            if(cad.charAt(i) == ')') p2+=1;
        }
        if(p1 == p2) return true;
        else return false;
    }
    //----------------------------------------------------------------------------------------------
    //CALCULAR LA CADENA INGRESADA
    public void calcular(String cad){
        Stack<Character> operadores = new Stack<>();
        List<String> cadres = new ArrayList<String>();
        List<String> aux = new ArrayList<String>();
        char c1,c2;
        for(int i=0; i<cad.length(); i++){
            c1=cad.charAt(i);
            if(isopera(c1) || c1 == '(' || c1 == ')'){
                if(!operadores.isEmpty()){
                    c2=operadores.peek();
                    if(c1=='(' || c2=='(') operadores.push(c1);
                    else if(c1 == ')'){
                        cadres = vaciar_pila(cadres,operadores);
                        operadores.clear();
                    }
                    else if(puntaje(c1) == puntaje(c2)){
                        cadres.add(c2+"");
                        operadores.pop();
                        operadores.push(c1);
                    }
                    else if(puntaje(c1) > puntaje(c2)) operadores.push(c1);
                    else if(puntaje(c1) < puntaje(c2)){
                        cadres = vaciar_pila(cadres,operadores);
                        operadores.clear();
                        operadores.push(c1);
                    }
                }
                else operadores.push(c1);
            }
            else cadres.add(c1+"");
        }

        aux = vaciar_pila(cadres,operadores);
        res.setText("");
        for(int i=0; i<cadres.size(); i++){
            res.setText(aux.get(i)+"\n");
        }
    }
    //----------------------------------------------------------------------------------------------
    //VACIAR LA PILA EN LA LISTA
    public List<String> vaciar_pila(List<String> lista, Stack<Character> pila){
        int tam = pila.size();
        for(int i=0; i<tam; i++){
            lista.add(pila.peek()+"");
            pila.pop();
        }
        return lista;
    }
    //----------------------------------------------------------------------------------------------
    //CALCULAR LA CADENA INGRESADA
    public int puntaje(char simb){
        if(simb == '+' || simb == '-') return 1;
        if(simb == 'x' || simb == '/') return 2;
        return 0;
    }
}
