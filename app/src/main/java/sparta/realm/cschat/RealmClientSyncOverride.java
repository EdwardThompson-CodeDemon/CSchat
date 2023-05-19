package sparta.realm.cschat;

import android.content.Intent;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import java.util.List;

import sparta.realm.Realm;
import sparta.realm.RealmClientCallbackInterface;
import sparta.realm.realmclient.RealmClientServiceManager;
import sparta.realm.spartautils.svars;

import static sparta.realm.realmclient.RealmClientServiceManager.convertImplicitIntentToExplicitIntent;

public class RealmClientSyncOverride extends RealmClientCallbackInterface.Stub {

    public static RealmClientServiceManager rcsm;
public String serverAddress="20.216.187.240";
public String serverAddress__="";
public String serverAddress2__="𓀴";
//public int serverPort=8891;//v1 server
public int serverPort=8892;//v2 server


    public void Initialize(Stub vs){
      if(rcsm==null){
          new Thread(new Runnable() {
              @Override
              public void run() {

                  Intent rci = new Intent("sparta.realm.RealmClientInterface");
                  Realm.context.stopService(convertImplicitIntentToExplicitIntent(rci, Realm.context));
                  rcsm=new RealmClientServiceManager(vs, svars.device_code(Realm.context),serverAddress,serverPort,"demo","demo123");

              }
          }).start();

      }


    }
    public void attatchListener(Stub listener ){
        rcsm.registerCallback(listener);
    }
   public void sync(){
      new Thread(new Runnable() {
          @Override
          public void run() {
              rcsm.Synchronize();
          }
      }).start();

    }
public void download(String service_id){
    rcsm.download(service_id);

}
   public void upload(String service_id){
      new Thread(new Runnable() {
          @Override
         public void run() {
              if(rcsm!=null){
                  rcsm.upload(service_id);
              }

          }
      }).start();

}
    public void downloadAll(){
    rcsm.downloadAll();

}
    public void uploadAll(){
        rcsm.uploadAll();

    }

    @Override
    public void on_status_changed(String s) throws RemoteException {

    }

    @Override
    public void on_info_updated(String s) throws RemoteException {

    }

    @Override
    public void on_main_percentage_changed(int i) throws RemoteException {

    }

    @Override
    public void on_secondary_progress_changed(int i) throws RemoteException {

    }

    @Override
    public void onSynchronizationBegun() throws RemoteException {

    }

    @Override
    public void onServiceSynchronizationCompleted(String service_id) throws RemoteException {

    }




    @Override
    public void onSynchronizationCompleted() throws RemoteException {

    }

    @Override
    public List<String> OnAboutToUploadObjects(String s, List<String> list) throws RemoteException {
        return list;
    }

    @Override
    public String OnAboutToDownloadObjects(String s) throws RemoteException {
        return null;
    }

    @Override
    public String OnDownloadedObjects(String s, String s1) throws RemoteException {
        return null;
    }

    @Override
    public ParcelFileDescriptor OnDownloadedData(String s, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {
        return null;
    }


}
