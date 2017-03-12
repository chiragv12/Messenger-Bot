package com.chig.messengerbot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    BroadcastReceiver receiver;
    SmsManager manager;
    Handler handler;
    int state = 0;
    int c = 0;
    String name = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView_1);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
        }

        manager = SmsManager.getDefault();
        handler = new Handler();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for(int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                }

                Toast.makeText(context, messages[0].getMessageBody(), Toast.LENGTH_SHORT).show();

                    //manager.sendTextMessage(messages[0].getOriginatingAddress(), null, "Test", null, null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //manager.sendTextMessage(messages[0].getOriginatingAddress(), null, "Test", null, null);
                            String message = "";
                            String message2 = "";

                            switch(state){
                                case 0:

                                    textView.setText("State 1: Greeting");
                                    if(c == 0) {
                                        if (messages[0].getMessageBody().toUpperCase().contains("HELLO") || messages[0].getMessageBody().toUpperCase().contains("HI") || messages[0].getMessageBody().toUpperCase().contains("HEY")) {
                                            int rand = (int)(Math.random()*2);
                                            if(rand == 0) {
                                                message = "Hello and welcome to the wonderful world of Pokémon. My name is Professor Oak, but everyone calls me the Pokémon Professor." +
                                                        " What is your name?";
                                            }
                                            else if(rand == 1){
                                                message = "Hello how are you doing? My name is Professor Oak, but everyone calls me the Pokémon Professor." +
                                                        "What is your name?";
                                            }
                                            c++;
                                        }
                                    }
                                    else if(c == 1){
                                            message = "Hello " + messages[0].getMessageBody() + "! Would you like to learn about the gyms or the pokemon?";
                                            name = messages[0].getMessageBody();
                                            c = 0;
                                            state++;
                                    }

                                    break;
                                case 1:
                                    textView.setText("State 2: Basic Info");
                                    if(messages[0].getMessageBody().toUpperCase().contains("GYMS") || messages[0].getMessageBody().toUpperCase().contains("GYM")) {
                                        message = "In the world of pokemon there are 8 gyms, each of which get progressively harder." +
                                                " Now, which starter would you pick: Squirtle, Charmander, or Bulbasaur?";
                                        state++;
                                    }
                                    else if(messages[0].getMessageBody().toUpperCase().contains("POKEMON")){
                                        message = "There are many different types of pokemon that you can discover when you explore." +
                                                " Now, which starter would you pick: Squirtle, Charmander, or Bulbasaur? ";
                                        state++;
                                    }

                                    break;
                                case 2:
                                    textView.setText("State 3: Starter Info");
                                    if (messages[0].getMessageBody().toUpperCase().contains("SQUIRTLE")) {
                                        message = "Squirtle the turtle is the water starter. Squirtle is a great choice against fire and will eventually evolve into Blastoise.";
                                        state++;
                                    } else if (messages[0].getMessageBody().toUpperCase().contains("CHARMANDER")) {
                                        message = "Charmander is the fire starter. Charmander is a great choice against grass and will eventually evolve into Charzard";
                                        state++;
                                    } else if (messages[0].getMessageBody().toUpperCase().contains("BULBASAUR")) {
                                        message = "Balbasaur is the grass starter. Bulbasaur is a great choice against water and will eventually evolve into Venasaur.";
                                        state++;
                                    }
                                    if(state == 3){
                                        message2 = "Would you like to learn about the pokedex next, or battle items?";
                                    }

                                    break;
                                case 3:
                                    textView.setText("State 4: Exit State");
                                    if(messages[0].getMessageBody().toUpperCase().contains("POKEDEX")){
                                        message = "The pokedex is a tool that all trainers should have. It records all the pokemon you have seen and what you have caught";
                                        state++;
                                    }
                                    else if(messages[0].getMessageBody().toUpperCase().contains("BATTLE ITEMS")){
                                        message = "There are many items that you can use during a battle. Things like potions will help your pokemon a lot during a battle.";
                                        state++;
                                    }

                                    if(state == 4){
                                        message2 = "Well, " + name + " that's all the time I have to talk. It was fun to talk with you. Until next time.";
                                    }

                                    break;
                            }


                            if(!message.equals("")){
                                manager.sendTextMessage(messages[0].getOriginatingAddress(), null, message, null, null);
                            }
                            if(!message2.equals("")){
                                manager.sendTextMessage(messages[0].getOriginatingAddress(), null, message2, null, null);
                            }


                        }
                    }, 10000);

            }
        };

        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(receiver, filter);

    }
}
