package sparta.realm.cschat.Models;

import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;
import com.realm.annotations.SyncDescription;

import static com.realm.annotations.SyncDescription.service_type.Download;
import static com.realm.annotations.SyncDescription.service_type.Upload;

@DynamicClass(table_name = "messages")
@SyncDescription(service_id = "11", service_name = "Messages", service_type = Download, chunk_size = 50000,storage_mode_check = true)
@SyncDescription(service_id = "12", service_name = "Messages", service_type = Upload, chunk_size = 50000,storage_mode_check = true)
public class message extends RealmModel {
    public message() {

    }

    public enum MessageType {
        Unavailable,
        TextObject,
        Audio

    }

    @DynamicProperty(json_key = "source")
    public String source;

    @DynamicProperty(json_key = "destination")
    public String destination;

    @DynamicProperty(json_key = "reply_to_tr")
    public String reply_to_tr = "";


    @DynamicProperty(json_key = "server_creation_time")
    public String server_creation_time;

    @DynamicProperty(json_key = "text")
    public String text;


    @DynamicProperty(json_key = "message_type")
    public String message_type;

    @DynamicProperty(json_key = "game_invite_tr")
    public String game_invite_tr;

    @DynamicProperty(json_key = "file", storage_mode = DynamicProperty.storage_mode.FilePath)
    public String file;


    @DynamicProperty(json_key = "file_type")
    public String file_type;

   @DynamicProperty(json_key = "audio_length")
    public String audio_length;




}
