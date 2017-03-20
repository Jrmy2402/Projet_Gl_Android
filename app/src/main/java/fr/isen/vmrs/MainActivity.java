
package fr.isen.vmrs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class MainActivity extends Activity {
    EditText email, password;
    Button login;
    TextView link;

    SharedPreferences pref;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.loginButton);
        link = (TextView) findViewById(R.id.siteText);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        link.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.url)));
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String emailtxt;
                final String passwordtxt;
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                new AsyncLogin().execute(emailtxt,passwordtxt);

            }
        });
    }

    //Connexion au serveur, recupere un token
    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://172.31.1.25:80/auth/local");
                //url = new URL("http://10.0.2.2:9000/auth/local");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from server
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0]) //email
                        .appendQueryParameter("password", params[1]); //password
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }
            try {

                int response_code = conn.getResponseCode();
                System.out.println("CODE ? " + response_code);

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    //System.out.println("Connexion OK");
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        result.append("\n");
                    }
                    //reader.close();//test
                    String resultat = result.toString();

                    JSONObject jsonObj = new JSONObject(resultat);
                    String token = jsonObj.getString("token");
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("token",token);
                    edit.commit();

                    //System.out.println("TOKEN ? " + token); //On recupere un token
                    // Pass data to onPostExecute method

                    return("success");//result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("success"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                System.out.println("Login et Pw OK");
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Vous êtes connécté", Toast.LENGTH_SHORT).show();
                finish();

            }else if (result.equalsIgnoreCase("unsuccessful")){

                // If username and password does not match display a error message
                Toast.makeText(MainActivity.this, "Email ou Mot de Passe invalide", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception")) {

                Toast.makeText(MainActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_LONG).show();

            }
        }

    }
}
