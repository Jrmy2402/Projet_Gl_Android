package fr.isen.vmrs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class InfosActivity extends AppCompatActivity {
    TextView AffichName;
    TextView AffichIP;
    TextView AffichOS;
    TextView AffichInfo;
    TextView AffichDate;
    TextView AffichApps;
    TextView AffichMemory;
    TextView AffichCPU;
    TextView AffichNetwork;
    TextView AffichBlock;
    TextView TagIP;
    TextView TagOS;
    TextView TagInfo;
    TextView TagDate;
    TextView TagApps;
    TextView TagMemory;
    TextView TagCPU;
    TextView TagNetwork;
    TextView TagBlock;
    Button StartVM;
    Button StopVM;
    Space Space2;
    Space Space3;
    Space Space4;
    Space Space5;
    Space Space6;
    Space Space7;
    Space Space8;
    Space Space9;
    Button Back;
    ImageView Logo;
    SharedPreferences pref;
    String token;
    String id;
    String Apps[];
    private Boolean isConnected = false;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        token = pref.getString("token", "");

        //Configuration du socket
        try {
            IO.Options opts = new IO.Options();
            opts.query = "token=" + token;
            socket = IO.socket("http://172.31.1.25:9000/", opts);
            System.out.println("Socket configuré");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //Lancement du Socket
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("infoVm", onNewData);
        socket.connect();

        //Recuperation des données de la page Liste
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        String name = bundle.getString("name");
        String os = bundle.getString("os");
        String ip = bundle.getString("ip");
        String date = bundle.getString("date");
        int port = bundle.getInt("port");
        int image = bundle.getInt("image");

        //Instanciation des elements de l'activité
        AffichName = (TextView) findViewById(R.id.AffichName);
        AffichIP = (TextView) findViewById(R.id.AffichIP);
        AffichOS = (TextView) findViewById(R.id.AffichOS);
        AffichInfo = (TextView) findViewById(R.id.AffichInfos);
        AffichDate = (TextView) findViewById(R.id.AffichDate);
        AffichApps = (TextView) findViewById(R.id.AffichApps);
        AffichCPU = (TextView) findViewById(R.id.AffichCPU);
        AffichMemory = (TextView) findViewById(R.id.AffichMemory);
        AffichBlock = (TextView) findViewById(R.id.AffichBlock);
        AffichNetwork = (TextView) findViewById(R.id.AffichNetwork);

        TagIP = (TextView) findViewById(R.id.TagIP);
        TagOS = (TextView) findViewById(R.id.TagOS);
        TagInfo = (TextView) findViewById(R.id.TagInfos);
        TagDate = (TextView) findViewById(R.id.TagDate);
        TagApps = (TextView) findViewById(R.id.TagApps);
        TagCPU = (TextView) findViewById(R.id.TagCPU);
        TagMemory = (TextView) findViewById(R.id.TagMemory);
        TagNetwork = (TextView) findViewById(R.id.TagNetwork);
        TagBlock = (TextView) findViewById(R.id.TagBlock);

        Space2 = (Space) findViewById(R.id.space2);
        Space3 = (Space) findViewById(R.id.space3);
        Space4 = (Space) findViewById(R.id.space4);
        Space5 = (Space) findViewById(R.id.space5);
        Space6 = (Space) findViewById(R.id.space6);
        Space7 = (Space) findViewById(R.id.space7);
        Space8 = (Space) findViewById(R.id.space8);
        Space9 = (Space) findViewById(R.id.space9);

        Back = (Button) findViewById(R.id.return_button);
        StartVM = (Button) findViewById(R.id.buttonON);
        StopVM = (Button) findViewById(R.id.buttonOFF);
        Logo = (ImageView) findViewById(R.id.logoVM);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Lancement de la toolbar
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_vmrs);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Affichage des infos de base sur la page
        if (!name.isEmpty()) {
            AffichName.setText(name);
        } else {
            AffichName.setVisibility(View.GONE);
            //AffichName.setText("No name");
        }

        Logo.setImageResource(image);

        if (!ip.isEmpty()) {
            if (port != 0) {
                AffichIP.setText(ip + ":" + port);
            } else AffichIP.setText(ip);
        } else {
            AffichIP.setVisibility(View.GONE);
            TagIP.setVisibility(View.GONE);
            Space2.setVisibility(View.GONE);
        }

        if (!os.isEmpty()) {
            AffichOS.setText(os);
        } else {
            AffichOS.setVisibility(View.GONE);
            TagOS.setVisibility(View.GONE);
            Space3.setVisibility(View.GONE);
        }

        //Traitement de la date -- AAAA-MM-DDTHH:MM:SS.mmmm
        if (!date.isEmpty()) {
            String tempDate = date.substring(0, 10);
            String tempHour = date.substring(11, 19);
            date = tempDate + " " + tempHour;
            AffichDate.setText(date);
        } else {
            AffichDate.setVisibility(View.GONE);
            TagDate.setVisibility(View.GONE);
            Space4.setVisibility(View.GONE);
        }
/*
        if (Apps.length != 0) {
            for (int i = 0; i < Apps.length; i++) {
                if (i == 0) {
                    tempApps = Apps[i];
                } else tempApps = tempApps + "\n" + Apps[i];
            }
            AffichApps.setText(tempApps);
        } else {
            AffichApps.setVisibility(View.GONE);
            TagApps.setVisibility(View.GONE);
            Space4.setVisibility(View.GONE);
        }
*/
        Toast.makeText(InfosActivity.this, "Chargement des informations ...", Toast.LENGTH_LONG).show();

        Back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        StartVM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new AsyncLogin().execute("start");
            }
        });

        StopVM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new AsyncLogin().execute("stop");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //On deconnecte le socket à la fin de l'activité
        socket.disconnect();
        //System.out.println("onDestroy");
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("Data In", onNewData);
    }

    //Récupération des données via socket
    private Emitter.Listener onNewData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //System.out.println(data);
                    try {

                        if (!data.getString("info").isEmpty()) {
                            AffichInfo.setText(data.getString("info"));
                            AffichInfo.setVisibility(View.VISIBLE);
                            TagInfo.setVisibility(View.VISIBLE);
                            Space6.setVisibility(View.VISIBLE);
                        } else {
                            AffichInfo.setVisibility(View.GONE);
                            TagInfo.setVisibility(View.GONE);
                            Space6.setVisibility(View.GONE);
                        }

                        //Traitement de la liste des applis
                        if (!data.isNull("application")) {
                            JSONArray jsonApps = new JSONArray(data.getString("application"));
                            Apps = new String[jsonApps.length()];
                            for (int j = 0; j < jsonApps.length(); j++) {
                                Apps[j] = jsonApps.getString(j);
                            }
                        } else Apps = null;
                        String tempApps = "";
                        if (Apps.length != 0) {
                            for (int i = 0; i < Apps.length; i++) {
                                if (i == 0) {
                                    tempApps = Apps[i];
                                } else tempApps = tempApps + "\n" + Apps[i];
                            }
                            AffichApps.setText(tempApps);
                            AffichApps.setVisibility(View.VISIBLE);
                            TagApps.setVisibility(View.VISIBLE);
                            Space5.setVisibility(View.VISIBLE);
                        } else {
                            AffichApps.setVisibility(View.GONE);
                            TagApps.setVisibility(View.GONE);
                            Space5.setVisibility(View.GONE);
                        }

                        //Affichage des feedbacks
                        if (!data.isNull("feedback")) {
                            JSONObject jsonFeedback = new JSONObject(data.getString("feedback"));
                            //System.out.println(jsonFeedback);
                            //if (jsonFeedback.getInt("cpu") != 0) {
                            AffichCPU.setText(jsonFeedback.getString("cpu"));
                            AffichCPU.setVisibility(View.VISIBLE);
                            TagCPU.setVisibility(View.VISIBLE);
                            Space7.setVisibility(View.VISIBLE);
                            /*} else {
                                AffichCPU.setVisibility(View.GONE);
                                TagCPU.setVisibility(View.GONE);
                                Space7.setVisibility(View.GONE);
                            }*/

                            JSONObject jsonMemory = new JSONObject(jsonFeedback.getString("memory"));
                            if (!jsonMemory.getString("total").isEmpty()) {
                                String tempMemory = jsonMemory.getString("usage") + "/" + jsonMemory.getString("total") + " (" + jsonMemory.getString("percentage") + "%)";
                                AffichMemory.setText(tempMemory);
                                AffichMemory.setVisibility(View.VISIBLE);
                                TagMemory.setVisibility(View.VISIBLE);
                                Space8.setVisibility(View.VISIBLE);
                            } else {
                                AffichMemory.setVisibility(View.GONE);
                                TagMemory.setVisibility(View.GONE);
                                Space8.setVisibility(View.GONE);
                            }

                            JSONObject jsonNetwork = new JSONObject(jsonFeedback.getString("network"));
                            if (!jsonNetwork.getString("total").isEmpty()) {
                                String tempNetwork = jsonNetwork.getString("usage") + "/" + jsonNetwork.getString("total");
                                AffichNetwork.setText(tempNetwork);
                                AffichNetwork.setVisibility(View.VISIBLE);
                                TagNetwork.setVisibility(View.VISIBLE);
                                Space9.setVisibility(View.VISIBLE);
                            } else {
                                AffichNetwork.setVisibility(View.GONE);
                                TagNetwork.setVisibility(View.GONE);
                                Space9.setVisibility(View.GONE);
                            }

                            JSONObject jsonBlock = new JSONObject(jsonFeedback.getString("block"));
                            if (!jsonBlock.getString("total").isEmpty()) {
                                String tempBlock = jsonBlock.getString("usage") + "/" + jsonBlock.getString("total");
                                AffichBlock.setText(tempBlock);
                                AffichBlock.setVisibility(View.VISIBLE);
                                TagBlock.setVisibility(View.VISIBLE);
                            } else {
                                AffichBlock.setVisibility(View.GONE);
                                TagBlock.setVisibility(View.GONE);
                            }


                        } else {
                            AffichNetwork.setVisibility(View.GONE);
                            AffichCPU.setVisibility(View.GONE);
                            AffichBlock.setVisibility(View.GONE);
                            AffichMemory.setVisibility(View.GONE);
                            TagNetwork.setVisibility(View.GONE);
                            TagCPU.setVisibility(View.GONE);
                            TagBlock.setVisibility(View.GONE);
                            TagMemory.setVisibility(View.GONE);
                            Space7.setVisibility(View.GONE);
                            Space8.setVisibility(View.GONE);
                            Space9.setVisibility(View.GONE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
    };

    //Connexion du socket
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onCOnnect");
                    //System.out.println(isConnected);
                    if (!isConnected) {
                        //System.out.println(token);
                        if (null != token)
                            //System.out.println("ID : " + id);
                            socket.emit("statsVm", id);
                        System.out.println("Socket connecté");
                        //Toast.makeText(getApplicationContext(), "Socket Connecté", Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }

    };

    //Deconnexion du socket
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    System.out.println("Socket deconnecté");
                    //Toast.makeText(getApplicationContext(),"Socket déconnecté", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    //Gestion erreur socket
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Socket erreur");
                    Toast.makeText(getApplicationContext(),
                            "Erreur de connexion", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    // Commande Start/Stop VM, methode GET
    private class AsyncLogin extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(InfosActivity.this);
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
                if (params[0].equalsIgnoreCase("start")) {
                    url = new URL("http://172.31.1.25:9000/api/users/meVmStart/" + id);
                } else if (params[0].equalsIgnoreCase("stop")) {
                    url = new URL("http://172.31.1.25:9000/api/users/meVmStop/" + id);
                }

                // Setup HttpURLConnection class to send and receive data from server
                conn = (HttpURLConnection) url.openConnection();
                //conn.setRequestProperty("Content-Type","application/json");
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

                if (response_code == HttpURLConnection.HTTP_OK) {
                    return ("success");
                } else return("unsuccessful");

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

                Toast.makeText(InfosActivity.this, "Etat changé", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("unsuccessful")){

                // If username and password does not match display a error message
                Toast.makeText(InfosActivity.this, "Impossible d'éxécuter cette commande", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception")) {

                Toast.makeText(InfosActivity.this, "Problème de serveur.", Toast.LENGTH_LONG).show();

            }
        }

    }

}