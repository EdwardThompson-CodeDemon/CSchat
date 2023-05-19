package sparta.realm.cschat.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.realm.annotations.sync_status;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.Event;
import sparta.realm.cschat.Models.MemberImage;
import sparta.realm.cschat.Models.MessageMessageStatus;
import sparta.realm.cschat.Models.conversation;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.message;
import sparta.realm.cschat.R;
import sparta.realm.spartautils.svars;


import static android.view.LayoutInflater.from;

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private RecyclerView manager;
    private Context context;
    ArrayList<conversation> items;
    AdapterView.OnItemClickListener listener;
    Member myself;
 public ConversationsAdapter(ArrayList<conversation> conversations, AdapterView.OnItemClickListener listener) {
     this.items  = conversations;
     this.listener  = listener;
     myself= Globals.myself();
 }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        inflater = from(context);
        View v = inflater.inflate(R.layout.item_chat_conversation, parent, false);
        return new Holder(v);
 }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

     ((Holder)holder).populate(position);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView participant,last_message,time,unread_messages;
        CircleImageView participant_image;
        ImageView read_status_icon,file_type_icon;

        public int position;
        Holder(View itemView) {
            super(itemView);
            read_status_icon =  itemView.findViewById(R.id.readStatusIcon);
            file_type_icon =  itemView.findViewById(R.id.messageTypeIcon);
            participant = (TextView) itemView.findViewById(R.id.participant_name);
            last_message = (TextView) itemView.findViewById(R.id.last_message);
            time = (TextView) itemView.findViewById(R.id.index);
            unread_messages = (TextView) itemView.findViewById(R.id.unread_messages);
            participant_image =  itemView.findViewById(R.id.participant_image);
            file_type_icon.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
//                    manager.smoothScrollToPosition(adapterPosition);
                    listener.onItemClick(null,itemView,position,0);
//                    listener.onItemClick(null,itemView,position,Long.parseLong(items.get(position).sid));

                }
            });
        }

        public void populate(int position){
            this.position = position;
            conversation c = items.get(position);
             participant.setText(c.participant_name);
            try {
                MemberImage profile_photo = Realm.databaseManager.loadObject(MemberImage.class, new Query().setTableFilters("member_transaction_no='" + c.participant_tr + "'"));

                participant_image.setImageURI(Uri.parse(Uri.parse(svars.current_app_config(context).file_path_employee_data) + profile_photo.image));

            } catch (Exception ex) {

            }
            message last_message = Realm.databaseManager.loadObject(message.class, "select * from messages m where (m.source='" + c.participant_tr + "' OR m.destination='" + c.participant_tr + "') AND (m.source='" + myself.transaction_no + "' OR m.destination='" + myself.transaction_no + "') order by m.reg_time DESC LIMIT 1 ");
                 try{
                if (last_message.message_type != null && last_message.message_type.equalsIgnoreCase(message.MessageType.Audio.ordinal() + "")) {

                    this.last_message.setText("Audio  " + last_message.audio_length);
                } else {

                    this.last_message.setText(last_message.text);

                }
            }catch (NullPointerException ex){}
            if (last_message != null) {
                this.time.setText(last_message.reg_time);
                if (last_message.source.equalsIgnoreCase(myself.transaction_no)) {
                    read_status_icon.setVisibility(View.VISIBLE);
                    boolean read = Realm.databaseManager.getRecordCount(MessageMessageStatus.class, "message_tr='" + last_message.transaction_no + "'", "message_status='" + MessageMessageStatus.MessageStatus.Read.ordinal() + "'") > 0;//if read
                    if (read) {
                        read_status_icon.setImageDrawable(context.getDrawable(R.drawable.double_tick_50px));
                        read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                    } else if (Realm.databaseManager.getRecordCount(MessageMessageStatus.class, "message_tr='" + last_message.transaction_no + "'", "message_status='" + MessageMessageStatus.MessageStatus.Delivered.ordinal() + "'") > 0) {//if delivered
                        read_status_icon.setImageDrawable(context.getDrawable(R.drawable.double_tick_50px));
                        read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                    } else if (Integer.parseInt(last_message.sync_status) == sync_status.syned.ordinal()) {//if uploaded
                        read_status_icon.setImageDrawable(context.getDrawable(R.drawable.ic_single_tick));
                        read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                    } else {//pending on phone
                        read_status_icon.setImageDrawable(context.getDrawable(R.drawable.ic_pending));
                        read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                    }


                    unread_messages.setVisibility(View.GONE);
                } else {
                    read_status_icon.setVisibility(View.GONE);

                    int unreadMessages = Realm.databaseManager.getRecordCount(message.class, "source='" + last_message.source + "'", "destination='" + myself.transaction_no + "'", "transaction_no not in(select message_tr from message_message_status WHERE member_tr='" + Globals.myself().transaction_no + "' AND message_status='" + MessageMessageStatus.MessageStatus.Read.ordinal() + "')");
                    unread_messages.setText(unreadMessages + "");
                    if (unreadMessages > 0) {
                        unread_messages.setVisibility(View.VISIBLE);

                    } else {
                        unread_messages.setVisibility(View.GONE);


                    }
                }
            }
//            this.time.setText(last_message.reg_time);


        }



        @Override
        public void onClick(View v) {
listener.onItemClick(null,itemView,position,Long.parseLong(items.get(position).sid));
        }
    }

}
