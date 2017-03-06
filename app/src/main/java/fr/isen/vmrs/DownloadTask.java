package fr.isen.vmrs;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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


public class DownloadTask extends AsyncTask<String, String, String> {

    private AsyncResponse delegate;


    HttpURLConnection conn;
    URL url = null;
    List<VM> ListVM = new ArrayList<VM>();

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(List<VM> result);
    }

    public DownloadTask(AsyncResponse delegate){
        this.delegate = delegate;
    }


    protected String doInBackground(String... params) {
        String token = params[0];
        try {

            // Enter URL address where your php file resides
            url = new URL("http://10.0.2.2:9000/api/users/meVm");


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


        if(result.equalsIgnoreCase("success"))
        {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

            //VMAdapter adapter = new VMAdapter(ListActivity.this,ListVM);
            delegate.processFinish(ListVM);
            //Toast.makeText(ListActivity.class, "Liste actualisée", Toast.LENGTH_LONG).show();

        }else if (result.equalsIgnoreCase("unsuccessful")){

            delegate.processFinish(null);
            // If username and password does not match display a error message
            //Toast.makeText(ListActivity.this, "Liste non actualisée", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("exception")) {
            delegate.processFinish(null);
            //Toast.makeText(ListActivity.this, "Problème de Connexion. Essayez de vous reconnecter", Toast.LENGTH_LONG).show();

        }
    }

}
