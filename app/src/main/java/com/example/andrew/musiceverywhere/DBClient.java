package com.example.andrew.musiceverywhere;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBClient {
    public static FirebaseDatabase db = FirebaseDatabase.getInstance();

    public static void writeToUser(User user) {
        if(user.getID() != null) {
            db.getReference("users/" + user.getID()).setValue(user);
        }
        else {
            DatabaseReference ref = db.getReference("users/").push();
            user.setID(ref.getKey());
            ref.setValue(user);
        }
    }
}
