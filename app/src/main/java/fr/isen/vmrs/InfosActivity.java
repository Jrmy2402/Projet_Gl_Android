package fr.isen.vmrs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;


public class InfosActivity extends AppCompatActivity {
    TextView AffichName;
    TextView AffichIP;
    TextView AffichOS;
    TextView AffichInfo;
    TextView AffichDate;
    TextView AffichApps;
    TextView AffichMemory;
    TextView AffichCPU;
    TextView AffichIO;
    TextView TagIP;
    TextView TagOS;
    TextView TagInfo;
    TextView TagDate;
    TextView TagApps;
    Space Space2;
    Space Space3;
    Space Space4;
    Space Space5;
    Button Back;
    ImageView Logo;
    SharedPreferences pref;
    String token;
    String id;
    private Boolean isConnected = false;
    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        token = pref.getString("token","");

        try{
            IO.Options opts = new IO.Options();
            //opts.forceNew = true;
            opts.query = "token=" + token;
            //opts.path = "/sock";
            socket = IO.socket("http://172.31.1.25:9000/", opts);
            System.out.println("Socket configuré");
        } catch (URISyntaxException e){
            throw new RuntimeException(e);
        }

        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("infoVm", onNewData);
        socket.connect();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        String name = bundle.getString("name");
        String os = bundle.getString("os");
        String ip = bundle.getString("ip");
        String date = bundle.getString("date");
        String info = bundle.getString("info");
        String tempApps = "";
        int port = bundle.getInt("port");
        String Apps[] = bundle.getStringArray("apps");
        int image = bundle.getInt("image");

        AffichName = (TextView) findViewById(R.id.AffichName);
        AffichIP = (TextView) findViewById(R.id.AffichIP);
        AffichOS = (TextView) findViewById(R.id.AffichOS);
        AffichInfo = (TextView) findViewById(R.id.AffichInfos);
        AffichDate = (TextView) findViewById(R.id.AffichDate);
        AffichApps = (TextView) findViewById(R.id.AffichApps);

        TagIP = (TextView) findViewById(R.id.TagIP);
        TagOS = (TextView) findViewById(R.id.TagOS);
        TagInfo = (TextView) findViewById(R.id.TagInfos);
        TagDate = (TextView) findViewById(R.id.TagDate);
        TagApps = (TextView) findViewById(R.id.TagApps);

        Space2 = (Space) findViewById(R.id.space2);
        Space3 = (Space) findViewById(R.id.space3);
        Space4 = (Space) findViewById(R.id.space4);
        Space5 = (Space) findViewById(R.id.space5);

        Back = (Button) findViewById(R.id.return_button);
        Logo = (ImageView) findViewById(R.id.logoVM);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_vmrs);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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

        if (!info.isEmpty()) {
            AffichInfo.setText(info);
        } else {
            AffichInfo.setVisibility(View.GONE);
            TagInfo.setVisibility(View.GONE);
            Space5.setVisibility(View.GONE);
        }

        if (!os.isEmpty()) {
            AffichOS.setText(os);
        } else {
            AffichOS.setVisibility(View.GONE);
            TagOS.setVisibility(View.GONE);
            Space3.setVisibility(View.GONE);
        }

        //AAAA-MM-DDTHH:MM:SS.mmmm
        if (!date.isEmpty()) {
            String tempDate = date.substring(0,10);
            String tempHour = date.substring(11,19);
            date = tempDate + " " + tempHour;
            AffichDate.setText(date);
        } else {
            AffichDate.setVisibility(View.GONE);
            TagDate.setVisibility(View.GONE);
        }

        if (Apps.length != 0) {
            for (int i = 0; i < Apps.length; i++) {
                if (i == 0) {
                    tempApps = Apps[i];
                }
                else tempApps = tempApps + "\n" + Apps[i];
            }
            AffichApps.setText(tempApps);
        } else {
            AffichApps.setVisibility(View.GONE);
            TagApps.setVisibility(View.GONE);
            Space4.setVisibility(View.GONE);
        }


        Back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        socket.disconnect();
        System.out.println("onDestroy");
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("Data In", onNewData);
    }


    private Emitter.Listener onNewData = new Emitter.Listener() {
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    System.out.println(data);

                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("onCOnnect");
                    System.out.println(isConnected);
                    if (!isConnected) {
                        //System.out.println(token);
                        if (null != token)
                            //System.out.println("ID : " + id);
                        socket.emit("statsVm", id);
                        System.out.println("Socket connecté");
                        Toast.makeText(getApplicationContext(),
                                "Socket Connecté", Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }

    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    System.out.println("Socket deconnecté");
                    Toast.makeText(getApplicationContext(),
                            "Socket déconnecté", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

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
}
