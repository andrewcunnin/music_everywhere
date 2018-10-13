package com.example.andrew.musiceverywhere;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DBClient {
    public static FirebaseDatabase db = FirebaseDatabase.getInstance();

    public static void writeToUser(User user) {
        if(user.getId() != null) {
            db.getReference("users/" + user.getId()).setValue(user);
        }
        else {
            DatabaseReference ref = db.getReference("users/").push();
            user.setId(ref.getKey());
            ref.setValue(user);
        }
    }

}