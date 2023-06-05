package sparta.realm.cschat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.cschat.Models.RealmDynamics.spartaDynamics;
import sparta.realm.cschat.activities.Communication;
import sparta.realm.cschat.activities.ConversationActivity;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.MessageMessageStatus;
import sparta.realm.cschat.Models.OnlineStatus;
import sparta.realm.cschat.Models.message;
import sparta.realm.cschat.activities.ConversationsVP;
import sparta.realm.cschat.activities.Splash;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;


public class ChatApplication extends Application {

    private static Context baseContext;
    private static Context appContext;
    public static AppConfig UIPA_APP = null;
    public static RealmClientSyncOverride rcso = new RealmClientSyncOverride();
    public static SyncInterface syncInterface = new SyncInterface() {
    };
    public static String logTag = "Application";
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        baseContext = getBaseContext();
        UIPA_APP = new AppConfig("http://ta.cs4africa.com:9090",
                null,
                "U.I.P.A.",
                "MAIN CAMPUS",
                "/Authentication/Login/Submit", false

        );
        UIPA_APP.WORKING_PROFILE_MODE = AppConfig.PROFILE_MODE.GENERAL;


        Realm.Initialize(this, new spartaDynamics(), BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID/*+":uipa"*/, UIPA_APP);


        SharedPreferences.Editor saver = this.getSharedPreferences(svars.sharedprefsname, this.MODE_PRIVATE).edit();
        saver.putString("username", "evans");
        saver.putString("pass", "demo123");
        saver.commit();
        svars.set_sync_interval_mins(this, 60);
//        DatabaseManager.database.execSQL("DELETE FROM online_status");
//        DatabaseManager.database.execSQL("DELETE FROM message_message_status");
//
//        DatabaseManager.database.execSQL("DELETE FROM messages");
//        DatabaseManager.database.execSQL("DELETE FROM member_info_table");
//        DatabaseManager.database.execSQL("DELETE FROM member_image_table");



