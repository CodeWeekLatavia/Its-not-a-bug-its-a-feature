package com.inabiaf.monkey;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONObject;

public class JavaScriptInterface {
    public ConnectTask mainConnection;
    public Context mainAct;
    public JavaScriptInterface(ConnectTask main, Context context)
    {
        mainConnection = main;
        mainConnection.main = (MainActivity)context;
        mainAct = context;
    }

    @JavascriptInterface
    public void Register(String name, String surname,String email,String password,String city, boolean isWorker ) //TODO add other stuff
    {
        try
        {
            Log.i("MONKEY","Register clicked");
            JSONObject obj = new JSONObject();
            obj.put("cmd","register");
            obj.put("name",name);
            obj.put("surname",surname);
            obj.put("mail",email);
            obj.put("pass",password);
            obj.put("city",city);
            obj.put("isWorker",isWorker);
            sendMessage(obj.toString());
        }
        catch (Exception e)
        {
            Log.e("MONKEY",e.toString());
        }
    }

    @JavascriptInterface
    public void Login(String email, String password)
    {
        try
        {
            Log.i("MONKEY","Login clicked");
            JSONObject obj = new JSONObject();
            obj.put("cmd","login");
            obj.put("mail",email);
            obj.put("pass",password);
            sendMessage(obj.toString());
        }
        catch (Exception e)
        {
            Log.e("MONKEY",e.toString());
        }

    }

    @JavascriptInterface
    public void Warn(String title, String message)
    {
        mainConnection.main.WarnUser(title,message);
    }


    @JavascriptInterface
    public void FinishRegistration(String info, String interests)
    {
        try
        {
            Log.i("MONKEY","Finish registration clicked");
            JSONObject obj = new JSONObject();
            obj.put("cmd","finishReg");
            obj.put("info",info);
            obj.put("interests",interests);
            sendMessage(obj.toString());
        }
        catch (Exception e)
        {
            Log.e("MONKEY",e.toString());
        }
    }

    @JavascriptInterface
    public void SaveUserData(String name,String surname,String mail,String city,String about,String interests, boolean isWorker)
    {
        try
        {
            Log.i("MONKEY","Save user data clicked");
            JSONObject obj = new JSONObject();
            obj.put("cmd","saveUserData");
            obj.put("name",name);
            obj.put("surname",surname);
            obj.put("mail",mail);
            obj.put("city",city);
            obj.put("about",about);
            obj.put("interests",interests);
            obj.put("isWorker",isWorker);
            sendMessage(obj.toString());
        }
        catch (Exception e)
        {
            Log.e("MONKEY",e.toString());
        }
    }

    @JavascriptInterface
    public void getJobOffer()
    {
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("cmd","getJobOffer");
            sendMessage(obj.toString());
        }
        catch (Exception e)
        {
            Log.e("MONKEY",e.toString());

        }
    }

    public void sendMessage(String msg)
    {
     //   if(mainConnection == null || mainConnection.mTcpClient == null)
     //   {
      //      mainConnection = new ConnectTask();
        //    mainConnection.main = (MainActivity)mainAct;
        //    mainConnection.execute("");
        //}
        try
        {
            mainConnection.mTcpClient.sendMessage(msg);
        }
        catch (Exception e)
        {
            if(mainConnection.mTcpClient ==null)
            {
                mainConnection = new ConnectTask();
                mainConnection.main = (MainActivity)mainAct;
                mainConnection.execute("");
            }
            sendMessage(msg);
        }

    }

}
