package sparta.realm.cschat.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sparta.realm.cschat.activities.ConversationActivity;
import sparta.realm.cschat.Models.conversation;
import sparta.realm.cschat.R;


public class CommunicationPagesAdapter extends  RecyclerView.Adapter<CommunicationPagesAdapter.view> {

    Activity cntxt;
 public  ArrayList<ArrayList<conversation>> pages;
    AdapterView.OnItemClickListener listener;
    Boolean selectable=false;
    Boolean isList=true;
  public CommunicationPagesAdapter(Activity cntxt, ArrayList<ArrayList<conversation>> pages)
    {

       this.cntxt=cntxt;
        this.pages = pages;


    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cntxt).inflate( R.layout.fragment_conversations, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
      ArrayList<conversation> conversationsPage= pages.get(position);
      holder.id=position;
      holder.conversations.setAdapter(new ConversationsAdapter(conversationsPage, new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Intent convo_intent=new Intent(cntxt, ConversationActivity.class);
              convo_intent.putExtra("tr",conversationsPage.get(position).participant_tr);
             cntxt.startActivity(convo_intent);
          }
      }));





    }


    @Override
    public int getItemCount() {
        return pages.size();
    }

    public class view extends RecyclerView.ViewHolder {

      public RecyclerView conversations;
public int id;
View here;


        view(View itemView) {
            super(itemView);
            this.id=id;
            conversations=itemView.findViewById(R.id.conversationList);
            conversations.setLayoutManager(new LinearLayoutManager(cntxt));

            here=itemView;

        }

        void populate(){


        }


    }
}
