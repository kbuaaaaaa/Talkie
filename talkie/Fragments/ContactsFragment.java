package com.example.talkie.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.talkie.Adapter.UserAdapter;
import com.example.talkie.AddActivity;
import com.example.talkie.MessageActivity;
import com.example.talkie.Model.FriendList;
import com.example.talkie.Model.Request;
import com.example.talkie.Model.Users;
import com.example.talkie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchClient;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private List<FriendList> mFriends;
    private Button addBtn;
    DatabaseReference reference;
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addBtn = view.findViewById(R.id.add_button);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddActivity.class);
                getContext().startActivity(i);
            }
        });
        mFriends = new ArrayList<>();
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("FriendList").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFriends.clear();
                for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                    FriendList list = dSnapshot.getValue(FriendList.class);
                    mFriends.add(list);
                }

                ReadUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ReadUsers();
        return view;
    }

    private void ReadUsers(){
        reference = FirebaseDatabase.getInstance().getReference("MyUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dSnapshot : snapshot.getChildren()) {

                    Users user = dSnapshot.getValue(Users.class);
                    for (FriendList list : mFriends) {
                        if (user.getId().equals(list.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, 2);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

