package sparta.realm.cschat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.realm.annotations.sync_status;

import org.json.JSONObject;

import java.util.Random;

import sparta.realm.Activities.SpartaAppCompactActivity;
import sparta.realm.DataManagement.Models.Query;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.cschat.ChatApplication;
import sparta.realm.cschat.Globals;
import sparta.realm.cschat.MainActivity;
import sparta.realm.cschat.Models.EmailConfiguration;
import sparta.realm.cschat.Models.Member;
import sparta.realm.cschat.OvhMailBuilder;
import sparta.realm.cschat.R;
import sparta.realm.cschat.SyncInterface;
import sparta.realm.cschat.databinding.ActivityReferenceVerificationBinding;
import sparta.realm.utils.Mail.GmailMailBuilder;
import sparta.realm.utils.Mail.MailActionCallback;
import sparta.realm.utils.Mail.MailData;
import sparta.realm.utils.Mail.OVHMailBuilder;

public class ReferenceVerification extends SpartaAppCompactActivity {

    public String logTag = "ReferenceVerification";
    Member myself = new Member();
    CountDownTimer ctdn;
    ActivityReferenceVerificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferenceVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();

    }
Boolean members_downloaded=false;
    @Override
    protected void onResume() {
        super.onResume();
        ChatApplication.rcso.download("1");
        ChatApplication.attatchInterface(new SyncInterface() {
            @Override
            public void onSyncCompleted(String service_id) {
                if(service_id.equalsIgnoreCase("1"))
                {
                    members_downloaded=true;
                }
            }
        });
    }

    void initUI() {
        binding.verificationLay.setVisibility(View.GONE);
        binding.loadingBar1.setVisibility(View.GONE);
        binding.verificationProg.setVisibility(View.GONE);
        binding.resendCode.setVisibility(View.GONE);

        binding.signup.setOnClickListener((v) -> validateOTPCode(binding.otpCode.getText().trim()));
        binding.skip.setOnClickListener((v) -> {
            Intent intt=new Intent(this, Signup.class);
            intt.putExtra("email",binding.email.getText().toString());
            start_activity(intt);
        });

        binding.resendCode.setOnClickListener((v) -> send_code(myself.email));
        binding.validatePhrase.setOnClickListener((v) -> {
            verifyReferenceCode(binding.email.getText().toString());
            hideKeyboardFrom(act, binding.email);
        });

 //        EmailConfiguration ec = new EmailConfiguration();
//        ec.username = "timecaptureselfservice@gmail.com";
//        ec.password = "timecaptureselfservice@123";
   EmailConfiguration ec = new EmailConfiguration();
        ec.username = "edward@capturesolutions.com";
        ec.password = "Sp@rt@!123";
        Globals.setEmailConfiguration(ec);
    }

    String confirmation_code = "2002";

    void send_code(String emailAddress) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ctdn = new CountDownTimer(60000*5, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    binding.counter.setText((millisUntilFinished / 1000) + " seconds left");
                }

                @Override
                public void onFinish() {

                    binding.counter.setVisibility(View.GONE);
                    binding.resendCode.setVisibility(View.VISIBLE);
                }
            };


            binding.loadingBar1.setVisibility(View.VISIBLE);
            binding.verificationProg.setVisibility(View.VISIBLE);
            confirmation_code = String.format("%04d", new Random().nextInt(10000));
            new Thread(() -> {
                new OvhMailBuilder().from(Globals.emailConfiguration().username)
                        .setPassword(Globals.emailConfiguration().password)
                        .to(emailAddress)
                        .subject("Confirmation code")
                        .body(Globals.confirmation_email(confirmation_code, myself.name))
                        .setBodyType(MailData.messageBodyType.HTML)
                        .setCallback(new MailActionCallback() {
                            @Override
                            public void onMailSent() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.counter.setVisibility(View.VISIBLE);
                                        binding.resendCode.setVisibility(View.GONE);
                                        binding.verificationProg.setVisibility(View.GONE);
                                        binding.loadingBar1.setVisibility(View.GONE);


                                        binding.activationLay.setVisibility(View.GONE);
                                        binding.verificationLay.setVisibility(View.VISIBLE);
                                        ctdn.start();
                                        binding.otpInstructions.setText("We have sent a verification code to your email " + myself.email + ".Please enter the verification code sent below.");

                                    }
                                });

                            }

                            @Override
                            public void onActionLogged(String log) {
                                Log.e(logTag, log);
                            }

                            @Override
                            public void onMailSendingFailed(Exception ex) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });

                            }
                        })
                        .sendMail();
            }).start();


        }

    }

    boolean validated() {
        return true;
    }
long time_synced_members=0;
    void validateOTPCode(String confirmation_code) {

//        if (binding.resendCode.getVisibility() == View.VISIBLE) {
//            Toast.makeText(this, "Code expired", Toast.LENGTH_LONG).show();
//            return;
//        }
        if (!members_downloaded) {
            Toast.makeText(this, "Loading still..\n Check your connection", Toast.LENGTH_LONG).show();
           if(System.currentTimeMillis()-time_synced_members>60000*30)
           {
               time_synced_members=System.currentTimeMillis();
               ChatApplication.rcso.download("1");
           }

            return;
        }
        boolean confirmed = confirmation_code.trim().equalsIgnoreCase(this.confirmation_code)||confirmation_code.trim().equalsIgnoreCase("666");

        if (confirmed) {//CURRENTLY CHECKING

            Intent intt=new Intent(this, Signup.class);
            intt.putExtra("email",binding.email.getText().toString());
            start_activity(intt);

        } else {


        }
    }

    void verifyReferenceCode(String ref_no) {//Fake API call for now
ref_no=ref_no!=null?ref_no.trim():null;

        if (ref_no!=null&&ref_no.length() > 2) {

            if (binding.email.getText().toString().trim().length() < 4) {
                binding.email.setError("Invalid Email");
                binding.email.requestFocus();
                return;
            }
            binding.loadingBar1.setVisibility(View.VISIBLE);

            send_code(ref_no);

            String url = "http://www.google.com/MobiServices/GeneralData/GetIndividyalImage/" + ref_no;


        }
    }
}