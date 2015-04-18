package edu.nyu.scps.cursoradapter;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private Helper helper;
    private SimpleCursorAdapter adapter;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    //public GestureDetector gestureScanner;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            throw new RuntimeException();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContacts();
                setContentView(R.layout.contacts_list);
                Toast.makeText(MainActivity.this, "Loading contacts...", Toast.LENGTH_LONG).show();
            }
        });

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewList();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_append) {
            /*String name = getString("", "Enter new contact name");
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            SQLiteDatabase database = helper.getWritableDatabase();
            database.insert("people", null, contentValues);
            adapter.changeCursor(helper.getCursor());*/
            createEntry();
            return true;
        }

        if (id == R.id.action_delete_all) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete("people", null, null);  //Delete all records!
            adapter.changeCursor(helper.getCursor());
            return true;
        }

        if(id == R.id.action_edit_entry) {
            String newId = "_id = "+ getString("ID", "Enter entry id to edit");
            String newName = getString("Name", "Enter new name");
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", newName);
            SQLiteDatabase database = helper.getWritableDatabase();
            database.update("people", contentValues, newId, null);
            adapter.changeCursor(helper.getCursor());
            return true;
        }

        if(id == R.id.action_sort_alpha) {
            SQLiteDatabase database = helper.getWritableDatabase();
            adapter.changeCursor(helper.sortByName());
            return true;
        }

        if(id == R.id.action_sort_id) {
            SQLiteDatabase database = helper.getWritableDatabase();
            adapter.changeCursor(helper.sortById());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String mResult;

    public String getString(String title, String message) {

        //A builder object can create a dialog object.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        //This inflator reads the dialog.xml and creates the objects described therein.
        //Pass null as the parent view because it's going in the dialog layout.
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog, null);
        builder.setView(view);

        //Must be final to be mentioned in the anonymous inner class.
        final EditText editText = (EditText)view.findViewById(R.id.editText);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == EditorInfo.IME_ACTION_DONE) ||(event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Editable editable = editText.getText();
                    String string = editable.toString();
                    mResult = string;

                    Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_LONG).show();

                    //Sending this message will break us out of the loop below.
                    Message message = handler.obtainMessage();
                    handler.sendMessage(message);
                }
                return false;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //Loop until the user presses the EditText's Done button.
        try {
            Looper.loop();
        }
        catch(RuntimeException runtimeException) {
        }

        alertDialog.dismiss();
        return mResult;
    }

    public void viewList(){
        setContentView(R.layout.contacts_list);

        final ListView listView = (ListView)findViewById(R.id.listView);

        helper = new Helper(this);
        Cursor cursor = helper.getCursor();

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[] {"name",             "phone"},
                new int[]    {android.R.id.text1, android.R.id.text2},
                0	//don't need any flags
        );

        listView.setAdapter(adapter);

        //Display a message when the table contains no rows.

        LayoutInflater inflater = getLayoutInflater();
        TextView textView = (TextView)inflater.inflate(R.layout.empty, null);

        ViewGroup viewGroup = (ViewGroup)findViewById(android.R.id.content); //Get the RelativeLayout.
        viewGroup.addView(textView);
        listView.setEmptyView(textView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position); //downcast
                int indexDisplayName = cursor.getColumnIndex("name");
                String name = cursor.getString(indexDisplayName);
                String s = name + ", position = " + position + ", id = " + id + ".";
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

                //editEntry((int)id);

                /*SQLiteDatabase database = helper.getWritableDatabase();
                database.delete("people", "_id = ?", new String[] {Long.toString(id)});
                adapter.changeCursor(helper.getCursor());*/
            }
        });

        listView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                int position = listView.pointToPosition((int) x_pos, (int) y_pos);
                int id = (int) listView.getItemIdAtPosition(position);
                editName(id);

            }

            public void onSwipeLeft() {
                //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                int position = listView.pointToPosition((int) x_pos, (int) y_pos);
                int id = (int) listView.getItemIdAtPosition(position);
                String s = "" + id;
                deleteEntry(id);
            }

            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            public void onClickLong() {
                int position = listView.pointToPosition((int) x_pos, (int) y_pos);
                int id = (int) listView.getItemIdAtPosition(position);
                editPhone(id);
            }

            public void onClickDouble(){
                int position = listView.pointToPosition((int) x_pos, (int) y_pos);
                int id = (int) listView.getItemIdAtPosition(position);
                editEntry(id);
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

    }

    public void createEntry() {
        String newName = getString("Name", "Enter new name");
        String newPhone = getString("Phone", "Enter new phone number");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", newName);
        contentValues.put("phone", newPhone);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.insert("people", null, contentValues);
        adapter.changeCursor(helper.getCursor());
    }

    public void editEntry(int id){
        String newName = getString("Name", "Enter new name");
        String newPhone = getString("Phone", "Enter new phone number");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", newName);
        contentValues.put("phone", newPhone);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.update("people", contentValues, "_id = "+id, null);
        adapter.changeCursor(helper.getCursor());
    }

    public void editName(int id){
        String newName = getString("Name", "Enter new name");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", newName);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.update("people", contentValues, "_id = "+id, null);
        adapter.changeCursor(helper.getCursor());
    }

    public void editPhone(int id){
        String newPhone = getString("Phone", "Enter new phone number");
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", newPhone);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.update("people", contentValues, "_id = "+id, null);
        adapter.changeCursor(helper.getCursor());
    }

    public void deleteEntry(int id){
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete("people", "_id = " + id, null);
        adapter.changeCursor(helper.getCursor());
    }

    public void addContacts() {
        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        if(cursor.getCount() > 0){
            while(cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        Toast.makeText(MainActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
                        helper = new Helper(this);
                        helper.addContact(name, phoneNumber);
                        adapter.changeCursor(helper.getCursor());
                    }
                    phoneCursor.close();
                }
            }
        }
    }
}
