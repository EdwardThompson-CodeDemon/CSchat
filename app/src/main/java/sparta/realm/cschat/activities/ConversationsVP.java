package sparta.realm.cschat.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.cschat.ChatApplication;
import sparta.realm.cschat.R;
import sparta.realm.cschat.SyncInterface;
import sparta.realm.cschat.activities.ui.main.SectionsPagerAdapter;
import sparta.realm.cschat.databinding.ActivityConversationsVpBinding;

public class ConversationsVP extends SpartaAppCompactActivity {

    private ActivityConversationsVpBinding binding;
    SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConversationsVpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.viewpagertab.setViewPager(binding.viewPager);

//         viewPagerTab.getTabAt(0).setPointerIcon(R.drawable.your_icon);


        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConversationsVP.this, ContactSearch.class));

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        ChatApplication.attatchInterface(syncInterface);


    }

    SyncInterface syncInterface = new SyncInterface() {
        @Override
        public void onSyncCompleted(String service_id) {
            if (service_id.equalsIgnoreCase("11") || service_id.equalsIgnoreCase("12") || service_id.equalsIgnoreCase("13") || service_id.equalsIgnoreCase("14")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sectionsPagerAdapter = new SectionsPagerAdapter(act, getSupportFragmentManager());
                        binding.viewPager.setAdapter(sectionsPagerAdapter);
                    }
                });
            }

        }
    };
}