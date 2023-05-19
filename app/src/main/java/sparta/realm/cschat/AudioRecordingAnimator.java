package sparta.realm.cschat;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varunjohn1990.audio_record_view.AttachmentOption;
import com.varunjohn1990.audio_record_view.AttachmentOptionsListener;
import com.varunjohn1990.audio_record_view.AudioRecordView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecordingAnimator {
    public enum UserBehaviour {
        CANCELING,
        LOCKING,
        NONE
    }

    public enum RecordingBehaviour {
        CANCELED,
        LOCKED,
        LOCK_DONE,
        RELEASED
    }
    public interface RecordingListener {

        void onRecordingStarted();

        void onRecordingLocked();

        void onRecordingCompleted();

        void onRecordingCanceled();

    }
//    View sendView;
    private Animation animBlink, animJump, animJumpFast;
    private RecordingListener recordingListener;
    private boolean isDeleting;
    private LinearLayout viewContainer, layoutAttachmentOptions;
    private View sendView, imageViewLockArrow, imageViewLock, imageViewMic, dustin, dustin_cover, imageViewStop, imageViewSend;
    private View layoutAttachment, layoutDustin, layoutMessage, imageViewAttachment, imageViewCamera, imageViewEmoji;
    private View layoutSlideCancel, layoutLock, layoutEffect1, layoutEffect2;
    private EditText editTextMessage;
    private TextView timeText, textViewSlide;

    private ImageView stop, audio, send;
    private boolean stopTrackingAction;
    private Handler handler;

    private int audioTotalTime;
    private TimerTask timerTask;
    private Timer audioTimer;
    private SimpleDateFormat timeFormatter;

    private float lastX, lastY;
    private float firstX, firstY;

    private float directionOffset, cancelOffset, lockOffset;
    private float dp = 0;
    private boolean isLocked = false;

    private UserBehaviour userBehaviour = UserBehaviour.NONE;

    boolean isLayoutDirectionRightToLeft;

    int screenWidth, screenHeight;

    private List<AttachmentOption> attachmentOptionList;
    private AttachmentOptionsListener attachmentOptionsListener;

    private List<LinearLayout> layoutAttachments;

    private Context context;

    private boolean showCameraIcon = true, showAttachmentIcon = true, showEmojiIcon = true;
    private boolean removeAttachmentOptionAnimation;
    static String logTag="AudioRecordingAnimator";
    public AudioRecordingAnimator( View sendView,View imageViewLockArrow,View imageViewLock,View imageViewMic,View dustin,View dustin_cover,View  imageViewStop,View imageViewSend,
                                   View layoutAttachment,View layoutDustin,View layoutMessage,View imageViewAttachment,View imageViewCamera,View imageViewEmoji,
                                   View layoutSlideCancel,View layoutLock,View layoutEffect1,View layoutEffect2,EditText editTextMessage,
                                   TextView timeText,TextView textViewSlide,ImageView stop,ImageView audio,ImageView send
){

        this.sendView = sendView;
        this.imageViewLockArrow = imageViewLockArrow;
        this.imageViewLock = imageViewLock;
        this.imageViewMic = imageViewMic;
        this.dustin = dustin;
        this.dustin_cover = dustin_cover;
        this.imageViewStop = imageViewStop;
        this.imageViewSend = imageViewSend;
        this.layoutAttachment = layoutAttachment;
        this.layoutDustin = layoutDustin;
        this.layoutMessage = layoutMessage;
        this.imageViewAttachment = imageViewAttachment;
        this.imageViewCamera = imageViewCamera;
        this.imageViewEmoji = imageViewEmoji;
        this.layoutSlideCancel = layoutSlideCancel;
        this.layoutLock = layoutLock;
        this.layoutEffect1 = layoutEffect1;
        this.layoutEffect2 = layoutEffect2;
        this.editTextMessage = editTextMessage;
        this.timeText = timeText;
        this.textViewSlide = textViewSlide;
        this.stop = stop;
        this.audio = audio;
        this.send = send;
        context=imageViewEmoji.getContext();
        initUi();
    }

   public AudioRecordingAnimator(){

    }

    void initUi(){
        animBlink = AnimationUtils.loadAnimation(context,
                R.anim.blink);
        animJump = AnimationUtils.loadAnimation(context,
                R.anim.jump);
        animJumpFast = AnimationUtils.loadAnimation(context,
                R.anim.jump_fast);
        isLayoutDirectionRightToLeft = context.getResources().getBoolean(R.bool.is_right_to_left);
        timeFormatter = new SimpleDateFormat("m:ss", Locale.getDefault());

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        handler = new Handler(Looper.getMainLooper());

        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        imageViewSend.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    if (imageViewSend.getVisibility() != View.GONE) {
                        imageViewSend.setVisibility(View.GONE);
                        imageViewSend.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                    }

                    if (showCameraIcon) {
                        if (imageViewCamera.getVisibility() != View.VISIBLE && !isLocked) {
                            imageViewCamera.setVisibility(View.VISIBLE);
                            imageViewCamera.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                        }
                    }

                } else {
                    if (imageViewSend.getVisibility() != View.VISIBLE && !isLocked) {
                        imageViewSend.setVisibility(View.VISIBLE);
                        imageViewSend.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                    }

                    if (showCameraIcon) {
                        if (imageViewCamera.getVisibility() != View.GONE) {
                            imageViewCamera.setVisibility(View.GONE);
                            imageViewCamera.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                        }
                    }
                }
            }
        });

        sendView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (isDeleting) {
                    return true;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    cancelOffset = (float) (screenWidth / 2.8);
                    lockOffset = (float) (screenWidth / 2.5);

                    if (firstX == 0) {
                        firstX = motionEvent.getRawX();
                    }

                    if (firstY == 0) {
                        firstY = motionEvent.getRawY();
                    }

                    startRecord();

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        stopRecording(AudioRecordView.RecordingBehaviour.RELEASED);
                    }

                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    if (stopTrackingAction) {
                        return true;
                    }

                    UserBehaviour direction = UserBehaviour.NONE;

                    float motionX = Math.abs(firstX - motionEvent.getRawX());
                    float motionY = Math.abs(firstY - motionEvent.getRawY());

                    if (isLayoutDirectionRightToLeft ? (motionX > directionOffset && lastX > firstX && lastY > firstY) : (motionX > directionOffset && lastX < firstX && lastY < firstY)) {

                        if (isLayoutDirectionRightToLeft ? (motionX > motionY && lastX > firstX) : (motionX > motionY && lastX < firstX)) {
                            direction = UserBehaviour.CANCELING;

                        } else if (motionY > motionX && lastY < firstY) {
                            direction = UserBehaviour.LOCKING;
                        }

                    } else if (isLayoutDirectionRightToLeft ? (motionX > motionY && motionX > directionOffset && lastX > firstX) : (motionX > motionY && motionX > directionOffset && lastX < firstX)) {
                        direction = UserBehaviour.CANCELING;
                    } else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                        direction = UserBehaviour.LOCKING;
                    }

                    if (direction == UserBehaviour.CANCELING) {
                        if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawY() + sendView.getWidth() / 2 > firstY) {
                            userBehaviour = UserBehaviour.CANCELING;
                        }

                        if (userBehaviour == UserBehaviour.CANCELING) {
                            translateX(-(firstX - motionEvent.getRawX()));
                        }
                    } else if (direction == UserBehaviour.LOCKING) {
                        if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawX() + sendView.getWidth() / 2 > firstX) {
                            userBehaviour = UserBehaviour.LOCKING;
                        }

                        if (userBehaviour == UserBehaviour.LOCKING) {
                            translateY(-(firstY - motionEvent.getRawY()));
                        }
                    }

                    lastX = motionEvent.getRawX();
                    lastY = motionEvent.getRawY();
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });

        imageViewStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocked = false;
                stopRecording(AudioRecordView.RecordingBehaviour.LOCK_DONE);
            }
        });
        setupAttachmentOptions();
    }
    public RecordingListener getRecordingListener() {
        return recordingListener;
    }

    public void setRecordingListener(RecordingListener recordingListener) {
        this.recordingListener = recordingListener;
    }
    private void startRecord() {
        if (recordingListener != null)
            recordingListener.onRecordingStarted();

//        hideAttachmentOptionView();

        stopTrackingAction = false;
        editTextMessage.setVisibility(View.INVISIBLE);
        imageViewAttachment.setVisibility(View.INVISIBLE);
        imageViewCamera.setVisibility(View.INVISIBLE);
        imageViewEmoji.setVisibility(View.INVISIBLE);
        sendView.animate().scaleXBy(1f).scaleYBy(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
        timeText.setVisibility(View.VISIBLE);
        layoutLock.setVisibility(View.VISIBLE);
        layoutSlideCancel.setVisibility(View.VISIBLE);
        imageViewMic.setVisibility(View.VISIBLE);
        layoutEffect2.setVisibility(View.VISIBLE);
        layoutEffect1.setVisibility(View.VISIBLE);

        timeText.startAnimation(animBlink);
        imageViewLockArrow.clearAnimation();
        imageViewLock.clearAnimation();
        imageViewLockArrow.startAnimation(animJumpFast);
        imageViewLock.startAnimation(animJump);

        if (audioTimer == null) {
            audioTimer = new Timer();
            timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timeText.setText(timeFormatter.format(new Date(audioTotalTime * 1000)));
                        audioTotalTime++;
                    }
                });
            }
        };

        audioTotalTime = 0;
        audioTimer.schedule(timerTask, 0, 1000);
    }
    private void stopRecording(AudioRecordView.RecordingBehaviour recordingBehaviour) {

        stopTrackingAction = true;
        firstX = 0;
        firstY = 0;
        lastX = 0;
        lastY = 0;

        userBehaviour = UserBehaviour.NONE;

        sendView.animate().scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(100).setInterpolator(new LinearInterpolator()).start();
        layoutSlideCancel.setTranslationX(0);
        layoutSlideCancel.setVisibility(View.GONE);

        layoutLock.setVisibility(View.GONE);
        layoutLock.setTranslationY(0);
        imageViewLockArrow.clearAnimation();
        imageViewLock.clearAnimation();

        if (isLocked) {
            return;
        }

        if (recordingBehaviour == AudioRecordView.RecordingBehaviour.LOCKED) {
            imageViewStop.setVisibility(View.VISIBLE);

            if (recordingListener != null)
                recordingListener.onRecordingLocked();

        } else if (recordingBehaviour == AudioRecordView.RecordingBehaviour.CANCELED) {
            timeText.clearAnimation();
            timeText.setVisibility(View.INVISIBLE);
            imageViewMic.setVisibility(View.INVISIBLE);
            imageViewStop.setVisibility(View.GONE);
            layoutEffect2.setVisibility(View.GONE);
            layoutEffect1.setVisibility(View.GONE);

            timerTask.cancel();
            delete();

            if (recordingListener != null)
                recordingListener.onRecordingCanceled();

        } else if (recordingBehaviour == AudioRecordView.RecordingBehaviour.RELEASED || recordingBehaviour == AudioRecordView.RecordingBehaviour.LOCK_DONE) {
            timeText.clearAnimation();
            timeText.setVisibility(View.INVISIBLE);
            imageViewMic.setVisibility(View.INVISIBLE);
            editTextMessage.setVisibility(View.VISIBLE);
            if (showAttachmentIcon) {
                imageViewAttachment.setVisibility(View.VISIBLE);
            }
            if (showCameraIcon) {
                imageViewCamera.setVisibility(View.VISIBLE);
            }
            if (showEmojiIcon) {
                imageViewEmoji.setVisibility(View.VISIBLE);
            }
            imageViewStop.setVisibility(View.GONE);
            editTextMessage.requestFocus();
            layoutEffect2.setVisibility(View.GONE);
            layoutEffect1.setVisibility(View.GONE);

            timerTask.cancel();

            if (recordingListener != null)
                recordingListener.onRecordingCompleted();
        }
    }
    private void translateY(float y) {
        if (y < -lockOffset) {
            locked();
            sendView.setTranslationY(0);
            return;
        }

        if (layoutLock.getVisibility() != View.VISIBLE) {
            layoutLock.setVisibility(View.VISIBLE);
        }

        sendView.setTranslationY(y);
        layoutLock.setTranslationY(y / 2);
        sendView.setTranslationX(0);
    }

    private void translateX(float x) {

        if (isLayoutDirectionRightToLeft ? x > cancelOffset : x < -cancelOffset) {
            canceled();
            sendView.setTranslationX(0);
            layoutSlideCancel.setTranslationX(0);
            return;
        }

        sendView.setTranslationX(x);
        layoutSlideCancel.setTranslationX(x);
        layoutLock.setTranslationY(0);
        sendView.setTranslationY(0);

        if (Math.abs(x) < imageViewMic.getWidth() / 2) {
            if (layoutLock.getVisibility() != View.VISIBLE) {
                layoutLock.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutLock.getVisibility() != View.GONE) {
                layoutLock.setVisibility(View.GONE);
            }
        }
    }

    private void locked() {
        stopTrackingAction = true;
        stopRecording(AudioRecordView.RecordingBehaviour.LOCKED);
        isLocked = true;
    }

    private void canceled() {
        stopTrackingAction = true;
        stopRecording(AudioRecordView.RecordingBehaviour.CANCELED);
    }
    private void delete() {
        imageViewMic.setVisibility(View.VISIBLE);
        imageViewMic.setRotation(0);
        isDeleting = true;
        sendView.setEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isDeleting = false;
                sendView.setEnabled(true);

                if (showAttachmentIcon) {
                    imageViewAttachment.setVisibility(View.VISIBLE);
                }
                if (showCameraIcon) {
                    imageViewCamera.setVisibility(View.VISIBLE);
                }
                if (showEmojiIcon) {
                    imageViewEmoji.setVisibility(View.VISIBLE);
                }
            }
        }, 1250);

        imageViewMic.animate().translationY(-dp * 150).rotation(180).scaleXBy(0.6f).scaleYBy(0.6f).setDuration(500).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

                float displacement = 0;

                if (isLayoutDirectionRightToLeft) {
                    displacement = dp * 40;
                } else {
                    displacement = -dp * 40;
                }

                dustin.setTranslationX(displacement);
                dustin_cover.setTranslationX(displacement);

                dustin_cover.animate().translationX(0).rotation(-120).setDuration(350).setInterpolator(new DecelerateInterpolator()).start();

                dustin.animate().translationX(0).setDuration(350).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        dustin.setVisibility(View.VISIBLE);
                        dustin_cover.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageViewMic.animate().translationY(0).scaleX(1).scaleY(1).setDuration(350).setInterpolator(new LinearInterpolator()).setListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imageViewMic.setVisibility(View.INVISIBLE);
                                imageViewMic.setRotation(0);

                                float displacement = 0;

                                if (isLayoutDirectionRightToLeft) {
                                    displacement = dp * 40;
                                } else {
                                    displacement = -dp * 40;
                                }

                                dustin_cover.animate().rotation(0).setDuration(150).setStartDelay(50).start();
                                dustin.animate().translationX(displacement).setDuration(200).setStartDelay(250).setInterpolator(new DecelerateInterpolator()).start();
                                dustin_cover.animate().translationX(displacement).setDuration(200).setStartDelay(250).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        editTextMessage.setVisibility(View.VISIBLE);
                                        editTextMessage.requestFocus();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }
                ).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
    private void setupAttachmentOptions() {
        imageViewAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAttachment.getVisibility() == View.VISIBLE) {
                    int x = isLayoutDirectionRightToLeft ? (int) (dp * (18 + 40 + 4 + 56)) : (int) (screenWidth - (dp * (18 + 40 + 4 + 56)));
                    int y = (int) (dp * 220);

                    int startRadius = 0;
                    int endRadius = (int) Math.hypot(screenWidth - (dp * (8 + 8)), (dp * 220));

                    Animator anim = ViewAnimationUtils.createCircularReveal(layoutAttachment, x, y, endRadius, startRadius);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            layoutAttachment.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    anim.start();

                } else {

                    if (!removeAttachmentOptionAnimation) {
                        int count = 0;
                        if (layoutAttachments != null && !layoutAttachments.isEmpty()) {

                            int[] arr = new int[]{5, 4, 2, 3, 1, 0};

                            if (isLayoutDirectionRightToLeft) {
                                arr = new int[]{3, 4, 0, 5, 1, 2};
                            }

                            for (int i = 0; i < layoutAttachments.size(); i++) {
                                if (arr[i] < layoutAttachments.size()) {
                                    final LinearLayout layout = layoutAttachments.get(arr[i]);
                                    layout.setScaleX(0.4f);
                                    layout.setAlpha(0f);
                                    layout.setScaleY(0.4f);
                                    layout.setTranslationY(dp * 48 * 2);
                                    layout.setVisibility(View.INVISIBLE);

                                    layout.animate().scaleX(1f).scaleY(1f).alpha(1f).translationY(0).setStartDelay((count * 25) + 50).setDuration(300).setInterpolator(new OvershootInterpolator()).start();
                                    layout.setVisibility(View.VISIBLE);

                                    count++;
                                }
                            }
                        }
                    }

                    int x = isLayoutDirectionRightToLeft ? (int) (dp * (18 + 40 + 4 + 56)) : (int) (screenWidth - (dp * (18 + 40 + 4 + 56)));
                    int y = (int) (dp * 220);

                    int startRadius = 0;
                    int endRadius = (int) Math.hypot(screenWidth - (dp * (8 + 8)), (dp * 220));

                    Animator anim = ViewAnimationUtils.createCircularReveal(layoutAttachment, x, y, startRadius, endRadius);
                    anim.setDuration(500);
                    layoutAttachment.setVisibility(View.VISIBLE);
                    anim.start();
                }
            }
        });
    }
    private void showErrorLog(String s) {
        Log.e(logTag, s);
    }
}
