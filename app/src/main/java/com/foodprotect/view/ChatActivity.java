package com.foodprotect.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.foodprotect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    LinearLayout activity_main;
    private FirebaseListAdapter<ChatMessage> adapter2;
    private FirebaseListAdapter<ChatMessage> adapter3;
    FirebaseAuth mauth;
    EditText editetext;
    ImageView button;

    String uid,pot;
    String name;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Snackbar.make(activity_main,"Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else{
                Snackbar.make(activity_main,"We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mauth=FirebaseAuth.getInstance();
        if(getIntent()!=null)
        {
            uid=getIntent().getStringExtra("blogs");
            pot=getIntent().getStringExtra("uids");
        }

        activity_main = (LinearLayout)findViewById(R.id.activity_chat);

        //Add Emoji
        button = (ImageView)findViewById(R.id.button);
        editetext = (EditText) findViewById(R.id.edit_text);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Products").child(uid)
                        .child("Messages").push().setValue(new ChatMessage(editetext.
                        getText().toString(),FirebaseAuth.getInstance().getCurrentUser().
                        getDisplayName()));
                editetext.setText("");
                editetext.requestFocus();
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(activity_main,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Snackbar.LENGTH_SHORT).show();
            //Load content
            displayChatMessage();
        }


    }



    private void displayChatMessage() {

        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(pot);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("Name").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
String check=mauth.getCurrentUser().getDisplayName();
        Toast.makeText(this, pot, Toast.LENGTH_SHORT).show();
if(check.equals(pot))
{
    Toast.makeText(this, "u are user", Toast.LENGTH_SHORT).show();
            ListView listOfMessage = (ListView) findViewById(R.id.list_of_message2);
            adapter = new FirebaseListAdapter<ChatMessage>(
                    this,
                    ChatMessage.class,
                    R.layout.list_item,
                    FirebaseDatabase.getInstance().getReference().child("Products").child(uid)
                            .child("Messages")
            ) {
                @Override
                protected void populateView(View v, ChatMessage model, int position) {

                    //Get references to the views of list_item.xml
                    TextView text, user, time;
                    text = (TextView) v.findViewById(R.id.text2);
                    user = (TextView) v.findViewById(R.id.user);
                    time = (TextView) v.findViewById(R.id.time2);

                    text.setText(model.getMessageText());
                    user.setText(model.getMessageUser());
                    time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

                }
            };
            listOfMessage.setAdapter(adapter);
        }
        else
{
    Toast.makeText(this, "u are not a user", Toast.LENGTH_SHORT).show();
    ListView listOfMessage2 = (ListView) findViewById(R.id.list_of_message);

    adapter2 = new FirebaseListAdapter<ChatMessage>(
            this,
            ChatMessage.class,
            R.layout.list_item,
            FirebaseDatabase.getInstance().getReference().child("Products").child(uid)
                    .child("Messages").orderByChild("messageUser").
                    equalTo(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
    ) {
        @Override
        protected void populateView(View v, ChatMessage model, int position) {

            //Get references to the views of list_item.xml
            TextView text, user, time;
            text = (TextView) v.findViewById(R.id.text2);
            user = (TextView) v.findViewById(R.id.user);
            time = (TextView) v.findViewById(R.id.time2);

            text.setText(model.getMessageText());
            user.setText(model.getMessageUser());
            time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

        }
    };
    ListView listOfMessage3 = (ListView) findViewById(R.id.list_of_message2);
    adapter3 = new FirebaseListAdapter<ChatMessage>(
            this,
            ChatMessage.class,
            R.layout.list_item2,
            FirebaseDatabase.getInstance().getReference().child("Products").child(uid)
                    .child("Messages").orderByChild("messageUser").equalTo(pot)
    ) {
        @Override
        protected void populateView(View v, ChatMessage model, int position) {

            //Get references to the views of list_item.xml
            TextView text, user, time;
            text = (TextView) v.findViewById(R.id.text21);
            user = (TextView) v.findViewById(R.id.user1);
            time = (TextView) v.findViewById(R.id.time21);

            text.setText(model.getMessageText());
            user.setText(model.getMessageUser());
            time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

        }
    };
    listOfMessage2.setAdapter(adapter2);
    listOfMessage3.setAdapter(adapter3);

}
    }

}