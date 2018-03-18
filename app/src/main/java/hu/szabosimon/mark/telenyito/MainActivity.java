package hu.szabosimon.mark.telenyito;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    String gatePhoneNumber;
    private static final int PICK_CONTACT_REQUEST = 1001;
    private static final String GATE_PHONE_NUMBER = "GatePhoneNumber";
    private static final String NEARBY = "Nearby";

    Button btnTest;
    Button btnPickContact;
    EditText ET_phonenumber;
    CheckBox CB_nearby;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        //create UI element variables
        btnTest = findViewById(R.id.btnTest);
        btnPickContact = findViewById(R.id.btnPickContact);
        ET_phonenumber = findViewById(R.id.ET_phonenumber);
        CB_nearby = findViewById(R.id.CB_nearby);

        //set listeners
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendNotification();
            }
        });
        btnPickContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pickContact();
            }
        });
        ET_phonenumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                gatePhoneNumber = s.toString();
                editor.putString(GATE_PHONE_NUMBER,s.toString());
                editor.apply();
            }
        });
        CB_nearby.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //save the setting
                editor.putBoolean(NEARBY,isChecked);
                editor.apply();

                if ( isChecked )
                {
                    Log.d("abcd","Checked");
                } else {
                    Log.d("abcd","Not checked");
                }

            }
        });

        //load settings and fill the form
        gatePhoneNumber = pref.getString(GATE_PHONE_NUMBER, "");
        ET_phonenumber.setText(gatePhoneNumber);
        ET_phonenumber.setSelection(ET_phonenumber.getText().length()); //place cursor to the end of the edittext
        CB_nearby.setChecked(pref.getBoolean(NEARBY,false));

    }

    public void sendNotification() {
        String CHANNEL_ID = "0";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID);

        //Create the intent that’ll fire when the user taps the notification//
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + gatePhoneNumber));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        mBuilder.setContentTitle("A kapu nyitásához kattints ide");
        mBuilder.setContentText("A " + gatePhoneNumber + " hívása");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                // Do something with the phone number...
                Log.d("abc123",number);
                gatePhoneNumber = number;
                ET_phonenumber.setText(number);

            }
        }
    }

}
