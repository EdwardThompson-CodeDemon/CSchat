package sparta.realm.cschat.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.realm.annotations.sync_status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.cschat.Adapters.MessageAdapter;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.MessageMessageStatus;
import sparta.realm.cschat.Models.OnlineStatus;
import sparta.realm.cschat.Models.message;
import sparta.realm.cschat.SyncInterface;
import sparta.realm.cschat.ChatApplication;
import sparta.realm.cschat.databinding.ActivityConversationBinding;
import sparta.realm.spartautils.svars;


public class ConversationActivity extends AppCompatActivity {

    Member partner;
    public ArrayList<message> messageArrayList;
    ActivityConversationBinding binding;
    Query conversation_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        partner = Realm.databaseManager.loadObject(Member.class, new Query().setTableFilters("transaction_no='" + getIntent().getStringExtra("tr") + "'"));
        conversation_query = new Query().setTableFilters("(destination='" + partner.transaction_no + "' OR source='" + partner.transaction_no + "')", "(destination='" + Globals.myself().transaction_no + "' OR source='" + Globals.myself().transaction_no + "')").setOrderFilters(true, "reg_time");
        conversation_query.order_filters = new String[]{"reg_time"};
        messageArrayList = Realm.databaseManager.loadObjectArray(message.class, conversation_query);
        initUi();

    }

    message outgoing_message = new message();

    void initUi() {
        binding.draftEdt.setImeOptions(EditorInfo.IME_ACTION_SEND);
        binding.draftEdt.setRawInputType(InputType.TYPE_CLASS_TEXT);
        binding.draftEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == 0) {

                    String available_text = binding.draftEdt.getText().toString();
                    if (available_text == null || available_text.trim().length() < 1) return true;
                    if (available_text.trim().length() >= 1) {
                        sendMessage(binding.draftEdt.getText().toString().trim());
                        binding.draftEdt.setText("");
                    }
                    return false;
                }
                return true;
            }
        });
        binding.messageList.setLayoutManager(new LinearLayoutManager(this));
        binding.messageList.setAdapter(new MessageAdapter(messageArrayList, new MessageAdapter.MessageActionListener() {
            @Override
            public void onMessageClicked(int position, message m) {


            }

            @Override
            public void onMessageGameInviteAccepted(String game_invite_transaction_no) {


            }

            @Override
            public void onMessageGameInviteDeclined(String game_invite_transaction_no) {



            }
        }));
        binding.draftEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChatApplication.last_typing_time = System.currentTimeMillis();
