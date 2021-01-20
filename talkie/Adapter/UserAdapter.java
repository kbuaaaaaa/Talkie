package com.example.talkie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.talkie.AddActivity;
import com.example.talkie.CallActivity;
import com.example.talkie.MessageActivity;
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

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<Users> mUsers;
    private int type;
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;

    public UserAdapter(Context context, List<Users> mUsers, int type) {
        this.context = context;
        this.mUsers = mUsers;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());
        if (users.getImageURL().equals("Default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(users.getImageURL()).into(holder.imageView);
        }
        switch (type){
            case 0 :
                holder.imageViewOn.setVisibility(View.GONE);
                holder.imageViewOff.setVisibility(View.GONE);
                holder.addBtn.setVisibility(View.VISIBLE);
                holder.chatBtn.setVisibility(View.GONE);
                holder.callBtn.setVisibility(View.GONE);
                holder.vidCallBtn.setVisibility(View.GONE);
                holder.acceptBtn.setVisibility(View.GONE);
                holder.declineBtn.setVisibility(View.GONE);
                break;
            case 1 :
                holder.addBtn.setVisibility(View.GONE);
                holder.chatBtn.setVisibility(View.GONE);
                holder.callBtn.setVisibility(View.GONE);
                holder.vidCallBtn.setVisibility(View.GONE);
                holder.acceptBtn.setVisibility(View.GONE);
                holder.declineBtn.setVisibility(View.GONE);
                if (users.getStatus().equals("Online")){
                    holder.imageViewOn.setVisibility(View.VISIBLE);
                    holder.imageViewOff.setVisibility(View.GONE);
                }else{
                    holder.imageViewOn.setVisibility(View.GONE);
                    holder.imageViewOff.setVisibility(View.VISIBLE);
                }
                break;
            case 2 :
                holder.imageViewOn.setVisibility(View.GONE);
                holder.imageViewOff.setVisibility(View.GONE);
                holder.addBtn.setVisibility(View.GONE);
                holder.chatBtn.setVisibility(View.VISIBLE);
                holder.callBtn.setVisibility(View.VISIBLE);
                holder.vidCallBtn.setVisibility(View.VISIBLE);
                holder.acceptBtn.setVisibility(View.GONE);
                holder.declineBtn.setVisibility(View.GONE);
                break;

            case 3:
                holder.imageViewOn.setVisibility(View.GONE);
                holder.imageViewOff.setVisibility(View.GONE);
                holder.addBtn.setVisibility(View.GONE);
                holder.chatBtn.setVisibility(View.GONE);
                holder.callBtn.setVisibility(View.GONE);
                holder.vidCallBtn.setVisibility(View.GONE);
                holder.acceptBtn.setVisibility(View.VISIBLE);
                holder.declineBtn.setVisibility(View.VISIBLE);

        }

        if (type == 1) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MessageActivity.class);
                    i.putExtra("userid", users.getId());
                    context.startActivity(i);
                }
            });
        }

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request").child(users.getId());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id",fUser.getUid());
                reference.push().setValue(hashMap);
            }
        });

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);
            }
        });


        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CallActivity.class);
                i.putExtra("userid",users.getId());
                context.startActivity(i);
            }
        });

        holder.vidCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FriendList").child(users.getId());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id",fUser.getUid());
                reference.push().setValue(hashMap);

                reference = FirebaseDatabase.getInstance().getReference("FriendList").child(fUser.getUid());
                hashMap = new HashMap<>();
                hashMap.put("id",users.getId());
                reference.push().setValue(hashMap);

                reference = FirebaseDatabase.getInstance().getReference("Request").child(fUser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dSnapshot: snapshot.getChildren()) {
                            Request req = dSnapshot.getValue(Request.class);
                            if (req.getId().equals(users.getId())){
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Request").child(fUser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dSnapshot: snapshot.getChildren()) {
                            Request req = dSnapshot.getValue(Request.class);
                            if (req.getId().equals(users.getId())){
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView imageView;
        public ImageView imageViewOn;
        public ImageView imageViewOff;
        public ImageButton addBtn;
        public ImageButton callBtn;
        public ImageButton chatBtn;
        public ImageButton vidCallBtn;
        public ImageButton acceptBtn;
        public ImageButton declineBtn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.usernameView);
            this.imageView = itemView.findViewById(R.id.userPicCalling);
            this.imageViewOn =  itemView.findViewById(R.id.statusViewOn);
            this.imageViewOff = itemView.findViewById(R.id.statusViewOff);
            this.addBtn = itemView.findViewById(R.id.btn_add);
            this.chatBtn = itemView.findViewById(R.id.btn_chat);
            this.callBtn = itemView.findViewById(R.id.btn_call);
            this.vidCallBtn = itemView.findViewById(R.id.btn_vidCall);
            this.acceptBtn = itemView.findViewById(R.id.btn_accept);
            this.declineBtn = itemView.findViewById(R.id.btn_decline);

        }
    }


}



