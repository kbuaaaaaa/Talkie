package com.example.talkie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.talkie.Adapter.MessageAdapter;
import com.example.talkie.Model.Chat;
import com.example.talkie.Model.Users;
import com.example.talkie.sinch.SinchService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.talkie.sinch.*;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;


import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallActivity extends BaseActivity implements SinchService.StartFailedListener{

    android.content.Context context;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    Users user;
    DatabaseReference reference;
    private String userid;
    private ImageButton hangupBtn;
    private ProgressDialog mSpinner;
    static final String TAG = CallActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    private ImageView imgvMuteVideo,imgvSpeakerVideo;
    private boolean isSpeakerOn = false;


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration =  findViewById(R.id.callDuration);
        mCallerName =  findViewById(R.id.remoteUser);
        mCallState =  findViewById(R.id.callState);
//        imgvMuteVideo = findViewById(R.id.imgvMuteVideo);
//        imgvSpeakerVideo = findViewById(R.id.imgvSpeakerVideo);

        mCallId = getIntent().getStringExtra("CallId");
        ImageButton endCallButton = findViewById(R.id.hangupButton);
        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }

//        imgvSpeakerVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setupSpeaker();
//            }
//        });
//
//
//
//        imgvMuteVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setupMute();
//            }
//        });
        userid = getIntent().getStringExtra("userid");
        mCallerName.setText(userid);

//        reference = FirebaseDatabase.getInstance().getReference().child("MyUsers");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                user = dataSnapshot.child(userid).getValue(Users.class);
//                mCallerName.setText(user.getUsername());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
        //asking for permissions here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA
                    , android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_PHONE_STATE},100);
        }



    }

    //this method is invoked when the connection is established with the SinchService
    @Override
    protected void onServiceConnect() {
        Call call;
        getSinchServiceInterface().setStartListener(this);

        if (mCallId != null){
            call = getSinchServiceInterface().getCall(mCallId);
            updateUI();
        }else {
            call = getSinchServiceInterface().callUserVideo(userid);
            mCallId = call.getCallId();
        }
        if (!mAddedListener) {
            call.addCallListener(new SinchCallListener());
            mAddedListener = true;
        }
        updateUI();
    }


    private void updateUI() {

        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        assert call != null;
        if (call != null) {
            mCallState.setText(call.getState().toString());
            if (call.getState() == CallState.ESTABLISHED) {
                //when the call is established, addVideoViews configures the video to  be shown
                addVideoViews();
            }
        }
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    @Override
    public void onStartFailed(SinchError error) {
        // Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();

    }

    //Invoked when just after the service is connected with Sinch
    @Override
    public void onStarted() {
    }

    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }


    //Once the connection is made to the Sinch Service, It takes you to the next activity where you enter the name of the user to whom the call is to be placed

    private void showSpinner() {

    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            //Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            //String endMsg = "Call ended: " + call.getDetails().toString();
            //Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    //method to update live duration of the call
    private void updateCallDuration() {
        if (mCallStart > 0) {
            mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    //method which sets up the video feeds from the server to the UI of the activity
    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());

            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this toggles the front camera to rear camera and vice versa
                    vc.toggleCaptureDevicePosition();
                }
            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    //removes video feeds from the com once the call is terminated
    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    private void setupSpeaker(){

        AudioManager audioManager =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (isSpeakerOn == false) {
            isSpeakerOn = true;
            //imgvSpeakerVideo.setImageResource(R.drawable.speaker_on);
        } else {
            isSpeakerOn = false;
            //imgvSpeakerVideo.setImageResource(R.drawable.speaker_off);
        }

        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(isSpeakerOn);

    }

    private void setupMute(){
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        if (audioManager.isMicrophoneMute()) {

            audioManager.setMicrophoneMute(false);
            //imgvMuteVideo.setImageResource(R.drawable.nomuted);

        } else {
            audioManager.setMicrophoneMute(true);
            //imgvMuteVideo.setImageResource(R.drawable.muted);
        }

    }

}