//                String available_text= binding.draftEdt.getText().toString();
//                if(available_text==null||available_text.trim().length()<1)return;
//                if(available_text.trim().length()>=1&&available_text.endsWith("\n"))
//                {
//                    sendMessage(binding.draftEdt.getText().toString().trim());
//                    binding.draftEdt.setText("");
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (messageArrayList.size() > 1) {
            binding.messageList.smoothScrollToPosition(messageArrayList.size() - 1);
            readAllUnreadMessages();

        }
        binding.toolbar.title.setText(partner.name);
    }

    void readAllUnreadMessages() {
//       DatabaseManager.database.execSQL("Insert into message_message_status () VALUES (select * from messages where id not in (select message_tr from message_message_status where message_status='3'))");
        Query unread_messages_query = new Query().setTableFilters("(source='" + partner.transaction_no + "')", "(destination='" + Globals.myself().transaction_no + "')", "transaction_no not in (select message_tr from message_message_status where member_tr='" + Globals.myself().transaction_no + "' AND message_status ='" + MessageMessageStatus.MessageStatus.Read.ordinal() + "')");
        ArrayList<message> unreadMessages = Realm.databaseManager.loadObjectArray(message.class, unread_messages_query);
        Iterator<message> it = unreadMessages.iterator();
        if (it.hasNext()) {
            do {
                readMessage(it.next().transaction_no);
            }
            while (it.hasNext());
            ChatApplication.rcso.upload("14");
        }

    }




    void readMessage(String message_tr) {
        MessageMessageStatus messageMessageStatus = new MessageMessageStatus(Globals.myself().transaction_no, message_tr, MessageMessageStatus.MessageStatus.Read.ordinal() + "");
        Realm.databaseManager.insertObject(messageMessageStatus);

    }




    void sendMessage(String text) {
        outgoing_message = new message();
        outgoing_message.transaction_no = Globals.getTransactionNo();
        outgoing_message.sid = outgoing_message.transaction_no;
        outgoing_message.sync_status = sync_status.pending.ordinal() + "";
        outgoing_message.source = Globals.myself().transaction_no;
        outgoing_message.destination = partner.transaction_no;
        outgoing_message.message_type = message.MessageType.TextObject.ordinal() + "";
        outgoing_message.text = text;


        Realm.databaseManager.insertObject(outgoing_message);
        ChatApplication.rcso.upload("12");
        messageArrayList.clear();


        messageArrayList.addAll(Realm.databaseManager.loadObjectArray(message.class, conversation_query));
        binding.messageList.getAdapter().notifyDataSetChanged();
        binding.messageList.smoothScrollToPosition(messageArrayList.size() - 1);
    }





    @Override
    protected void onResume() {
        super.onResume();
        ChatApplication.attatchInterface(syncInterface);
//        new WindowInsetsControllerCompat(getWindow(),getWindow().getDecorView()).setAppearanceLightStatusBars(true);

    }

    SyncInterface syncInterface = new SyncInterface() {
        @Override
        public void onSyncCompleted(String service_id) {
            if (service_id.equalsIgnoreCase("11") || service_id.equalsIgnoreCase("12")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageArrayList.clear();
                        ArrayList<message> new_list=Realm.databaseManager.loadObjectArray(message.class, conversation_query);
                        if(new_list.size()>messageArrayList.size()){
                            messageArrayList.addAll(new_list);
                            binding.messageList.getAdapter().notifyDataSetChanged();
                            int msgSize=messageArrayList.size();
                            if(msgSize>1){
                                binding.messageList.smoothScrollToPosition(msgSize - 1);
                            }
                            readAllUnreadMessages();

                        }


                    }
                });
            } else if (service_id.equalsIgnoreCase("13") || service_id.equalsIgnoreCase("14")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageArrayList.clear();
                        messageArrayList.addAll(Realm.databaseManager.loadObjectArray(message.class, conversation_query));
                        binding.messageList.getAdapter().notifyDataSetChanged();

                    }
                });
            } else if (service_id.equalsIgnoreCase("15") || service_id.equalsIgnoreCase("16")) {
                check_online_status();

            }

        }
    };

    void check_online_status() {
        OnlineStatus partnerLastOnlineStatus = Realm.databaseManager.loadObject(OnlineStatus.class, "Select * from online_status where member_transaction_no='" + partner.transaction_no + "' ORDER BY reg_time DESC LIMIT 1");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (partnerLastOnlineStatus == null) {
                    binding.toolbar.onlineStatus.setText("***");
                    return;
                } else if (partnerLastOnlineStatus.online_status.equalsIgnoreCase(OnlineStatus.onlineStatus.offline.ordinal() + "")) {

                    binding.toolbar.onlineStatus.setText("Last seen: " + partnerLastOnlineStatus.reg_time);
                    return;
                } else if (partnerLastOnlineStatus.online_status.equalsIgnoreCase(OnlineStatus.onlineStatus.online.ordinal() + "")) {

                    binding.toolbar.onlineStatus.setText(findDifferenceinSeconds(partnerLastOnlineStatus.reg_time, Calendar.getInstance().getTime()) > 60 ? "Last seen: " + partnerLastOnlineStatus.reg_time : "online");
                    return;
                } else if (partnerLastOnlineStatus.online_status.equalsIgnoreCase(OnlineStatus.onlineStatus.typing.ordinal() + "")) {

                    binding.toolbar.onlineStatus.setText("typing ...");
                    return;
                } else if (partnerLastOnlineStatus.online_status.equalsIgnoreCase(OnlineStatus.onlineStatus.recording_audio.ordinal() + "")) {

                    binding.toolbar.onlineStatus.setText("recording audio ...");
                    return;
                }
            }
        });


    }


    static String logTag = "ConversationActivity";

    static String findDifference(Date d1, Date d2) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = svars.sdf_db_time;

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date


            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;
            long difference_In_Months = difference_In_Days / 30;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds
            StringBuilder sb = new StringBuilder();
            if (difference_In_Years >= 1) {
                sb.append(difference_In_Years
                        + " years, "
                        + difference_In_Months
                        + " months, "
                        + difference_In_Days
                        + " days, "
                        + difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");
            } else if (difference_In_Months >= 1) {
                sb.append(difference_In_Months
                        + " months, "
                        + difference_In_Days
                        + " days, "
                        + difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");
            } else if (difference_In_Days >= 1) {
                sb.append(difference_In_Days
                        + " days, "
                        + difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");
            } else if (difference_In_Hours >= 1) {
                sb.append(difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");
            } else if (difference_In_Minutes >= 1) {
                sb.append(difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");
            } else if (difference_In_Minutes >= 1) {
                sb.append(difference_In_Seconds
                        + " seconds");
            }
            sb.append(" ago");
            Log.e(logTag,
                    "Difference "
                            + "between two dates is: ");

            Log.e(logTag,
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static long findDifferenceinSeconds(String start_date, Date end_date) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = svars.sdf_db_time;


        try {
            Date d1 = sdf.parse(start_date);
            Date d2 = end_date;
            findDifference(d1, d2);
            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            long difference_In_Seconds
                    = (difference_In_Time
                    / (1000));

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60));
            Log.e(logTag, "Difference in seconds: " + difference_In_Seconds);
            return difference_In_Seconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    void populate() {

    }
}