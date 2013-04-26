package com.example.ui;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactDB {
 
 public static final String COL_ID = "_id";
 public static final String COL_NAME = "name";
 public static final String COL_SIP = "sip";
 public static final String COL_IMAGE = "image";
 private static final int NUM_COL_ID = 0;
 private static final int NUM_COL_NAME = 1;
 private static final int NUM_COL_SIP = 2;
 private static final int NUM_COL_IMAGE=3;
 
 private static final String TAG = "ContactDB";
 private DatabaseHelper mDbHelper;
 private SQLiteDatabase mDb;
 
 private static final String DATABASE_NAME = "DB";
 private static final String SQLITE_TABLE = "Contact";
 private static final int DATABASE_VERSION = 1;
 
 private final Context mCtx;
 
 private static final String DATABASE_CREATE =
  "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
  COL_ID + " integer PRIMARY KEY autoincrement," + COL_SIP + " TEXT NOT NULL, "
	+ COL_NAME + " TEXT NOT NULL, "+ COL_IMAGE +");";
 
 private static class DatabaseHelper extends SQLiteOpenHelper {
 
  DatabaseHelper(Context context) {
   super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
 
 
  @Override
  public void onCreate(SQLiteDatabase db) {
   Log.w(TAG, DATABASE_CREATE);
   db.execSQL(DATABASE_CREATE);
  }
 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
   Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
     + newVersion + ", which will destroy all old data");
   db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
   onCreate(db);
  }
 }
 
 public ContactDB(Context ctx) {
  this.mCtx = ctx;
 }
 
 public ContactDB open() throws SQLException {
  mDbHelper = new DatabaseHelper(mCtx);
  mDb = mDbHelper.getWritableDatabase();
  return this;
 }
 
 public void close() {
  if (mDbHelper != null) {
   mDbHelper.close();
  }
 }
 
 public long createContact(String name, 
   String sip,String image){
 
  ContentValues initialValues = new ContentValues();
  initialValues.put(COL_NAME, name);
  initialValues.put(COL_SIP, sip);
  initialValues.put(COL_IMAGE, image);
 
  return mDb.insert(SQLITE_TABLE, null, initialValues);
 }
 
 public boolean deleteAllContacts() {
 
  int doneDelete = 0;
  doneDelete = mDb.delete(SQLITE_TABLE, null , null);
  Log.w(TAG, Integer.toString(doneDelete));
  return doneDelete > 0;
 
 }
 
 public Cursor fetchContactByName(String inputText) throws SQLException {
  Log.w(TAG, inputText);
  Cursor mCursor = null;
  if (inputText == null  ||  inputText.length () == 0)  {
   mCursor = mDb.query(SQLITE_TABLE, new String[] {COL_ID,COL_NAME, COL_SIP,COL_IMAGE}, 
     null, null, null, null, null);
 
  }
  else {
   mCursor = mDb.query(true, SQLITE_TABLE, new String[] {COL_ID,COL_NAME, COL_SIP,COL_IMAGE}, 
     COL_NAME + " like '%" + inputText + "%'", null,
     null, null, null, null);
  }
  if (mCursor != null) {
   mCursor.moveToFirst();
  }
  return mCursor;
 
 }
 
 public Cursor fetchAllContacts() {
 
  Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {COL_ID,COL_NAME, COL_SIP,COL_IMAGE}, 
    null, null, null, null, null);
 
  if (mCursor != null) {
   mCursor.moveToFirst();
  }
  return mCursor;
 }
 private Contact cursorToContact(Cursor c){
		
		if (c.getCount() == 0)
			return null;

		c.moveToFirst();
		Contact contact = new Contact();
		contact.setId(c.getInt(NUM_COL_ID));
		contact.setName(c.getString(NUM_COL_NAME));
		contact.setSip(c.getString(NUM_COL_SIP));
		contact.setImage(c.getString(NUM_COL_IMAGE));
		c.close();
		return contact;
	}
 public Contact getContactWithName(String n){
		
		Cursor c = mDb.query(SQLITE_TABLE, new String[] {COL_ID,COL_NAME, COL_SIP,COL_IMAGE}, COL_NAME + " LIKE \"" + n +"\"", null, null, null, null);
		return cursorToContact(c);
	}

 public int removeContactwithName(String n){
		
		return mDb.delete(SQLITE_TABLE, COL_ID+ " = " +getContactWithName(n).getId(), null);
	}
}
