package sparta.realm.cschat.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.cschat.Adapters.members_adapter;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.databinding.ActivityContactSearchBinding;


public class ContactSearch extends AppCompatActivity {
    private ActivityContactSearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactSearchBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());//1221
        binding.contactList.setLayoutManager(new LinearLayoutManager(this));
        String search_tearm="";
        String tablefilter="(UPPER(name) LIKE '%" + search_tearm + "%' OR UPPER(email) LIKE '%" + search_tearm + "%' OR UPPER(sid) LIKE '%" + search_tearm + "%')";

        String tablefilter_exclude_myself="transaction_no <> '" + Globals.myself().transaction_no + "'";

        binding.contactList.setAdapter(new members_adapter(this, Realm.databaseManager.loadObjectArray(Member.class, new Query().setTableFilters(new String[]{tablefilter,tablefilter_exclude_myself})), new members_adapter.onItemClickListener() {
            @Override
            public void onItemClick(Member mem) {
                Intent convo_intent=new Intent(ContactSearch.this,ConversationActivity.class);
                convo_intent.putExtra("tr",mem.transaction_no);
                startActivity(convo_intent);
            }
        }));



    }
}