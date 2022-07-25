package sparta.realm.cschat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

import sparta.realm.Realm;
import sparta.realm.cschat.Adapters.CommunicationPagesAdapter;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.conversation;
import sparta.realm.cschat.R;
import sparta.realm.cschat.SyncInterface;
import sparta.realm.cschat.ChatApplication;
import sparta.realm.cschat.databinding.ActivityCommunicationBinding;


public class Communication extends AppCompatActivity {

    private ActivityCommunicationBinding binding;
    ViewPager2 viewPager2;
    ArrayList<conversation> conversations;
    ArrayList<ArrayList<conversation>> conversation_pages = new ArrayList<>();
    boolean created = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCommunicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewPager2 = binding.viewPager2;
        conversations = Realm.databaseManager.loadObjectArray(conversation.class, "SELECT mit.name AS participant_name,mit.transaction_no AS participant_tr,m.transaction_no AS last_message_tr from member_info_table mit inner JOIN messages m on ((m.source='" + Globals.myself().transaction_no + "' and m.destination=mit.transaction_no) or (m.destination='" + Globals.myself().transaction_no + "' and m.source=mit.transaction_no)) where mit.transaction_no<>'" + Globals.myself().transaction_no + "' group by mit.transaction_no ");
// ArrayList<conversation> conversations= Realm.databaseManager.loadObjectArray(conversation.class,"SELECT mit.name AS participant_name,\n" +
//         "mit.transaction_no AS participant_tr,\n" +
//         "m.transaction_no AS last_message_tr,\n" +
//         "m.text AS last_message_text\n" +
//         " from messages m\n" +
//         "LEFT JOIN member_info_table mit \n" +
//         "on m.source='"+Globals.myself().transaction_no+"' \n" +
//         "OR m.destination='"+Globals.myself().transaction_no+"' \n" +
//         "where mit.transaction_no<>'"+Globals.myself().transaction_no+"' \n" +
//         "order by m.reg_time DESC LIMIT 1");

        conversation_pages.add(conversations);
//   conversation_pages.add(conversations);

        viewPager2.setAdapter(new CommunicationPagesAdapter(this, conversation_pages));

//        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
//        viewPagerTab.setViewPager(viewPager2);

//        TabLayoutMediator(tabLayout, viewPager2) {
//        tab, position ->  viewPager2.setCurrentItem(tab.position, true)             if (position == 1)                tab.setText("Test2")            if (position == 0)                 tab.setText("Test1")         }.attach()

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Communication.this, ContactSearch.class));
            }
        });
        created = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        ChatApplication.attatchInterface(syncInterface);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);
        if (created) {
            conversations = Realm.databaseManager.loadObjectArray(conversation.class, "SELECT mit.name AS participant_name,mit.transaction_no AS participant_tr,m.transaction_no AS last_message_tr from member_info_table mit inner JOIN messages m on ((m.source='" + Globals.myself().transaction_no + "' and m.destination=mit.transaction_no) or (m.destination='" + Globals.myself().transaction_no + "' and m.source=mit.transaction_no)) where mit.transaction_no<>'" + Globals.myself().transaction_no + "' group by mit.transaction_no ");
            viewPager2.setAdapter(new CommunicationPagesAdapter(this, conversation_pages));
        }

    }

    void resetPages() {
        ArrayList<conversation> conversations = Realm.databaseManager.loadObjectArray(conversation.class, "SELECT mit.name AS participant_name,mit.transaction_no AS participant_tr,m.transaction_no AS last_message_tr from member_info_table mit inner JOIN messages m on ((m.source='" + Globals.myself().transaction_no + "' and m.destination=mit.transaction_no) or (m.destination='" + Globals.myself().transaction_no + "' and m.source=mit.transaction_no)) where mit.transaction_no<>'" + Globals.myself().transaction_no + "' group by mit.transaction_no ");
        ArrayList<ArrayList<conversation>> conversation_pages = new ArrayList<>();
        conversation_pages.add(conversations);
        conversation_pages.add(conversations);

        viewPager2.setAdapter(new CommunicationPagesAdapter(this, conversation_pages));
    }

    SyncInterface syncInterface = new SyncInterface() {
        @Override
        public void onSyncCompleted(String service_id) {
            if (service_id.equalsIgnoreCase("11") || service_id.equalsIgnoreCase("12")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetPages();

                    }
                });
            } else if (service_id.equalsIgnoreCase("13") || service_id.equalsIgnoreCase("14")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetPages();
                    }
                });
            }

        }
    };
}