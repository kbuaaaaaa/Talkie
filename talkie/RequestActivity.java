package com.example.talkie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.talkie.Adapter.UserAdapter;
import com.example.talkie.Model.Request;
import com.example.talkie.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    FirebaseUser fUser;
    DatabaseReference reference;

    UserAdapter userAdapter;
    List<Users> mUser;
    List<Request> mRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mUser = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.request_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        reference = FirebaseDatabase.getInstance().getReference("Request").child(fUser.getUid());
        mRequest = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRequest.clear();
                for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                    Request request = dSnapshot.getValue(Request.class);
                    mRequest.add(request);
                }

                requestList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void requestList() {

        reference = FirebaseDatabase.getInstance().getReference("MyUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dSnapshot : snapshot.getChildren()) {

                    Users user = dSnapshot.getValue(Users.class);
                    for (Request list : mRequest) {
                        if (user.getId().equals(list.getId())) {
                            mUser.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getApplicationContext(), mUser, 3);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}