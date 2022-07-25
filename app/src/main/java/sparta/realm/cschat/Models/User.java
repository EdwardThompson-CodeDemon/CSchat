package sparta.realm.cschat.Models;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import java.io.Serializable;

import sparta.realm.cschat.Globals;


@DynamicClass(table_name = "user_table")
//@SyncDescription(service_name = "Users",service_type = SyncDescription.service_type.Download,chunk_size = 100,storage_mode_check = true)
//@SyncDescription(service_name = "Users",upload_link = "/Authentication/Login/AddIndividualUser",is_ok_position = "JO:isOkay",service_type = SyncDescription.service_type.Upload,chunk_size = 100,storage_mode_check = true)
public class User extends RealmModel implements Serializable {

@DynamicProperty(json_key = "member_transaction_no")
public String member_transaction_no;

@DynamicProperty(json_key = "member_id")
 public String member_id="";
@DynamicProperty(json_key = "member_no")
 public String member_no="";

@DynamicProperty(json_key = "username")
 public String username="";


@DynamicProperty(json_key = "password")
 public String password="";


@DynamicProperty(json_key = "msid")
public String msid="";




    public User(String member_transaction_no, String member_id, String username, String password)
    {
        this.member_transaction_no=member_transaction_no;
        this.member_id=member_id;
        this.username=username;
        this.password=password;
        this.transaction_no= Globals.getTransactionNo();
        this.sync_status= com.realm.annotations.sync_status.pending.ordinal()+"";

    }
  public User(String member_no, String username, String password)
    {
        this.member_no=member_no;
        this.username=username;
        this.password=password;
        this.transaction_no= Globals.getTransactionNo();
        this.sync_status= com.realm.annotations.sync_status.syned.ordinal()+"";

    }
public User()
    {


    }

}