        createNotificationChannels();
        InitializeOnlineStatusChecker();
//        NeuralNetwork nn = new NeuralNetwork(2,10,1);
//        Log.e(logTag,"Fitting neural network");
//        nn.fit(X, Y, 5000);
//        double [][] input ={{0,0},{0,1},{1,0},{1,1}};
//        Log.e(logTag,"Fit neural network");
//        for(double d[]:input)
//        {
//          List<Double>  output = nn.predict(d);
//           Log.e(logTag,output.toString());
//        }//Output

    }

    static double[][] X = {
            {0, 0},
            {1, 0},
            {0, 1},
            {1, 1}
    };
    static double[][] Y = {
            {0}, {1}, {1}, {0}
    };

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    public static void createNotificationChannel(Member source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    source.transaction_no,
                    source.name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel " + source.name);
            NotificationManager manager = getAppContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }

    //----------> changed from main activity to conversationsVP <-------
    public static void sendChannel1Notification(Context context) {
        Intent activityIntent = new Intent(context, ConversationsVP.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);


        ArrayList<Member> unread_conversations = Realm.databaseManager.loadObjectArray(Member.class, new Query().setTableFilters("transaction_no in(select source from messages where transaction_no not in(select message_tr from message_message_status where message_status='" + MessageMessageStatus.MessageStatus.Read + "'))"));
        for (Member m : unread_conversations) {
            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                    .setLabel("Your answer...")
                    .build();

            Intent replyIntent;
            PendingIntent replyPendingIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                replyIntent = new Intent(context, MessageNotificationReceiver.class);
                replyIntent.putExtra("partner", m.transaction_no);
                replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, 0);

            } else {
                //start chat activity instead (PendingIntent.getActivity)
                //cancel notification with notificationManagerCompat.cancel(id)
            }

            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                    R.drawable.storage_icon,
                    "Reply",
                    replyPendingIntent
            ).addRemoteInput(remoteInput).build();

            Person me = new Person.Builder().setName("Me").build();
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(me);
            messagingStyle.setConversationTitle(m.name);

            Query conversation_query = new Query().setTableFilters("(destination='" + m.transaction_no + "' OR source='" + m.transaction_no + "')", "(destination='" + Globals.myself().transaction_no + "' OR source='" + Globals.myself().transaction_no + "')").setOrderFilters(false, "reg_time").setLimit(10);
            ArrayList<message> messages = Realm.databaseManager.loadObjectArray(message.class, conversation_query);
            for (message mess : messages) {
                if (Globals.myself().transaction_no.equalsIgnoreCase(mess.source)) {
                    if (mess.message_type.equalsIgnoreCase(message.MessageType.TextObject.ordinal() + "")) {
                        NotificationCompat.MessagingStyle.Message notificationMessage =
                                null;
                        try {
                            notificationMessage = new NotificationCompat.MessagingStyle.Message(
                                    mess.text,
                                    svars.sdf_db_time.parse(mess.reg_time).getTime(),
                                    me
                            );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        messagingStyle.addMessage(notificationMessage);
                    } else if (mess.message_type.equalsIgnoreCase(message.MessageType.Audio.ordinal() + "")) {
                        NotificationCompat.MessagingStyle.Message notificationMessage =
                                null;
                        try {
                            notificationMessage = new NotificationCompat.MessagingStyle.Message(
                                    "Audio",
                                    svars.sdf_db_time.parse(mess.reg_time).getTime(),
                                    me
                            );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        messagingStyle.addMessage(notificationMessage);

                    }

                } else {
                    if (mess.message_type.equalsIgnoreCase(message.MessageType.TextObject.ordinal() + "")) {
                        NotificationCompat.MessagingStyle.Message notificationMessage =
                                null;
                        try {
                            notificationMessage = new NotificationCompat.MessagingStyle.Message(
                                    mess.text,
                                    svars.sdf_db_time.parse(mess.reg_time).getTime(),
                                    m.name
                            );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        messagingStyle.addMessage(notificationMessage);

                    } else if (mess.message_type.equalsIgnoreCase(message.MessageType.Audio.ordinal() + "")) {
                        NotificationCompat.MessagingStyle.Message notificationMessage =
                                null;
                        try {
                            notificationMessage = new NotificationCompat.MessagingStyle.Message(
                                    "Audio",
                                    svars.sdf_db_time.parse(mess.reg_time).getTime(),
                                    m.name
                            );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        messagingStyle.addMessage(notificationMessage);

                    }


                }
            }

            createNotificationChannel(m);
            Notification notification = new NotificationCompat.Builder(context, m.transaction_no)
                    .setSmallIcon(R.drawable.download_icon)
                    .setStyle(messagingStyle)
                    .addAction(replyAction)
                    .setColor(Color.BLUE)
                    .setColorized(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, notification);


        }


    }


    public static void attatchInterface(SyncInterface syncInterface) {
        ChatApplication.syncInterface = syncInterface;
    }

    public static Boolean online = false;
    public static long last_typing_time = 0;
    public static String last_typing_target_transaction_no = "";
    public static String last_recording_target_transaction_no = "";
    public static long last_recording_audio_time = 0;

    public static void Initialize(SyncInterface syncInterface) {

        ChatApplication.syncInterface = syncInterface;
        rcso.Initialize(new RealmClientCallbackInterface.Stub() {
            @Override
            public void on_status_changed(String s) throws RemoteException {
                Log.e(logTag, "Status changed" + s);
            }

            @Override
            public void on_info_updated(String s) throws RemoteException {
                Log.e(logTag, "On Info updated " + s);
                ChatApplication.syncInterface.onInfoUpdated(s);

            }

            @Override
            public void on_main_percentage_changed(int i) throws RemoteException {

            }

            @Override
            public void on_secondary_progress_changed(int i) throws RemoteException {

            }

            @Override
            public void onSynchronizationBegun() throws RemoteException {

            }

            @Override
            public void onServiceSynchronizationCompleted(String service_id) throws RemoteException {
                if (Globals.myself()!=null&&(service_id.equalsIgnoreCase("11") || service_id.equalsIgnoreCase("12"))) {
                    Log.e("RSCO", "Received messo");
                    Query unread_messages_query = new Query().setTableFilters("destination='" + Globals.myself().transaction_no + "'", "transaction_no not in (select message_tr from message_message_status where message_status ='" + MessageMessageStatus.MessageStatus.Delivered.ordinal() + "')");
                    ArrayList<message> unreadMessages = Realm.databaseManager.loadObjectArray(message.class, unread_messages_query);
                    Iterator<message> it = unreadMessages.iterator();
                    if (it.hasNext()) {
                        do {
                            deliverMessage(it.next().transaction_no);
                        }
                        while (it.hasNext());
                        rcso.upload("14");
                    }
                }

                ChatApplication.syncInterface.onSyncCompleted(service_id);
            }

            void deliverMessage(String message_tr) {
                MessageMessageStatus messageMessageStatus = new MessageMessageStatus(Globals.myself().transaction_no, message_tr, MessageMessageStatus.MessageStatus.Delivered.ordinal() + "");
                Realm.databaseManager.insertObject(messageMessageStatus);
                sendChannel1Notification(getAppContext());
            }

            @Override
            public void onSynchronizationCompleted() throws RemoteException {

            }

            @Override
            public List<String> OnAboutToUploadObjects(String s, List<String> list) throws RemoteException {
                return null;
            }

            @Override
            public String OnAboutToDownloadObjects(String s) throws RemoteException {
//                if (s.equalsIgnoreCase("1") || s.equalsIgnoreCase("3")) {
//                    try {
//                        JSONObject filter = new JSONObject();
//
//                        filter.put("email", Globals.data_set(Realm.context) == 0 ? new JSONArray(new String[]{""}) : new JSONArray(new int[]{Globals.data_set(Realm.context)}));
//
//
//                        Log.e("RSCO", filter.toString());
//                        return filter.toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
                return null;
            }

            @Override
            public String OnDownloadedObjects(String s, String s1) throws RemoteException {
                return s1;
            }

            @Override
            public ParcelFileDescriptor OnDownloadedData(String s, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
                return null;
            }
        });
//        InitializeOnlineStatusChecker();

    }
    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= 28)
            return Application.getProcessName();

        // Using the same technique as Application.getProcessName() for older devices
        // Using reflection since ActivityThread is an internal API

        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");

            // Before API 18, the method was incorrectly named "currentPackageName", but it still returned the process name
            // See https://github.com/aosp-mirror/platform_frameworks_base/commit/b57a50bd16ce25db441da5c1b63d48721bb90687
            String methodName = Build.VERSION.SDK_INT >= 18 ? "currentProcessName" : "currentPackageName";

            Method getProcessName = activityThread.getDeclaredMethod(methodName);
            return (String) getProcessName.invoke(null);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

public static  void InitializeOnlineStatusChecker()
{
//    Log.e(logTag,"Process name:"+getProcessName());
    if(!getProcessName().equals("sparta.realm.cschat"))return;
    new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
            try {
                PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isInteractive();
                if ((isActivityRunning(ConversationActivity.class) || isActivityRunning(ConversationsVP.class))&&isScreenOn) {
                    if (svars.is_server_online(rcso.serverAddress)) {
                        Log.e(logTag, " Conversation activities running");
                        long current_time = System.currentTimeMillis();
                        long typing_time_diff_in_seconds = (current_time - last_typing_time) / 1000;
                        long recording_time_diff_in_seconds = (current_time - last_recording_audio_time) / 1000;
                        if (recording_time_diff_in_seconds < 2) {
                            Realm.databaseManager.insertObject(new OnlineStatus(Globals.myself().transaction_no, OnlineStatus.onlineStatus.recording_audio,last_recording_target_transaction_no));

                        } else if (typing_time_diff_in_seconds < 2) {
                            Realm.databaseManager.insertObject(new OnlineStatus(Globals.myself().transaction_no, OnlineStatus.onlineStatus.typing,last_typing_target_transaction_no));

                        } else {
                            Realm.databaseManager.insertObject(new OnlineStatus(Globals.myself().transaction_no, OnlineStatus.onlineStatus.online));
                        }
                        rcso.upload("16");
                        online = true;
                    } else {
                        Log.e(logTag, " SERVER NOT REACHABLE");

                    }
                } else {
                    if (svars.is_server_online(rcso.serverAddress)) {
                        Log.e(logTag, "Conversation activities not running !!!");
                        if (online) {
                            OnlineStatus lastOnlineStatus = Realm.databaseManager.loadObject(OnlineStatus.class, new Query().setTableFilters("member_transaction_no='" + Globals.myself().transaction_no + "'").addOrderFilters("reg_time",false));
                          if(OnlineStatus.onlineStatus.values()[Integer.parseInt(lastOnlineStatus.online_status)]!= OnlineStatus.onlineStatus.offline) {
                              Log.e(logTag, "Inserting Offline status !!!");
                              Realm.databaseManager.insertObject(new OnlineStatus(Globals.myself().transaction_no, OnlineStatus.onlineStatus.offline));
                              rcso.upload("16");
                          }
                            online = false;
                        }

                    } else {
                        Log.e(logTag, " SERVER NOT REACHABLE");

                    }
                }
            }catch (Exception ex){
                Log.e(logTag, "Error creating online status:",ex);

            }
        }
    }, 5000, 1000 * 15);
}
    public static boolean isServiceRunning(Context act, Class<?> serviceClass) {
//        Class<?> serviceClass=App_updates.class;

        ActivityManager manager;
        manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isActivityRunning(Class activityClass) {
        ActivityManager result = (ActivityManager) getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = result.getRunningTasks(Integer.MAX_VALUE);

        Log.e(logTag, "Topmost activity: " + services.get(0).topActivity.toString());
        Log.e(logTag, "Checking for  activity: " + activityClass.getCanonicalName());

//            if (activityClass.getCanonicalName().equalsIgnoreCase(services.get(0).topActivity.toString())){
        if (services.get(0).topActivity.toString().contains(activityClass.getCanonicalName())) {

            return true;
        }

        return false;
    }

    public static Boolean isActivityRunning_(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) baseContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
