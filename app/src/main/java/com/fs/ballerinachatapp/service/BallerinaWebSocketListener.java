package com.fs.ballerinachatapp.service;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.fs.ballerinachatapp.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by enadun on 17/Jan/22.
 */

public class BallerinaWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private TextView tv;
    public MainActivity activity;

    public BallerinaWebSocketListener(MainActivity activity){
        super();
        this.activity = activity;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("Hello, Welcome to the Ballerina Hackathon Chat!");
        //webSocket.close(NORMAL_CLOSURE_STATUS, "Bye Bye !");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        outPut(text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        outPut("Good Bye!");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        outPut("Error: " + t.getMessage());
    }

    private void outPut(final String text) {

        try {
            final JSONObject jb = new JSONObject(text);
            String chatMsg;
            Boolean isUser;
            switch (jb.getString("message-type")){
                case "signin":
                    this.activity.token = jb.getString("access_token");
                    this.activity.username = this.activity.usernameEditText.getText().toString();
                    this.activity.setupChat();
                    break;
                case "chat":
                    if (this.activity.username.equals(jb.getString("from"))){
                        chatMsg = "me:/n" + jb.getString("message");
                        isUser = true;
                    }else{
                        chatMsg = jb.getString("from") + "/n" + jb.getString("message");
                        isUser = false;
                    }
                    this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                activity.receiveChatMessage(jb.getString("message"),false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                default:break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

