package sparta.realm.cschat;

public interface SyncInterface {

    default void onSyncCompleted(String service_id){

    }
    default void onInfoUpdated(String info){

    }
}
