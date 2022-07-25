package sparta.realm.cschat.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;


@DynamicClass(table_name = "conversations")
public class conversation extends RealmModel {
    public conversation(){

    }
    public conversation(String participant_tr,String last_message_tr,String unread_messages){
        this.participant_tr=participant_tr;
        this.last_message_tr=last_message_tr;
        this.unread_messages=unread_messages;
    }

     @DynamicProperty(json_key = "participant_tr")
    public String participant_tr;

    @DynamicProperty(json_key = "participant_name")
    public String participant_name;

    @DynamicProperty(json_key = "last_message_tr")
    public String last_message_tr;

    @DynamicProperty(json_key = "unread_messages")
    public String unread_messages="";




}
