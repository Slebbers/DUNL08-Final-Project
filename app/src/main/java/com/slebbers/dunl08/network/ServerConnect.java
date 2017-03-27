package com.slebbers.dunl08.network;

import android.util.Log;

import com.google.gson.Gson;
import com.slebbers.dunl08.model.Checklist;
import com.slebbers.dunl08.model.ChecklistItem;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerConnect {

    private OkHttpClient client;
    private String returnedJSON;
    private String echo;

    /**
     * Constructs a new instance of ServerConnect
     */
    public ServerConnect() {
        client = new OkHttpClient();
    }

    /**
     * Gets all information stored in the server database
     * @return String containing JSON from server
     */
    public String getServerDatabaseJSON() {
        Thread networkRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //String url = "https://devweb2015.cis.strath.ac.uk/~isb14166/Checklist/php/appConnect.php";
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

    /**
     * Submits checklist data saved locally to the server
     * @param items Checklists saved locally
     * @return {@code True if successful} or {@code False if unsuccessful}
     */
    public boolean submitLocalData(final List<Checklist> items) {
        Thread networkRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://devweb2015.cis.strath.ac.uk/~isb14166/Checklist/php/dataSync.php";
                    String json = new Gson().toJson(items);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();


                    Response response = client.newCall(request).execute();
                    echo = response.body().string();
                    Log.d("ServerConnect", echo);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });

        networkRequest.start();

        try {
            networkRequest.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Submits a checklist to be inserted into the server database
     * @param checklist The checklist to be inserted
     * @return String containing the echo value returned by the server
     */
    public String submitChecklist(final Checklist checklist) {
        Thread networkRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://devweb2015.cis.strath.ac.uk/~isb14166/Checklist/php/appSubmit.php?json=";

                    url += new Gson().toJson(checklist);
                    Log.d("con", url);
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
