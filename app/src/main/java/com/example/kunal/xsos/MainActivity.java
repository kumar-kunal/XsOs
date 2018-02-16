package com.example.kunal.xsos;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;


import java.io.IOException;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SalutDataCallback, View.OnClickListener  {

    public static final String TAG = "SalutTestApp";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;
    SalutDataCallback callback;



    int myActivePlayer = 0; //0 is cross and 1 is circle
    int dp [] = {2,2,2,2,2,2,2,2,2};
    int win[][] = {
            {0,1,2},
            {3,4,5},
            {6,7,8},
            {0,4,8},
            {2,4,6},
            {0,3,6},
            {1,4,7},
            {2,5,8}
    };
    String s[] = {"Crosses" , "Circles"};
    int ww=3;

    //winning logic


    public void playAgain(View view)
    {
        ww=3;

        for(int i=0;i< dp.length ; i++)
            dp[i] = 2;

        myActivePlayer=0;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.line1);
        for(int i=0 ; i<linearLayout.getChildCount(); i++)
        {
            ImageView ll = (ImageView) linearLayout.getChildAt(i);
            ll.setImageResource(R.drawable.qmark);
        }
        linearLayout = (LinearLayout) findViewById(R.id.line2);
        for(int i=0 ; i<linearLayout.getChildCount(); i++)
        {
            ImageView ll = (ImageView) linearLayout.getChildAt(i);
            ll.setImageResource(R.drawable.qmark);
        }
        linearLayout = (LinearLayout) findViewById(R.id.line3);
        for(int i=0 ; i<linearLayout.getChildCount(); i++)
        {
            ImageView ll = (ImageView) linearLayout.getChildAt(i);
            ll.setImageResource(R.drawable.qmark);
        }

    }
    public boolean check(Vector<Integer> a , Vector<Integer> s)
    {
        int count=0;
        for(int j=0;j<3;j++)
        {
            int val = a.elementAt(j);
            for(int i=0;i<s.size();i++)
            {
                if(s.elementAt(i) == val)
                    count++;
            }
        }
        if(count==3)
            return true;
        else
            return false;

    }
    public void imageTapped(View view)
    {
        ImageView myTapped = (ImageView) view ;
        Log.i("TAG IS " , "Image Number Is " + myTapped.getTag().toString());
        int tapValue = Integer.parseInt(myTapped.getTag().toString());
        if(dp[tapValue]==2)
        {
            dp[tapValue] = myActivePlayer;
            if(myActivePlayer==0)
            {
                myTapped.setImageResource(R.drawable.cross);
                myTapped.animate().rotation(360).setDuration(1000);
                myActivePlayer=1;
            }
            else if(myActivePlayer==1)
            {
                @SuppressLint("ResourceType") Animation anim1 = AnimationUtils.loadAnimation(getApplicationContext() , R.layout.anim);//slide
                myTapped.setImageResource(R.drawable.circle);
                myActivePlayer=0;
            }

            Vector<Integer> set0 = new Vector<Integer>();
            Vector<Integer> set1 = new Vector<Integer>();

            for(int i=0;i<dp.length ; i++)
            {
                if(dp[i] == 0)
                    set0.add(i);
                else if(dp[i]==1)
                    set1.add(i);
            }
            for(int i=0;i<8;i++)
            {
                Vector<Integer> aa = new Vector<Integer>();
                for(int j=0;j<3;j++) {

                    aa.add(win[i][j]);
                }
                if(check(aa , set0))
                {
                    Toast.makeText(MainActivity.this , "Crosses Won The Game" , Toast.LENGTH_SHORT).show();
                    Log.i("set 0 is " , set0.elementAt(0) + " "+ set0.elementAt(1)+ " "+set0.elementAt(2) );
                    myActivePlayer=3;
                    ww=0;
                }

                if(check(aa , set1))
                {
                    Toast.makeText(MainActivity.this , "Circles Won The Game" , Toast.LENGTH_SHORT).show();
                    Log.i("set 0 is " , set1.elementAt(0) + " "+ set1.elementAt(1)+ " "+ set1.elementAt(2) );
                    myActivePlayer=3;
                    ww=1;
                }

            }



        }
        else if(ww==3){
            //getCallingActivity or MainActivity.this
            Toast.makeText(MainActivity.this , "This Possition Is Already Filled" , Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this , s[ww] + " WON THE GAME" , Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hostingBtn = (Button) findViewById(R.id.host_button);
        discoverBtn = (Button) findViewById(R.id.join_button);

        hostingBtn.setOnClickListener((View.OnClickListener) this);
        discoverBtn.setOnClickListener((View.OnClickListener) this);

        dataReceiver = new SalutDataReceiver(this, this);

        serviceData = new SalutServiceData("testAwesomeService", 60606,
                "HOST");

            network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                // wiFiFailureDiag.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });

    }

    private void setupNetwork()
    {
        if(!network.isRunningAsHost)
        {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                }
            });

            hostingBtn.setText("Stop Service");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);
        }
        else
        {
            network.stopNetworkService(false);
            hostingBtn.setText("Start Service");
            discoverBtn.setAlpha(1f);
            discoverBtn.setClickable(true);
        }
    }


    private void discoverServices()
    {
        if(!network.isRunningAsHost && !network.isDiscovering)
        {
            network.discoverNetworkServices(new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(getApplicationContext(), "Device: " + network.foundDevices.get(0).instanceName + " found.", Toast.LENGTH_SHORT).show();
                }
            }, true);
            discoverBtn.setText("Stop Discovery");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);
        }
        else
        {
            network.stopServiceDiscovery(true);
            discoverBtn.setText("Discover Services");
            hostingBtn.setAlpha(1f);
            hostingBtn.setClickable(false);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_base, menu);
//        return true;
//    }


    @Override
    public void onDataReceived(Object data) {
        //Data Is Received
        Log.d(TAG, "Received network data.");
        try
        {
            Message newMessage = LoganSquare.parse(String.valueOf((Message)data), Message.class);
            Log.d(TAG, newMessage.description);  //See you on the other side!
            //Do other stuff with data.
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }
    }

    @Override
    public void onClick(View v) {

        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(v.getId() == R.id.host_button)
        {
            setupNetwork();
        }
        else if(v.getId() == R.id.join_button)
        {
            discoverServices();
        }
    }

}