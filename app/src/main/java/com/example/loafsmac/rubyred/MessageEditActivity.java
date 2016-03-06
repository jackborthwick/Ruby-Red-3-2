package com.example.loafsmac.rubyred;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MessageEditActivity extends AppCompatActivity {
    String message;
    ImageButton editBUTTON;
    EditText editTEXT;
    TextView ViewCurrentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_edit);
//        saveMessageToParse("Almost done with basic functionality");
//        saveMessageToParse("Almost done with basic functionality");
        //updateMessageOrCreate("updating smartly");
        loadMessage();

        editTEXT = (EditText) findViewById(R.id.editText);
        editBUTTON = (ImageButton) findViewById(R.id.editButton);
        ViewCurrentMessage = (TextView) findViewById(R.id.current_txt);

        editBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTEXT.getTextSize() == 0){
                    Toast.makeText(MessageEditActivity.this, "HA... Try Again.", Toast.LENGTH_SHORT).show();
                }
                else{
//                    saveMessageToParse(editTEXT.getText().toString());
                    ViewCurrentMessage.setText(editTEXT.getText().toString());
                    updateMessageOrCreate(editTEXT.getText().toString());
//                    loadMessage();
                }
            }
        });

    }


//helper for updateMessageOrCreate
    public void saveMessageToParse(String messageInput){
        ParseObject numberObject = new ParseObject("Message");
        numberObject.put("message", messageInput);
        numberObject.saveInBackground();
    }
    public void updateMessageOrCreate(final String messageInput) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> numberPListToDelete, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < numberPListToDelete.size(); i++) {
                        numberPListToDelete.get(i).deleteEventually();
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
                saveMessageToParse(messageInput);
            }
        });
    }
    public void loadMessage () {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> numberPListToDelete, ParseException e) {//after this message string is useable
                if (e == null) {
                    for (int i = 0; i < numberPListToDelete.size(); i++){
                        message = numberPListToDelete.get(i).getString("message");
                        ViewCurrentMessage.setText(message);
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(MessageEditActivity.this, MainActivity.class);
        MessageEditActivity.this.startActivity(intent);
        MessageEditActivity.this.finish();
    }

}
