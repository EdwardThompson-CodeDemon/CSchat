package sparta.realm.cschat.activities.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import sparta.realm.cschat.Adapters.ConversationsAdapter;
import sparta.realm.cschat.Models.conversation;
import sparta.realm.cschat.activities.ConversationActivity;
import sparta.realm.cschat.databinding.FragmentConversationsVBinding;
import sparta.realm.cschat.utils.ui.MessageNotification;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentConversationsVBinding binding;
    MessageNotification messageNotification = new MessageNotification();

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentConversationsVBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.sectionLabel;
        binding.conversationList.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
       pageViewModel.getLists().observe(getViewLifecycleOwner(), new Observer<ArrayList>() {
           @Override
           public void onChanged(ArrayList arrayList) {
               binding.conversationList.setAdapter(new ConversationsAdapter(arrayList, new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       ArrayList<conversation> convos=arrayList;
                       Intent convo_intent=new Intent(getContext(), ConversationActivity.class);
                       convo_intent.putExtra("tr",convos.get(position).participant_tr);
                       getContext().startActivity(convo_intent);
                   }
               }));
           }
       });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}