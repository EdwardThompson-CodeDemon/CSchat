package sparta.realm.cschat.activities.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import sparta.realm.Realm;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.conversation;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Hello world from section: " + input;
        }
    });

 private LiveData<ArrayList> mList = Transformations.map(mIndex, new Function<Integer, ArrayList>() {
        @Override
        public ArrayList apply(Integer input) {
switch(input){
    case 1:
        return Realm.databaseManager.loadObjectArray(conversation.class,"SELECT mit.name AS participant_name,mit.transaction_no AS participant_tr,m.transaction_no AS last_message_tr from member_info_table mit inner JOIN messages m on ((m.source='"+ Globals.myself().transaction_no +"' and m.destination=mit.transaction_no) or (m.destination='"+ Globals.myself().transaction_no +"' and m.source=mit.transaction_no)) where mit.transaction_no<>'"+ Globals.myself().transaction_no +"' group by mit.transaction_no ");


            }
            return new ArrayList<>();
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
   public LiveData<ArrayList> getLists() {
        return mList;
    }
}