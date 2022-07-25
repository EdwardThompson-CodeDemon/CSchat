package sparta.realm.cschat.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;

@DynamicClass(table_name = "games")
//@SyncDescription(service_id = "5", service_name = "Games", service_type = Download, chunk_size = 50000)
//@SyncDescription(service_id = "6", service_name = "Games", service_type = Upload, chunk_size = 50000)
public class Event extends RealmModel {
    public Event() {

    }

    public Event(String name, String creator_name, String scheduled_start_time) {
        this.name = name;
        this.creator_name = creator_name;
        this.scheduled_start_time = scheduled_start_time;
    }

    public Event(String name, String creator_name, String scheduled_start_time, String start_time) {
        this.name = name;
        this.creator_name = creator_name;
        this.scheduled_start_time = scheduled_start_time;
        this.start_time = start_time;
    }

    @DynamicProperty(json_key = "name")
    public String name;

    @DynamicProperty(json_key = "creator_name")
    public String creator_name;

    @DynamicProperty(json_key = "creator_id")
    public String creator_id = "";

    @DynamicProperty(json_key = "scheduled_start_time")
    public String scheduled_start_time;

    @DynamicProperty(json_key = "start_time")
    public String start_time;

    @DynamicProperty(json_key = "msid")
    public String msid;


}
