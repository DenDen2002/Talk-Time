package com.av.avmessenger;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class groupActivity extends AppCompatActivity {
    private static final String TAG = "groupActivity";

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    GroupAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Groups> groupsArrayList;
    ImageView chatbut, setbut, cumbut, imglogout;
    Button join_group, create_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        chatbut = findViewById(R.id.chatsbut);
        setbut = findViewById(R.id.settingBut);
        cumbut = findViewById(R.id.camBut);
        imglogout = findViewById(R.id.logoutimg);
        join_group = findViewById(R.id.join_group);
        create_group = findViewById(R.id.create_group);

        groupsArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter(this, groupsArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        DatabaseReference reference = database.getReference().child("groups");
        reference.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String groupId = dataSnapshot.getKey();
                    String groupName = Objects.requireNonNull(dataSnapshot.child("groupName").getValue()).toString();
                    String groupDescription = Objects.requireNonNull(dataSnapshot.child("groupDescription").getValue()).toString();
                    String groupIcon = Objects.requireNonNull(dataSnapshot.child("groupIcon").getValue()).toString();
                    long creationDate = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("creationDate").getValue()).toString());

                    Groups group = new Groups(groupId, groupName, groupDescription, groupIcon, creationDate);
                    groupsArrayList.add(group);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });

        imglogout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(groupActivity.this, R.style.dialoge);
            dialog.setContentView(R.layout.dialog_layout);
            Button no, yes;
            yes = dialog.findViewById(R.id.yesbnt);
            no = dialog.findViewById(R.id.nobnt);
            yes.setOnClickListener(v1 -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(groupActivity.this, login.class);
                startActivity(intent);
                finish();
            });
            no.setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        });

        chatbut.setOnClickListener(view -> {
            Intent intent = new Intent(groupActivity.this, MainActivity.class);
            startActivity(intent);
        });

        setbut.setOnClickListener(v -> {
            Intent intent = new Intent(groupActivity.this, setting.class);
            startActivity(intent);
        });

        cumbut.setOnClickListener(v -> {
            Intent intent = new Intent(groupActivity.this, groupActivity.class);
            startActivity(intent);
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(groupActivity.this, login.class);
            startActivity(intent);
        }

        // Add click listeners for create_group and join_group buttons
        create_group.setOnClickListener(v -> showCreateGroupDialog());

        join_group.setOnClickListener(v -> showJoinGroupDialog());
    }

    private void showCreateGroupDialog() {
        Dialog dialog = new Dialog(groupActivity.this, R.style.dialoge);
        dialog.setContentView(R.layout.dialog_create_group);
        EditText groupNameInput = dialog.findViewById(R.id.groupNameInput);
        EditText groupDescriptionInput = dialog.findViewById(R.id.groupDescriptionInput);
        Button createButton = dialog.findViewById(R.id.createButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        createButton.setOnClickListener(v -> {
            String groupName = groupNameInput.getText().toString().trim();
            String groupDescription = groupDescriptionInput.getText().toString().trim();

            if (groupName.isEmpty() || groupDescription.isEmpty()) {
                Toast.makeText(groupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                createGroup(groupName, groupDescription);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void createGroup(String groupName, String groupDescription) {
        DatabaseReference groupsRef = database.getReference().child("groups");
        String groupId = groupsRef.push().getKey();

        HashMap<String, Object> groupData = new HashMap<>();
        groupData.put("groupId", groupId);
        groupData.put("groupName", groupName);
        groupData.put("groupDescription", groupDescription);
        groupData.put("groupIcon", "default_icon_url"); // Set a default icon URL or provide a way to upload an icon
        groupData.put("creationDate", System.currentTimeMillis());

        if (groupId != null) {
            groupsRef.child(groupId).setValue(groupData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(groupActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(groupActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(groupActivity.this, "Failed to generate group ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void showJoinGroupDialog() {
        Dialog dialog = new Dialog(groupActivity.this, R.style.dialoge);
        dialog.setContentView(R.layout.dialog_join_group);
        EditText groupIdInput = dialog.findViewById(R.id.groupIdInput);
        Button joinButton = dialog.findViewById(R.id.joinButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        joinButton.setOnClickListener(v -> {
            String groupId = groupIdInput.getText().toString().trim();

            if (groupId.isEmpty()) {
                Toast.makeText(groupActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
            } else {
                joinGroup(groupId);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void joinGroup(String groupName) {
        DatabaseReference groupsRef = database.getReference().child("groups");
        groupsRef.orderByChild("groupName").equalTo(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                        String groupId = groupSnapshot.getKey();

                        // Add the user to the group's users list in the database
                        DatabaseReference groupUsersRef = database.getReference().child("groups").child(groupId).child("users");
                        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                        groupUsersRef.child(userId).setValue(true).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Add the group to the user's list of groups
                                DatabaseReference userGroupsRef = database.getReference().child("userGroups").child(userId).child(groupId);
                                userGroupsRef.setValue(true).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(groupActivity.this, "Joined group successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(groupActivity.this, "Failed to join group", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(groupActivity.this, "Failed to join group", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(groupActivity.this, "Group not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(groupActivity.this, "Error joining group: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
