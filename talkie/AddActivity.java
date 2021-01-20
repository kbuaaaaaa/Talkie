package com.example.talkie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talkie.Adapter.MessageAdapter;
import com.example.talkie.Adapter.UserAdapter;
import com.example.talkie.Model.Chat;
import com.example.talkie.Model.Chatlist;
import com.example.talkie.Model.Request;
import com.example.talkie.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText nameSearch;
    ImageButton searchBtn;
    Button requestBtn;

    FirebaseUser fUser;
    DatabaseReference reference;
    Intent intent;

    UserAdapter userAdapter;
    List<Users> mUser;

    String userid;
    String searchedName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        searchBtn = findViewById(R.id.btn_search);
        nameSearch = findViewById(R.id.search_name);
        requestBtn = findViewById(R.id.requestBtn);
        mUser = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.user_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fUser.getUid());

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers");

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.clear();
                if (!nameSearch.getText().toString().equals("")) {
                    searchedName = nameSearch.getText().toString();
                    Query query = reference.orderByChild("username").startAt(searchedName);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                                Users user = dSnapshot.getValue(Users.class);
                                assert user != null;
                                if (!user.getId().equals(fUser.getUid())) {
                                    mUser.add(user);
                                }

                                userAdapter = new UserAdapter(getApplicationContext(), mUser, 0);
                                recyclerView.setAdapter(userAdapter);
                            }
                            nameSearch.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddActivity.this, RequestActivity.class);
                startActivity(i);
            }
        });
    }

}
