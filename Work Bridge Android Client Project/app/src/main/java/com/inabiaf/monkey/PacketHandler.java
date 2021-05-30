package com.inabiaf.monkey;

import android.util.Log;

import org.json.JSONObject;

public class PacketHandler {

    public static void HandlePacket(String json, MainActivity main)
    {
        Log.i("MONKEY-PACKET-HANDLER",json);
        try
        {
            JSONObject obj = new JSONObject(json);
            String cmd = obj.getString("cmd");
            boolean success = obj.getBoolean("success");

            if(cmd.equals("login"))
            {
                if(success)
                {
                    main.name = obj.getString("name");
                    main.surname = obj.getString("surname");
                    main.email = obj.getString("mail");
                    main.city = obj.getString("city");
                    main.interests = obj.getString("interests");
                    main.about = obj.getString("about");
                    main.isLoggedIn = true;
                    main.isWorker = obj.getBoolean("isWorker");
                    main.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/index");
                }
                else main.WarnUser("Error","Incorrect email or password");
            }
            else if (cmd.equals("register"))
            {
                if(success)
                {
                    main.name = obj.getString("name");
                    main.surname = obj.getString("surname");
                    main.email = obj.getString("mail");
                    main.city = obj.getString("city");
                    main.interests = obj.getString("interests");
                    main.about = obj.getString("about");
                    main.isLoggedIn = true;
                    main.isWorker = obj.getBoolean("isWorker");
                    main.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/profile_finishReg");
                }
                else main.WarnUser("Error","Such user already exists");
            }
            else if (cmd.equals("finishReg"))
            {
                main.about = obj.getString("about");
                main.interests = obj.getString("interests");
                main.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/index");
            }
            else if (cmd.equals("saveUserData"))
            {
                main.name = obj.getString("name");
                main.surname = obj.getString("surname");
                main.email = obj.getString("mail");
                main.city = obj.getString("city");
                main.interests = obj.getString("interests");
                main.about = obj.getString("about");
                main.isLoggedIn = true;
                main.isWorker = obj.getBoolean("isWorker");
                main.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/index");
            }
            else if (cmd.equals("getJobOffer"))
            {
                if(success)
                {
                    main.base64Image = obj.getString("base64Image");
                    main.description = obj.getString("description");
                    main.location = obj.getString("location");
                    main.pay = obj.getString("pay");
                    main.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/offer");
                }
                else
                {
                    if (!main.mainPage) main.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/index");
                    main.WarnUser("Info","Currently there are no job offers found for you. Try again later.");
                }
            }



        }
        catch (Exception e)
        {
            Log.e("MONKEY-PACKET-HANDLER",e.toString());
        }

    }
}
