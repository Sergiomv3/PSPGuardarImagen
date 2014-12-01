package com.example.sergio.guardarimagen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    private RadioGroup rg;
    private RadioButton rbPr;
    private RadioButton rbPu;
    private Button btGuardar;
    private EditText ruta;
    private File donde;
    private ImageView iv;
    private String tipo,nombreUltimo;
    private EditText nombre;
    private Boolean enviable = false;
    private boolean guardado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rbPr = (RadioButton) findViewById(R.id.rbPrivada);
        rbPu = (RadioButton) findViewById(R.id.rbPublica);
        ruta = (EditText) findViewById(R.id.ruta);
        nombre = (EditText) findViewById(R.id.etNombre);
        iv = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!donde.equals(null) && !tipo.equals(null)) {
            outState.putString("ruta", donde.getAbsolutePath() + "/img." + tipo);
        }



    }
    /*
    Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("image/*");
    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(root.getAbsolutePath() + "/DCIM/Camera/image.jpg"));
    startActivity(Intent.createChooser(share,"Share via"));
    */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(!savedInstanceState.isEmpty()) {
            Bitmap bm = BitmapFactory.decodeFile(savedInstanceState.getString("ruta"));
            iv.setImageBitmap(bm);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.opciones, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.compartir) {
            if(enviable) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(donde.getAbsolutePath() + nombreUltimo + "."+tipo)));
                startActivity(Intent.createChooser(share, "Compartir vía"));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void guardarImagen(){

        if(rbPr.isChecked()){
            donde = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else if(rbPu.isChecked()){
            donde = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }


        try {
            URL url = null;
            url = new URL(ruta.getText().toString());
            tipo = url.toString().substring(url.toString().lastIndexOf('.')+1);
            InputStream is = url.openStream();
            OutputStream os;
            if(nombre.getText().toString().equals("")){
                os = new FileOutputStream(donde +"/"+ url.toString().substring(url.toString().lastIndexOf('/')+1));
                nombreUltimo = url.toString().substring(url.toString().lastIndexOf('/') + 1);
            }else {
                os = new FileOutputStream(donde +"/"+ nombre.getText().toString() +"."+ tipo);
                nombreUltimo = nombre.getText().toString();
            }
            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();


        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.this, "URL no válida",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e){
            Toast.makeText(MainActivity.this, "Imagen no encontrada en Internet",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(tipo.equalsIgnoreCase("jpg") || tipo.equalsIgnoreCase("jpeg") || tipo.equalsIgnoreCase("gif") || tipo.equalsIgnoreCase("png")){

            guardado =  true;
        }else{

            guardado = false;
        }
    }
    public class Hilo extends AsyncTask<Object, Integer, String>{

        @Override
        protected String doInBackground(Object... params) {
            guardarImagen();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(guardado) {
                enviable = true;
                Toast.makeText(MainActivity.this, "Imagen guardada en formato " + tipo, Toast.LENGTH_SHORT).show();
                if(nombre.getText().toString().equals("")){
                    Bitmap bm = BitmapFactory.decodeFile(donde.getAbsolutePath() + "/" + nombreUltimo);
                    iv.setImageBitmap(bm);
                }else {
                    Bitmap bm = BitmapFactory.decodeFile(donde.getAbsolutePath() + "/" + nombreUltimo + "." + tipo);
                    iv.setImageBitmap(bm);
                }
            }else{
                enviable = false;
                Toast.makeText(MainActivity.this, "¿Es una imagen?", Toast.LENGTH_SHORT).show();
            }


        }
    }
    public void guardar(View v){
        if(ruta.length()>0) {
            Hilo h = new Hilo();
            h.execute();
        }else{
            Toast.makeText(MainActivity.this, "Por favor, introduce una url.", Toast.LENGTH_SHORT).show();
        }
    }
}
