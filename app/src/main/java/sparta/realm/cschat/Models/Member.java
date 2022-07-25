package sparta.realm.cschat.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;

import sparta.realm.cschat.Globals;


@DynamicClass(table_name = "member_info_table")
@SyncDescription(service_id = "1", service_name = "Member", service_type = SyncDescription.service_type.Download)
@SyncDescription(service_id = "2", service_name = "Member", service_type = SyncDescription.service_type.Upload)
public class Member extends RealmModel implements Serializable {

    @DynamicProperty(json_key = "name")
    public String name;

    @DynamicProperty(json_key = "phone")
    public String phone = "";

    @DynamicProperty(json_key = "email")
    public String email = "";

    @DynamicProperty(json_key = "image")
    public String image = "";


    @DynamicProperty(json_key = "msid")
    public String msid = "";




    public MemberImage profile_photo = null;


    public Member() {
        this.transaction_no = Globals.getTransactionNo();
        this.sync_status = com.realm.annotations.sync_status.pending.ordinal() + "";


    }

}
