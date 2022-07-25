package sparta.realm.cschat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import com.realm.annotations.sync_status;

import java.util.ArrayList;
import java.util.List;

import sparta.realm.Realm;
import sparta.realm.cschat.Models.message;


public class MessageNotificationReceiver extends BroadcastReceiver {

    static List<Message> MESSAGES = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {
            CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
//            Person me = new Person.Builder().setName("Me").build();
            Message answer = new Message(replyText, "Me");
            MESSAGES.add(answer);
            message outgoing_message = new message();
            outgoing_message.transaction_no = Globals.getTransactionNo();
            outgoing_message.sid = outgoing_message.transaction_no;
            outgoing_message.sync_status = sync_status.pending.ordinal() + "";
            outgoing_message.source = Globals.myself().transaction_no;
            outgoing_message.destination = intent.getStringExtra("partner");
            outgoing_message.message_type = message.MessageType.TextObject.ordinal() + "";
            outgoing_message.text = replyText.toString();


            Realm.databaseManager.insertObject(outgoing_message);
            ChatApplication.rcso.upload("12");
            ChatApplication.sendChannel1Notification(context);
        }
    }
}
