package com.av.avmessenger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    Context context;
    ArrayList<Groups> groupsArrayList;

    public GroupAdapter(Context context, ArrayList<Groups> groupsArrayList) {
        this.context = context;
        this.groupsArrayList = groupsArrayList;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Groups group = groupsArrayList.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.groupDescription.setText(group.getGroupDescription());
        Picasso.get().load(group.getGroupIcon()).into(holder.groupIcon);
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(group.getGroupId()); // Adjusted to access the specific group node
//        Log.d("CurrentUserId",groupRef);
        holder.itemView.setOnClickListener(view -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("CurrentUserId", currentUserId);
            ArrayList pema = groupsArrayList;

            groupRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() { // Accessing the "users" node under the specific group
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isUserInGroup = dataSnapshot.hasChild(currentUserId); // Checking if the current user exists in the group
                    Log.d("UserCheck", "Is current user in group: " + isUserInGroup);

                    if (isUserInGroup) {
                        // User is in the group, handle the logic accordingly
                        Intent intent = new Intent(context, viewGroupMsg.class);
                        intent.putExtra("groupId", group.getGroupId());
                        intent.putExtra("groupName", group.getGroupName());
                        intent.putExtra("groupDescription", group.getGroupDescription());
                        intent.putExtra("groupIcon", group.getGroupIcon());
                        context.startActivity(intent);
                    } else {
                        // User is not in the group, show a toast or handle it accordingly
                        Toast.makeText(context, "You are not a member of this group", Toast.LENGTH_SHORT).show();
                    }
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("FirebaseError", "Error accessing group users: " + databaseError.getMessage());
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return groupsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView groupIcon;
        TextView groupName;
        TextView groupDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupIcon = itemView.findViewById(R.id.groupIcon);
            groupName = itemView.findViewById(R.id.groupName);
            groupDescription = itemView.findViewById(R.id.groupDescription);
        }
    }
}
