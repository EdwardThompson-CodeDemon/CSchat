package sparta.realm.cschat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import sparta.realm.Realm;
import sparta.realm.cschat.Models.EmailConfiguration;
import sparta.realm.cschat.Models.Member;
import sparta.realm.spartautils.svars;


public class Globals {

    public static void set_sync_time(Context act, String sync_time_var) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putString("sync_time_var", sync_time_var);
        saver.commit();

    }

    public static String getTransactionNo() {
       return System.currentTimeMillis()+"::"+new Random().nextDouble()+"::"+new Random().nextDouble();

    }
  public static String sync_time(Context act) {
        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getString("sync_time_var", null);

    }
 public static void set_group_created(Context act, Boolean group_created) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putBoolean("group_created", group_created);
        saver.commit();

    }


    public static void set_data_set(Context act, int data_set) {

        SharedPreferences.Editor saver = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();

        saver.putInt("data_set", data_set);
        saver.commit();

    }

    public static int data_set(Context act) {
        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        return prefs.getInt("data_set", 0);

    }


    public static void set_working_players(Context act, ArrayList<String> departments, String id)
    {

        SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(departments);

        saver.putString("wop::"+id+departments.getClass().getName(),json);
        saver.commit();


    }

    public static ArrayList<String> working_players(Context act,String id)
    {
        ArrayList<String> departments =new ArrayList<String>();

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("wop::"+id+departments.getClass().getName(), "");
        departments = gson.fromJson(json, departments.getClass());
        try {
            return departments==null?new ArrayList<>():departments ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
 public static String working_departments_json(Context act,String id)
    {
        ArrayList<Integer> departments =new ArrayList<Integer>();

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("wo::"+id+departments.getClass().getName(), new JSONArray().toString());
        return json;



    }
    public static void set_working_object(Context act, Object working_object,String id)
    {

        SharedPreferences.Editor saver =act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(working_object);

        saver.putString("wo::"+id+working_object.getClass().getName(),json);
        saver.commit();


    }
    public static Object working_object(Context act,Class<?> t,String id)
    {

        SharedPreferences prefs = act.getSharedPreferences(svars.sharedprefsname, act.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("wo::"+id+t.getName(), "");
        if(!json.equals("")){
            return gson.fromJson(json, t);
        }else{
            return null;
        }
//        try {

//            return emp==null?t.newInstance():emp ;
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public static Member myself(){ return (Member)working_object(Realm.context, Member.class,"0"); }
    public static void set_myself(Member mem){
        set_working_object(Realm.context,mem,"0");
    }


    public static void setEmailConfiguration( EmailConfiguration config) {set_working_object(Realm.context,config,"0"); }
    public static EmailConfiguration emailConfiguration() { return (EmailConfiguration)working_object(Realm.context,EmailConfiguration.class,"0");  }
    public static void populate_date(final Context context,final EditText edt, final Calendar cb, final boolean include_time, long min, long max) {
        Calendar cc = Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try {


            mYear = cb.get(Calendar.YEAR);
            mMonth = cb.get(Calendar.MONTH);
            mDay = cb.get(Calendar.DAY_OF_MONTH);

        } catch (Exception ex) {
        }


        DatePickerDialog dpd = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {


                    edt.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + year);


                    try {
                        cb.setTime(svars.sdf_user_friendly_date.parse(edt.getText().toString()));
                    } catch (Exception ex) {
                    }


                    if (include_time) {


                        new TimePickerDialog(context, (view1, hourOfDay, minute) -> {

                            String dom = "" + hourOfDay;
                            String moy = "" + minute;
                            char[] domch = dom.toCharArray();
                            char[] moych = moy.toCharArray();

                            if (domch.length < 2) {
                                dom = "0" + dom;
                            }
                            if (moych.length < 2) {
                                moy = "0" + moy;
                            }
                            edt.setText(edt.getText().toString() + " " + dom + ":" + moy + ":00");
                            try {
                                cb.setTime(svars.sdf_user_friendly_time.parse(edt.getText().toString()));
                            } catch (Exception ex) {
                            }


                        }, cb.get(Calendar.HOUR_OF_DAY), cb.get(Calendar.MINUTE), false).show();
                    }

                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR, calendar_min.get(Calendar.YEAR));

if(min>0){
    dpd.getDatePicker().setMinDate(min);
}
if(max>0){
    dpd.getDatePicker().setMaxDate(max);
}

        //  dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    public static void populate_date(EditText edt, Calendar cb)
    {
        Calendar cc= Calendar.getInstance();
        int mYear = cc.get(Calendar.YEAR);
        int mMonth = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);


        try{


            mYear = cb.get(Calendar.YEAR);
            mMonth = cb.get(Calendar.MONTH);
            mDay = cb.get(Calendar.DAY_OF_MONTH);

        }catch (Exception ex){}



        DatePickerDialog dpd = new DatePickerDialog(edt.getContext(),
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {



                        edt.setText((dayOfMonth<10?"0"+dayOfMonth:dayOfMonth) + " - "  + ((monthOfYear + 1)<10?"0"+(monthOfYear + 1):(monthOfYear + 1)) +" - "+year );


                        try{
                            cb.setTime(svars.sdf_user_friendly_date.parse(edt.getText().toString()));
                        }catch (Exception ex){}

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
        Calendar calendar_min = Calendar.getInstance();
        calendar_min.set(Calendar.YEAR,calendar_min.get(Calendar.YEAR));


        dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

        //  dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    public static String confirmation_email(String code,String name){

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <style type=\"text/css\">\n" +
                "        @media screen {\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: normal;\n" +
                "                font-weight: 400;\n" +
                "                src: local('Lato Regular'), local('Lato-Regular'), url(https://fonts.gstatic.com/s/lato/v11/qIIYRU-oROkIk8vfvxw6QvesZW2xOQ-xsNqO47m55DA.woff) format('woff');\n" +
                "            }\n" +
                "\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: normal;\n" +
                "                font-weight: 700;\n" +
                "                src: local('Lato Bold'), local('Lato-Bold'), url(https://fonts.gstatic.com/s/lato/v11/qdgUG4U09HnJwhYI-uK18wLUuEpTyoUstqEm5AMlJo4.woff) format('woff');\n" +
                "            }\n" +
                "\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: italic;\n" +
                "                font-weight: 400;\n" +
                "                src: local('Lato Italic'), local('Lato-Italic'), url(https://fonts.gstatic.com/s/lato/v11/RYyZNoeFgb0l7W3Vu1aSWOvvDin1pK8aKteLpeZ5c0A.woff) format('woff');\n" +
                "            }\n" +
                "\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: italic;\n" +
                "                font-weight: 700;\n" +
                "                src: local('Lato Bold Italic'), local('Lato-BoldItalic'), url(https://fonts.gstatic.com/s/lato/v11/HkF_qI1x_noxlxhrhMQYELO3LdcAZYWl9Si6vvxL-qU.woff) format('woff');\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        /* CLIENT-SPECIFIC STYLES */\n" +
                "        body,\n" +
                "        table,\n" +
                "        td,\n" +
                "        a {\n" +
                "            -webkit-text-size-adjust: 100%;\n" +
                "            -ms-text-size-adjust: 100%;\n" +
                "        }\n" +
                "\n" +
                "        table,\n" +
                "        td {\n" +
                "            mso-table-lspace: 0pt;\n" +
                "            mso-table-rspace: 0pt;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            -ms-interpolation-mode: bicubic;\n" +
                "        }\n" +
                "\n" +
                "        /* RESET STYLES */\n" +
                "        img {\n" +
                "            border: 0;\n" +
                "            height: auto;\n" +
                "            line-height: 100%;\n" +
                "            outline: none;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            border-collapse: collapse !important;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            height: 100% !important;\n" +
                "            margin: 0 !important;\n" +
                "            padding: 0 !important;\n" +
                "            width: 100% !important;\n" +
                "        }\n" +
                "\n" +
                "        /* iOS BLUE LINKS */\n" +
                "        a[x-apple-data-detectors] {\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: none !important;\n" +
                "            font-size: inherit !important;\n" +
                "            font-family: inherit !important;\n" +
                "            font-weight: inherit !important;\n" +
                "            line-height: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        /* MOBILE STYLES */\n" +
                "        @media screen and (max-width:600px) {\n" +
                "            h1 {\n" +
                "                font-size: 32px !important;\n" +
                "                line-height: 32px !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        /* ANDROID CENTER FIX */\n" +
                "        div[style*=\"margin: 16px 0;\"] {\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"background-color: #f4f4f4; margin: 0 !important; padding: 0 !important;\">\n" +
                "    <!-- HIDDEN PREHEADER TEXT -->\n" +
                "    <div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Lato', Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\"> We're thrilled to have you here! Get ready to dive into your new account. </div>\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "        <!-- LOGO -->\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#000000\" align=\"center\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 40px 10px;\"> </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#000000\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 4px; line-height: 48px;\">\n" +
                "                            <h1 style=\"font-size: 48px; font-weight: 400; margin: 2;\">Welcome!</h1> <img src=\" https://img.icons8.com/clouds/100/000000/handshake.png\" width=\"125\" height=\"120\" style=\"display: block; border: 0px;\" />\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 40px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                            <p style=\"margin: 0;\">Hi "+name+", we're excited to have you get started. First, you need to confirm your account. Just enter the code below on the app.</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\">\n" +
                "                            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                                <tr>\n" +
                "                                    <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 60px 30px;\">\n" +
                "                                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                                            <tr>\n" +
                "                                                <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#696969\"><a  style=\"font-size: 20px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: #ffffff; text-decoration: none; padding: 15px 25px; border-radius: 2px; border: 1px solid #000000; display: inline-block;\">"+code+"</a></td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr> <!-- COPY -->\n" +
                "                    \n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                            <p style=\"margin: 0;\">If you have any questions, just reply to this email—we're always happy to help out.</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 4px 4px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                            <p style=\"margin: 0;\">Cheers,<br>Capture solutions Team</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 30px 10px 0px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#696969\" align=\"center\" style=\"padding: 30px 30px 30px 30px; border-radius: 4px 4px 4px 4px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                            <h2 style=\"font-size: 20px; font-weight: 400; color: #ffffff; margin: 0;\">Need more help?</h2>\n" +
                "                            <p style=\"margin: 0;\"><a href=\"#\" target=\"_blank\" style=\"color: #ffffff;\">We&rsquo;re here to help you out</a></p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                   \n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }
}
