package sparta.realm.cschat.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.sync_status;


import sparta.realm.cschat.Globals;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;

@DynamicClass(table_name = "message_message_status")
@SyncDescription(service_id="13",service_name = "Message status",service_type = Download,chunk_size = 50000)
@SyncDescription(service_id="14",service_name = "Message status",service_type = Upload,chunk_size = 50000)
public class MessageMessageStatus extends RealmModel {

    public enum MessageStatus{
        Pending,
        Sent ,
        Delivered,
        Read
    }
    public MessageMessageStatus(){

    }
 public MessageMessageStatus(String member,String message,String message_status){
this.member_tr=member;
this.message_tr=message;
this.message_status=message_status;

     this.transaction_no = Globals.getTransactionNo();
     this.sid = this.transaction_no;
     this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";
    }

    @DynamicProperty(json_key = "member_tr")
    public String member_tr;

   @DynamicProperty(json_key = "message_tr")
    public String message_tr;

 @DynamicProperty(json_key = "message_status")
    public String message_status;



}
