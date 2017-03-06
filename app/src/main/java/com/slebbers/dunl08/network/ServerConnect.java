package com.slebbers.dunl08.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.slebbers.dunl08.model.Checklist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerConnect {

    private OkHttpClient client;
    private String returnedJSON;
    private String echo;

    public ServerConnect() {
        client = new OkHttpClient();
    }

    public String getServerDatabaseJSON() {
        Thread networkRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://devweb2015.cis.strath.ac.uk/~isb14166/Checklist/php/appConnect.php";

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    returnedJSON = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        networkRequest.start();

        try {
            networkRequest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return returnedJSON;
    }

    public String submitChecklist(final Checklist checklist) {
        Thread networkRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://devweb2015.cis.strath.ac.uk/~isb14166/Checklist/php/appSubmit.php?json=";

                    url += new Gson().toJson(checklist);

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    echo = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        networkRequest.start();

        try {
            networkRequest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return echo;
    }
}
