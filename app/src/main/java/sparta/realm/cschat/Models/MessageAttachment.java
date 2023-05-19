package sparta.realm.cschat.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;

import sparta.realm.cschat.Globals;


@DynamicClass(table_name = "message_attachment")
@SyncDescription(service_id = "17", service_name = "Message attachment", service_type = SyncDescription.service_type.Download, chunk_size = 100, storage_mode_check = true)
@SyncDescription(service_id = "18", service_name = "Message attachment", service_type = SyncDescription.service_type.Upload, storage_mode_check = true)
public class MessageAttachment extends RealmModel implements Serializable {

    @DynamicProperty(json_key = "message_transaction_no")
    public String message_transaction_no;

    @DynamicProperty(json_key = "attachment_type")
    public String attachment_type;

    @DynamicProperty(json_key = "attachment_size")
    public String attachment_size;

    @DynamicProperty(json_key = "attachment", storage_mode = DynamicProperty.storage_mode.FilePath)
    public String attachment;


    @DynamicProperty(json_key = "msid")
    public String msid = "";


    public MessageAttachment() {
        this.transaction_no = Globals.getTransactionNo();
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
