package com.av.avmessenger;

import java.util.ArrayList;

public class Groups {
    String groupId;
    String groupName;
    String groupDescription;
    String groupIcon;
    String createdBy;
    String creationDate;

    ArrayList<Users> users = new ArrayList<>();  // Initialize the users list
    ArrayList<msgModelclass> messagesAdpterArrayList = new ArrayList<>();  // Initialize the messages list

    // Default constructor required for calls to DataSnapshot.getValue(groups.class)
    public Groups() {}

    // Parameterized constructor
    public Groups(String groupId, String groupName, String groupDescription, String groupIcon, String createdBy, String creationDate) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupIcon = groupIcon;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
    }

    public Groups(String groupId, String groupName, String groupDescription, String groupIcon, long creationDate) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupIcon = groupIcon;
        this.creationDate = String.valueOf(creationDate);
    }

    // Getter and setter methods
    public ArrayList<Users> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Users> users) {
        this.users = users;
    }

    public ArrayList<msgModelclass> getMessagesAdpterArrayList() {
        return messagesAdpterArrayList;
    }

    public void setMessagesAdpterArrayList(ArrayList<msgModelclass> messagesAdpterArrayList) {
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
