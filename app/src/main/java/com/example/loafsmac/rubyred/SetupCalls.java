package com.example.loafsmac.rubyred;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by bertolopez-cruz on 2/9/16.
 */
public class SetupCalls extends Activity{
    TextView numberTextView;
    ArrayList<SaveContact> selectUsers = new ArrayList<>();
    ArrayList<String[]> numberList;
    List<SaveContact> temp;
    SearchView search;
    SaveContactAdapter adapter;
    ListView listView;
    EditText nameTextEdit;
    EditText numberTextEdit;

    Cursor phones;
    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setup_calls);
//        displayContacts();
//      Button view = (Button)findViewById(R.id.viewButton);
        Button add = (Button)findViewById(R.id.createButton);
        Button delete = (Button)findViewById(R.id.deleteButton);
        delete.setVisibility(View.INVISIBLE);
        numberTextView = (TextView)findViewById(R.id.thisview);
        nameTextEdit= (EditText)findViewById(R.id.namTextEdit);
        numberTextEdit= (EditText)findViewById(R.id.numTextEdit);
//        listView = (ListView) findViewById(R.id.contacts_list);

        loadAllNumbersFromParse();
//        saveNumberToParse("7034737666","Jack");
//        saveNumberToParse("7034737666","Jack");

//        deleteFromParse("703473766");

        /**
        search = (SearchView) findViewById(R.id.searchView);

        selectUsers = new ArrayList<SaveContact>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);
         */

//        view.setOnClickListener(new OnClickListener() {
//            public void onClick(View v){
//                displayContacts();
//                Log.i("NativeContentProvider", "Completed Displaying Contact list");
//            }
//        });

        add.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                if(nameTextEdit.getTextSize() == 0 && numberTextEdit.getTextSize()==0){
                    Toast.makeText(SetupCalls.this, "HA... Try Again.", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveNumberToParse(numberTextEdit.getText().toString(), nameTextEdit.getText().toString());
                    loadAllNumbersFromParse();
                }
            }
        });

//        search = (SearchView) findViewById(R.id.searchView);
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // TODO Auto-generated method stub
//                adapter.filter(newText);
//                return false;
//            }
//        });

    }
    public void saveNumberToParse(String number, String name){
        ParseObject numberObject = new ParseObject("Number");
        numberObject.put("number", number);
        numberObject.put("name", name);
        numberObject.saveInBackground();
    }
    public void loadAllNumbersFromParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Number");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> numberPList, ParseException e) {//this line is when request comes back
                if (e == null) {
                    Log.d("Number", "Retrieved " + numberPList.size() + " numbers");
                    numberList = new ArrayList<>();
                    for (int i = 0; i < numberPList.size(); i++) {//parsing pfobjects
                        String [] indivNumberArray = {numberPList.get(i).getString("number"),numberPList.get(i).getString("name")};
                        numberList.add(indivNumberArray);
                    }
                    //this is when you can use numberList (arraylist of string[number,name]
                    numberTextView.setText(numberList.get(0)[0] + " " + numberList.get(0)[1]);
                } else {
                    Log.d("location", "Error: " + e.getMessage());
                }
            }
        });
    }
    public void deleteFromParse(String number) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Number");
        query.whereEqualTo("number", number);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> numberPListToDelete, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + numberPListToDelete.size() + " to delete");
                    for (ParseObject parseObject : numberPListToDelete){
                        parseObject.deleteEventually();
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
//    contactNumber = (TextView)findViewById(R.id.contactnumber);
//
//    Button buttonPickContact = (Button)findViewById(R.id.pickcontact);
//    buttonPickContact.setOnClickListener(new Button.OnClickListener(){
//
//        @Override
//        public void onClick(View arg0) {
//            // TODO Auto-generated method stub
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
//            startActivityForResult(intent, 1);
//        }});
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == RQS_PICK_CONTACT){
//            if(resultCode == RESULT_OK){
//                Uri contactData = data.getData();
//                Cursor cursor =  managedQuery(contactData, null, null, null, null);
//                cursor.moveToFirst();
//
//                String number =       cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                //contactName.setText(name);
//                contactNumber.setText(number);
//                //contactEmail.setText(email);
//            }
//        }
//    }


    private void displayContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (phoneCur.moveToNext()) {
                        String phoneNumber = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(SetupCalls.this, "Name: " + name + ", Phone No: " + phoneNumber, Toast.LENGTH_SHORT).show();

                        SaveContact selectUser = new SaveContact();
                        selectUser.setName(name);
                        selectUser.setPhone(phoneNumber);
                        selectUser.setCheckedBox(false);
                        selectUsers.add(selectUser);
                    }
                    phoneCur.close();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            SaveContact data = selectUsers.get(i);
                        }
                    });

                    listView.setFastScrollEnabled(true);
                }
            }
        }
    }

//    @Override
//    private void displayList(){
//
//        adapter = new SaveContactAdapter(SaveContact, SetupCalls.this);
//        listView.setAdapter(adapter);
//
//        // Select item on listclick
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                SaveContact data = selectUsers.get(i);
//            }
//        });
//
//        listView.setFastScrollEnabled(true);
//    }

    private void createContact(String name, String phone) {
        ContentResolver cr = getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String existName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (existName.contains(name)) {
                    Toast.makeText(SetupCalls.this,"The contact name: " + name + " already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "accountname@gmail.com")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "com.google")
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());


        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(SetupCalls.this, "Created a new contact with name: " + name + " and Phone No: " + phone, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(SetupCalls.this, MainActivity.class);
        SetupCalls.this.startActivity(intent);
        SetupCalls.this.finish();
    }

}


