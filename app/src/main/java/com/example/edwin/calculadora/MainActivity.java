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
import java.util.TreeMap;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    TextView res;
    char [] opera = {'+','-','x','/'};
    char [] number = {'0','1','2','3','4','5','6','7','8','9'};
    Stack<Character> operadores;
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
            String cadcon = res.getText() + v.getTag().toString();
            if(isnumber(c1) && (isnumber(c2) || isopera(c2) || c2==')')) res.setText(cadcon);
            else if(isopera(c1) && (isnumber(c2) || c2=='(')) res.setText(cadcon);
            else if(c1=='(' && (isnumber(c2) || c2=='(' || c2==')')) res.setText(cadcon);
            else if(c1==')' && (isopera(c2) || c2=='(' || c2==')')) res.setText(cadcon);
        }
        else {
            if(isnumber(c2) || c2 == '(') res.setText(res.getText() + v.getTag().toString());
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
        int abiertos=0;
        int cerrados=0;
        boolean ban = false;
        for (int i=0; i<cad.length(); i++){
            if(cad.charAt(i) == '('){
                ban=true;
                abiertos +=1;
            }
            else if(cad.charAt(i) == ')'){
                if(ban==true){
                    abiertos -=1;
                    cerrados +=1;
                    ban = false;
                }
                else abiertos +=1;
            }
        }
        if(abiertos==0) return true;
        else return false;
    }
    //----------------------------------------------------------------------------------------------
    //CALCULAR LA CADENA INGRESADA
    public void calcular(String cad){
        operadores = new Stack<>();
        Stack<String> resultado = new Stack<>();
        List<String> cadres = new ArrayList<String>();
        char c1,c2;
        float n1, n2;
        int tlist;
        String elm1, elm2, elm3, rop;

        for(int i=0; i<cad.length(); i++){
            c1=cad.charAt(i);
            if(isopera(c1) || c1 == '(' || c1 == ')'){
                if(!operadores.isEmpty()){
                    c2=operadores.peek();
                    if(c1=='(' || c2=='(')operadores.push(c1);
                    else if(c1 == ')')cadres = vaciar_pila(cadres);
                    else if(puntaje(c1) == puntaje(c2)){
                        cadres.add(c2+"");
                        operadores.pop();
                        operadores.push(c1);
                    }
                    else if(puntaje(c1) > puntaje(c2)) operadores.push(c1);
                    else if(puntaje(c1) < puntaje(c2)){
                        cadres = vaciar_pila(cadres);
                        operadores.push(c1);
                    }
                }
                else operadores.push(c1);
            }
            else if(!cadres.isEmpty()){
                if(isnumber(cad.charAt(i-1))){
                    String cd1 = cadres.get(cadres.size()-1) + c1;
                    cadres.remove(cadres.size()-1);
                    cadres.add(cd1);
                }
                else cadres.add(c1+"");
            }
            else cadres.add(c1+"");
        }

        cadres = vaciar_pila(cadres);
        tlist = cadres.size();

        for(int j=0; j<tlist+1; j++){
            if(resultado.size() >= 3){
                elm1 = resultado.pop();
                elm2 = resultado.pop();
                elm3 = resultado.pop();
                if(isopera(elm1.charAt(0)) && isnumber(elm2.charAt(0)) && isnumber(elm3.charAt(0))){
                    rop = operar(Float.parseFloat(elm3),Float.parseFloat(elm2),elm1.charAt(0));
                    resultado.push(rop);
                }
                else {
                    resultado.push(elm3);
                    resultado.push(elm2);
                    resultado.push(elm1);
                }
            }
            if(j<tlist)resultado.push(cadres.get(j));
        }
        res.setText("");
        res.setText(resultado.pop());
    }
    //----------------------------------------------------------------------------------------------
    //VACIAR LA PILA EN LA LISTA
    public List<String> vaciar_pila(List<String> lista){
        int tam = operadores.size();
        for(int i=0; i<tam; i++){
            if('(' != operadores.peek())lista.add(operadores.pop()+"");
            else {
                operadores.pop();
                i=tam+1;
            }
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
    //----------------------------------------------------------------------------------------------
    //OPERACIONES
    public String operar(float num1, float num2, char simbolo){
        if(simbolo == '+') return (num1+num2)+"";
        if(simbolo == '-') return (num1-num2)+"";
        if(simbolo == 'x') return (num1*num2)+"";
        if(simbolo == '/') return (num1/num2)+"";
        return "";
    }
}
