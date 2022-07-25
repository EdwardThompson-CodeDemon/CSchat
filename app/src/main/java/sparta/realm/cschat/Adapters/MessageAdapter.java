package sparta.realm.cschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.realm.annotations.sync_status;

import java.util.ArrayList;

import sparta.realm.Realm;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.MessageMessageStatus;
import sparta.realm.cschat.Models.message;
import sparta.realm.cschat.R;


import static android.view.LayoutInflater.from;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private RecyclerView manager;
    private Context context;
    ArrayList<message> items;
    //    AdapterView.OnItemClickListener listener;
    Member myself = Globals.myself();

    public interface MessageActionListener {
        void onMessageClicked(int position, message m);

        default void onMessageGameInviteAccepted(String game_invite_transaction_no) {


        }

        default void onMessageGameInviteDeclined(String game_invite_transaction_no) {

        }

    }

    MessageActionListener messageActionListener;

    public MessageAdapter(ArrayList<message> items, MessageActionListener messageActionListener) {


        this.items = items;
        this.messageActionListener = messageActionListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        inflater = from(context);
        View v = inflater.inflate(R.layout.item_conversation_message, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((Holder) holder).populate(position);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView text, time;
        ImageView read_status_icon;
        ConstraintLayout main, game_invite_lay;

        /////Event invite//////////
        TextView gameName, gameScheduledStartTime;
        Button accept, decline;

        public int position;

        Holder(View itemView) {
            super(itemView);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            gameScheduledStartTime = itemView.findViewById(R.id.game_scheduled_start_time);
            gameName = itemView.findViewById(R.id.game_name);
            game_invite_lay = itemView.findViewById(R.id.game_invite_lay);
            main = itemView.findViewById(R.id.main);
            text = (TextView) itemView.findViewById(R.id.text);
            time = (TextView) itemView.findViewById(R.id.index);
            read_status_icon = itemView.findViewById(R.id.read_status_icon);
            accept.setOnClickListener(v -> messageActionListener.onMessageGameInviteAccepted(items.get(position).game_invite_tr));
            decline.setOnClickListener(v -> messageActionListener.onMessageGameInviteDeclined(items.get(position).game_invite_tr));
            itemView.setOnClickListener(v -> messageActionListener.onMessageClicked(position, items.get(position)));
        }

        public void populate(int position) {
            this.position = position;
            message m = items.get(position);
            final float scale = context.getResources().getDisplayMetrics().density;

            int padding_20_in_pixels = (int) Math.round(24 * scale);
            int padding_top_in_pixels = (int) Math.round(4 * scale);
            int padding_bottom_in_pixels = (int) Math.round(8 * scale);
            int padding_10_in_pixels = (int) Math.round(12 * scale);
            int margin_left = (int) Math.round(64 * scale);
            if (myself.transaction_no.equalsIgnoreCase(m.source)) {
                main.setBackground(context.getDrawable(R.drawable.shape_bg_outgoing_bubble_v2));
                main.setPadding(padding_10_in_pixels, padding_top_in_pixels, padding_20_in_pixels, padding_bottom_in_pixels);

                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                parms.setMargins(margin_left, 0, 0, 0);
                parms.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);


                main.setLayoutParams(parms);
                read_status_icon.setVisibility(View.VISIBLE);
                boolean read = Realm.databaseManager.getRecordCount(MessageMessageStatus.class, "message_tr='" + m.transaction_no + "'", "message_status='" + MessageMessageStatus.MessageStatus.Read.ordinal() + "'") > 0;//if read
                if (read) {
                    read_status_icon.setImageDrawable(context.getDrawable(R.drawable.double_tick_50px));
                    read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else if (Realm.databaseManager.getRecordCount(MessageMessageStatus.class, "message_tr='" + m.transaction_no + "'", "message_status='" + MessageMessageStatus.MessageStatus.Delivered.ordinal() + "'") > 0) {//if delivered
                    read_status_icon.setImageDrawable(context.getDrawable(R.drawable.double_tick_50px));
                    read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                } else if (Integer.parseInt(m.sync_status) == sync_status.syned.ordinal()) {//if uploaded
                    read_status_icon.setImageDrawable(context.getDrawable(R.drawable.ic_single_tick));
                    read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                } else {//pending on phone
                    read_status_icon.setImageDrawable(context.getDrawable(R.drawable.ic_pending));
                    read_status_icon.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                }

            } else {
                main.setBackground(context.getDrawable(R.drawable.shape_bg_incoming_bubble_v2));
                main.setPadding(padding_20_in_pixels, padding_top_in_pixels, padding_10_in_pixels, padding_bottom_in_pixels);
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                parms.setMargins(0, 0, margin_left, 0);
                parms.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                main.setLayoutParams(parms);
                read_status_icon.setVisibility(View.GONE);

            }



                game_invite_lay.setVisibility(View.GONE);


            text.setText(m.text);

            time.setText(m.reg_time);

        }


    }

}
