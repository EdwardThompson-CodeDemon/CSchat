package sparta.realm.cschat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.cschat.ChatApplication;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.Models.MemberImage;
import sparta.realm.cschat.databinding.ActivityUserSignupBinding;
import sparta.realm.spartautils.s_bitmap_handler;
import sparta.realm.spartautils.svars;

public class Signup extends SpartaAppCompactActivity {
    ActivityUserSignupBinding binding;
    MemberImage my_photo;
    Member currentMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String memberEmail = getIntent().getStringExtra("email");
        currentMember = Realm.databaseManager.loadObject(Member.class, new Query().setTableFilters("UPPER(email)='" + memberEmail.toUpperCase() + "'"));
//        String input=\;
//        String output=input.replace("\n","").replace("\\","").replace("\\","");
//        Log.e(logTag,output);
        MemberImage memberImage;
        if (currentMember != null) {
            memberImage = Realm.databaseManager.loadObject(MemberImage.class, new Query().setTableFilters("member_transaction_no='" + currentMember.transaction_no + "'"));
            currentMember.profile_photo = memberImage;
        }
        initUI();
        currentMember = new Member();
        currentMember.email = memberEmail;
    }

    void initUI() {
//        myself = Globals.myself();
        my_photo = new MemberImage();
        binding.loadingBar.setVisibility(View.GONE);
        binding.participantImage.setOnClickListener(v -> {
            svars.set_photo_camera_type(act, 1, 5);
//            Intent intt = new Intent(act, sparta.realm.spartautils.biometrics.face.SpartaFaceCamera.class);
//            intt.putExtra("sid", my_photo.transaction_no);
//            intt.putExtra("camera_index", 1);
//            intt.putExtra("camera_rotation", 90);
//            startActivityForResult(intt, 1);
            take_photo(1, "1");

        });

        binding.signin.setOnClickListener(v -> {
            currentMember.name = binding.displayName.getText().toString();
            if (validated()) {

                binding.loadingBar.setVisibility(View.VISIBLE);
                signupMember(currentMember.sync_var == null);
                Globals.set_myself(currentMember);
                start_activity(new Intent(act, Communication.class));
                finish();
            }

        });

//        boolean exists = confirmation_code.trim().equalsIgnoreCase(this.confirmation_code);
        if (currentMember != null) {
            populate();
        }

    }

    void populate() {
        binding.displayName.setText(currentMember.name);
        try {

            binding.participantImage.setImageURI(Uri.parse(Uri.parse(svars.current_app_config(this).file_path_employee_data) + currentMember.profile_photo.image));
            binding.participantImage.setColorFilter(null);
        } catch (Exception ex) {

        }

    }

    void signupMember(Boolean newMember) {
        if (newMember) {
            Realm.databaseManager.insertObject(currentMember);
            ChatApplication.rcso.upload("2");
        }
        Realm.databaseManager.insertObject(currentMember.profile_photo);
        ChatApplication.rcso.upload("4");
    }

    boolean validated() {
        boolean ok = true;
//        set_conditional_input_error2(ok, binding.nameEdt, "Invalid Name", binding.nameEdt.getText().toString().trim(), 3);
        set_conditional_input_error2(ok, binding.displayName, "Invalid display name", binding.displayName.getText().toString().trim(), 3);


        if (my_photo == null || my_photo.image == null) {
            ok = false;
            binding.participantImage.setColorFilter(Color.RED);
            Toast.makeText(act, "Photo hasn't been taken", Toast.LENGTH_SHORT).show();


        } else {
            binding.participantImage.setColorFilter(null);

        }


//        Member u = Realm.databaseManager.loadObject(Member.class, new Query().setTableFilters("username='" + currentMember.email + "'"));
//        if (u != null) {
//            ok = false;
//            Toast.makeText(act, "Username exists", Toast.LENGTH_SHORT).show();
//            binding.d.setError("Username exists");
//        }
        return ok;
    }

    protected boolean set_conditional_input_error2(boolean valid, EditText edt, String error, String input, int min_length) {
        if (input == null || input.length() < min_length) {
            try {
                edt.setError(error, error_drawable);
                edt.requestFocus();
            } catch (Exception ex) {
            }
            if (error != null) {
                Toast.makeText(act, error, Toast.LENGTH_LONG).show();

            }
            valid = false;
            return valid;
        } else {
            edt.setError(null);

        }

        return valid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        currentMember.profile_photo = new MemberImage();
        currentMember.profile_photo.image = data.getStringExtra("ImageUrl");
        currentMember.profile_photo.member_transaction_no = currentMember.transaction_no;

        try {
            binding.participantImage.setImageURI(Uri.parse(Uri.parse(svars.current_app_config(this).file_path_employee_data) + currentMember.profile_photo.image));
            binding.participantImage.setColorFilter(null);
        } catch (Exception ex) {

        }
// try {
//     JSONObject jo=new JSONObject();
//     JSONArray arr=new JSONArray();
//     jo.put("img","RE_DAT1655906145061_IMG.JPG");
//     arr.put(jo);
//     String base64=get_saved_doc_base64("RE_DAT1655906145061_IMG.JPG");
//     jo.put("img",base64);
////     String base64=get_saved_doc_base64(currentMember.profile_photo.image);
//     byte[] img=Base64.decode(jo.getString("img"),0);
//     Bitmap b=BitmapFactory.decodeByteArray(img,0,img.length);
////            binding.participantImage.setImageBitmap(b);
//            binding.participantImage.setColorFilter(null);
////     save_app_image(b);
//     img=Base64.decode( get_saved_doc_base64(save_app_image2(getBytes_JPG(b))),0);
//      b=BitmapFactory.decodeByteArray(img,0,img.length);
//     binding.participantImage.setImageBitmap(b);
//     binding.participantImage.setColorFilter(null);
//
//
//
// } catch (Exception ex) {
//
//        }
    }

    public static byte[] getBytes_JPG(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static String save_app_image2(byte[] fpb) {
        String img_name = "RE_DAT" + System.currentTimeMillis() + "_IMG.JPG";

        File file = new File(svars.current_app_config(Realm.context).file_path_employee_data);
        if (!file.exists()) {
            Log.e(logTag, "Creating data dir: " + (file.mkdirs() ? "Successfully created" : "Failed to create !"));
        }
        file = new File(svars.current_app_config(Realm.context).file_path_employee_data, img_name);

        try (OutputStream fOutputStream = new FileOutputStream(file)) {


            fOutputStream.write(fpb);
//            fOutputStream.close();

            //  MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            return "--------------";
        }
        return img_name;
    }

    public String get_saved_doc_base64(String data_name) {
        String res = "";
        try {
            res = Base64.encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(new File(svars.current_app_config(act).file_path_employee_data, data_name)), 0);
            return res;
        } catch (Exception ex) {
            Log.e("Data file retrieval :", " " + ex.getMessage());

        }


        return res;
    }

}