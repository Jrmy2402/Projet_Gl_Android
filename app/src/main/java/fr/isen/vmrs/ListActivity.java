package fr.isen.vmrs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    SharedPreferences pref;
    String token;
    private ListView mListView;
    List<VM> ListVM = new ArrayList<VM>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //On ajoute la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_vmrs);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mListView = (ListView) findViewById(R.id.listView);

        //Recuperation du token
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        token = pref.getString("token","");
        //System.out.println("Token : " + token);

        new AsyncLogin().execute();

        //Gestion de la Listview
        VMAdapter adapter = new VMAdapter(this,ListVM);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        VM vmselect = (VM) mListView.getItemAtPosition(position);

        //Si la VM n'est pas en Loading on envoie les infos et lance l'activité suivante
        if (!vmselect.getInfo().equalsIgnoreCase("Loading")) {
            Intent infoIntent = new Intent(ListActivity.this, InfosActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString("name", vmselect.getName());
            bundle.putString("id", vmselect.getID());
            bundle.putString("os", vmselect.getOS());
            bundle.putString("ip", vmselect.getIP());
            bundle.putString("date", vmselect.getAdDate());
            //bundle.putString("info", vmselect.getInfo());
            bundle.putInt("port", vmselect.getPort());
            //bundle.putStringArray("apps",vmselect.getApps());
            bundle.putInt("image", vmselect.getImage());
            //name,os,ip,date,info,port,apps,image
            infoIntent.putExtras(bundle);
            startActivity(infoIntent);
        } else Toast.makeText(ListActivity.this, "VM non disponible pour le moment", Toast.LENGTH_LONG).show();
    }

    //On ajoute le menu à l'appbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Gestion de la selection dans la barre de menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // On lance le navigateur internet vers le site VMRS
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.url)));
                startActivity(i);
                return true;

            case R.id.action_refresh:
                //On vide la liste
                ListVM.clear();
                //this.callDownloadTask();
                new AsyncLogin().execute();
                // On actualise la listview avec le serveur
                System.out.println(ListVM.size());

                return true;

            case R.id.action_unconnect:
                // On déconnecte l'utilisateur et on le renvoie vers MainActivity
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("token","");
                edit.commit();
                Intent mainIntent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(mainIntent);
                Toast.makeText(ListActivity.this, "Vous êtes déconnecté", Toast.LENGTH_LONG).show();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    // Connexion au serveur, methode GET
    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(ListActivity.this);
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
                url = new URL("http://172.31.1.25:9000/api/users/meVm");
                //url = new URL("http://10.0.2.2:9000/api/users/meVm");


                // Setup HttpURLConnection class to send and receive data from server
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestMethod("GET");


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

                    //On recupere le resultat JSON en String
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //System.out.println(result);
                    String resultat = result.toString();
                    //System.out.println(resultat);

                    //Mise en forme en JSONArray
                    JSONArray jsonA = new JSONArray(resultat);
                    System.out.println("Number of entries " + jsonA.length());

                    //Conversion de l'objet JSON vers VM.class
                    for (int i = 0; i < jsonA.length(); i++) {
                        JSONObject jsonObject = jsonA.getJSONObject(i);
                        String tempID, tempIP, tempName;
                        int tempPort;

                        //On vérifie que les champs existent dans le JSON
                        if (!jsonObject.isNull("_id")) tempID = jsonObject.getString("_id");
                        else tempID = "";

                        if (!jsonObject.isNull("ip")) tempIP = jsonObject.getString("ip");
                        else tempIP = "";

                        if (!jsonObject.isNull("port")) tempPort = jsonObject.getInt("port");
                        else tempPort = 0;

                        if (!jsonObject.isNull("name")) tempName = jsonObject.getString("name");
                        else tempName = "";

                        //On construit l'objet VM
                        VM tempVM = new VM(tempID,tempIP,tempPort,tempName);

                        if (!jsonObject.isNull("info")) tempVM.setInfo(jsonObject.getString("info"));
                        else tempVM.setInfo("");

                        if (!jsonObject.isNull("idContainer")) tempVM.setIdContainer(jsonObject.getString("idContainer"));
                        else tempVM.setIdContainer("");

                        if (!jsonObject.isNull("ad_date")) tempVM.setAdDate(jsonObject.getString("ad_date"));
                        else tempVM.setAdDate("");

                        if (!jsonObject.isNull("OS")) tempVM.setOS(jsonObject.getString("OS"));
                        else tempVM.setOS("");

                        if (!jsonObject.isNull("application")) {
                            JSONArray jsonApps = new JSONArray(jsonObject.getString("application"));
                            String tab[] = new String[jsonApps.length()];
                            for (int j = 0; j < jsonApps.length(); j++) {
                                tab[j] = jsonApps.getString(j);
                            }
                            tempVM.setApps(tab);
                        } else tempVM.setApps(null);

                        /*
                        //AFFICHAGE DES DONNEES DANS LA CONSOLE
                        System.out.println("ID : " + tempVM.getID());
                        System.out.println("Info : " + tempVM.getInfo());
                        System.out.println("Nom : " + tempVM.getName());
                        System.out.println("IP : " + tempVM.getIP());
                        System.out.println("Port : " + tempVM.getPort());
                        System.out.println("Id Container : " + tempVM.getIdContainer());
                        String applis[] = tempVM.getApps();
                        for (int j=0; j < applis.length;j++) {
                            System.out.println("Appli " + (j+1) + " de la machine " + (i+1) + " : " + applis[j]);
                        }
                        System.out.println("Date : " + tempVM.getAdDate());
                        System.out.println("OS : " + tempVM.getOS());
                        System.out.println("-------------------------");
*/
                        //Ajout de chaque objet VM à la liste
                        ListVM.add(tempVM);

                    }
                    System.out.println("Longueur liste : " + ListVM.size());

                    // Pass data to onPostExecute method

                    return("success");

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

                //On remplie la listview
                VMAdapter adapter = new VMAdapter(ListActivity.this,ListVM);
                mListView.setAdapter(adapter);

                Toast.makeText(ListActivity.this, "Liste actualisée", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("unsuccessful")){

                // If username and password does not match display a error message
                Toast.makeText(ListActivity.this, "Liste non actualisée", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception")) {

                Toast.makeText(ListActivity.this, "Problème de Connexion. Essayez de vous reconnecter", Toast.LENGTH_LONG).show();

            }
        }

    }


}
