package sparta.realm.cschat.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.sync_status;


import sparta.realm.cschat.Globals;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;

@DynamicClass(table_name = "online_status")
@SyncDescription(service_id = "15", service_name = "Online status", service_type = Download, chunk_size = 1000)
@SyncDescription(service_id = "16", service_name = "Online status", service_type = Upload, chunk_size = 1000)
public class OnlineStatus extends RealmModel {

    public enum onlineStatus {
        Unknown,
        offline,
        online,
        typing,
        recording_audio,

    }

    public OnlineStatus() {

    }

    public OnlineStatus(String member, onlineStatus online_status) {
        this.member_transaction_no = member;
        this.online_status = online_status.ordinal() + "";
        this.transaction_no = Globals.getTransactionNo();
        this.sid = this.transaction_no;
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";
    }

   public OnlineStatus(String member, onlineStatus online_status,String target_member_transaction_no) {
        this.member_transaction_no = member;
        this.target_member_transaction_no = target_member_transaction_no;
        this.online_status = online_status.ordinal() + "";
        this.transaction_no = Globals.getTransactionNo();
        this.sid = this.transaction_no;
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";
    }

    @DynamicProperty(json_key = "member_transaction_no")
    public String member_transaction_no;

    @DynamicProperty(json_key = "target_member_transaction_no")
    public String target_member_transaction_no;


    @DynamicProperty(json_key = "online_status")
    public String online_status;


}
