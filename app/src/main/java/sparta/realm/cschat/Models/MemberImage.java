package sparta.realm.cschat.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;

import sparta.realm.cschat.Globals;


@DynamicClass(table_name = "member_image_table")
@SyncDescription(service_id = "3", service_name = "Member image", service_type = SyncDescription.service_type.Download, chunk_size = 100, storage_mode_check = true)
@SyncDescription(service_id = "4", service_name = "Member image", service_type = SyncDescription.service_type.Upload, chunk_size = 100, storage_mode_check = true)
public class MemberImage extends RealmModel implements Serializable {

    @DynamicProperty(json_key = "member_transaction_no")
    public String member_transaction_no;

    @DynamicProperty(json_key = "member_id")
    public String member_id = "";

    @DynamicProperty(json_key = "image", storage_mode = DynamicProperty.storage_mode.FilePath)
    public String image = "";


    @DynamicProperty(json_key = "msid")
    public String msid = "";


    public MemberImage() {
        this.transaction_no = Globals.getTransactionNo();
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";

    }

}
