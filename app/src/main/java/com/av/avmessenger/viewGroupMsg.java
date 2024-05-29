package com.av.avmessenger;

import android.app.usage.NetworkStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class viewGroupMsg extends AppCompatActivity {

    private TextView groupName;

    String reciverimg, reciverUid, reciverName, SenderUID;

    String senderRoom, reciverRoom;
    private EditText messageEditText;
    private DatabaseReference groupMsgRef;
    private LinearLayout messageLayout; // Declare messageLayout

    RecyclerView groupMessageAdapter;
    groupMessageAdapter ggroupMessageAdapter;

    ArrayList<groupMsgModelclass> groupMessageArrayList;
    private List<String> messages;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    CardView sendbtn;
    EditText textmsg;

    ArrayList<String> groupMembers = new ArrayList<>();
    public static String senderImg;

    public static String reciverIImg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_msg);

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        groupMessageArrayList = new ArrayList<>();
        // Get the passed data from the intent
        String name = getIntent().getStringExtra("groupName");
        final String groupId = getIntent().getStringExtra("groupId");

        SenderUID = firebaseAuth.getUid();
        Log.d("senderID", SenderUID);


        groupMsgRef = database.getReference().child("groupchats").child(groupId).child("messages");


        // Find views
        groupName = findViewById(R.id.recivername);

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        groupMessageAdapter = findViewById(R.id.msgadpter1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        groupMessageAdapter.setLayoutManager(linearLayoutManager);
        ggroupMessageAdapter = new groupMessageAdapter(viewGroupMsg.this, groupMessageArrayList);
        groupMessageAdapter.setAdapter(ggroupMessageAdapter);

        // Set the data to the views
        groupName.setText(name);

        // Set click listener for the send button
        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());

        DatabaseReference chatreference = database.getReference().child("groupchats").child(groupId).child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMessageArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    groupMsgModelclass messages = dataSnapshot.getValue(groupMsgModelclass.class);
                    groupMessageArrayList.add(messages);
                }
                ggroupMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference groupMembersRef = database.getReference().child("groups").child(groupId).child("users");
        groupMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String memberUid = dataSnapshot.getKey();
                    groupMembers.add(memberUid);
                }
                for (String memberUid : groupMembers) {
                    DatabaseReference userRef = database.getReference().child("user").child(memberUid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Retrieve user profile information
                            String userName = snapshot.child("userName").getValue(String.class);
                            String profilePicUrl = snapshot.child("profilepic").getValue(String.class);

                            // Now you have the profile information, you can display it or store it as needed
                            Log.d("UserProfile", "Username: " + userName + ", ProfilePicUrl: " + profilePicUrl);

                            // Example: You can use Picasso to load profile images into ImageView
//                             Picasso.get().load(profilePicUrl).into(senderImg);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue(String.class);
                reciverIImg = reciverimg;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                Log.d("message", message);
                if (message.isEmpty()) {
                    Toast.makeText(viewGroupMsg.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                groupMsgModelclass messagess = new groupMsgModelclass(message, SenderUID, date.getTime());

                database = FirebaseDatabase.getInstance();
                DatabaseReference groupChatRef = database.getReference().child("groupchats").child(groupId).child("messages");
                groupChatRef.push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Message sent successfully
                        } else {
                            // Message sending failed
                            Toast.makeText(viewGroupMsg.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
