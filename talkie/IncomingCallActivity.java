package com.example.talkie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.talkie.Model.Users;
import com.example.talkie.sinch.AudioPlayer;
import com.example.talkie.sinch.SinchService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;

import java.util.List;

public class IncomingCallActivity extends  BaseActivity{

    static final String TAG = IncomingCallActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    private ImageView imgvDisplayIMgUSER;
    private DatabaseReference dbrefUsers;
    private String userid;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        dbrefUsers = FirebaseDatabase.getInstance().getReference().child("MyUsers");

        ImageButton answer =  findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        ImageButton decline =  findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        imgvDisplayIMgUSER = findViewById(R.id.userPicCalling);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra("CallId");
        userid = getIntent().getStringExtra("UserId");

        dbrefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Users temp = ds.getValue(Users.class);
                    if (temp.getId().equals(userid)){
                        users = temp;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        if(users.getImageURL().equals("Default")){
//            imgvDisplayIMgUSER.setImageResource(R.mipmap.ic_launcher);
//        }else{
//            Glide.with(getApplicationContext()).load(users.getImageURL()).into(imgvDisplayIMgUSER);
//        }



    }



    @Override
    protected void onServiceConnect() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            TextView remoteUser =  findViewById(R.id.remoteUser);
            remoteUser.setText(call.getRemoteUserId());

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        call.answer();
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("CallId",mCallId);
        startActivity(intent);
    }

    private void declineClicked() {

        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();

    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(final Call call) {

            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();


        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            // Display some kind of icon showing it's a video call
        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };


}