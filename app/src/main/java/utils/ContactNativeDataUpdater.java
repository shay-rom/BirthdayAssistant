package utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.srh.birthdayassistant.App;

public class ContactNativeDataUpdater {
    public static void insertType(long rawContactId, String mimeType, String data){
        insertOrUpdate(rawContactId, mimeType, data);
    }

    private static void insertOrUpdate(long rawContactId, String mimeType, String data){
        if(rawContactId == -1){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.MIMETYPE, mimeType);
        values.put(ContactsContract.Data.DATA1, data);

        String currentVal = readType(rawContactId, mimeType);
        if(StringUtils.isEmpty(currentVal)) {
            //no such value - need to insert
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);

            App.get().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        } else {
            //need to update
            String where = ContactsContract.Data.RAW_CONTACT_ID + "= ? AND " +
                    ContactsContract.Data.MIMETYPE + "= ?";
            String[] selectionArgs = {""+rawContactId, mimeType};

            App.get().getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where, selectionArgs);
        }
    }

    public static String readType(long rawContactId, String mimeType) {
        if(rawContactId == -1){
            return null;
        }

        Cursor c = App.get().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] {
                        ContactsContract.Data.DATA1
                },
                ContactsContract.Data.RAW_CONTACT_ID + "=" + rawContactId + " AND " + ContactsContract.Data.MIMETYPE + "= '" + mimeType
                        + "'", null, null);

        if(c != null && c.moveToFirst()) {
            return ContactUtils.Contact.getStringColumn(ContactsContract.Data.DATA1, c);
        } else {
            return null;
        }
    }
}
