package utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.LongSparseArray;

import com.srh.birthdayassistant.App;
import com.srh.birthdayassistant.Constants;

import java.util.ArrayList;
import java.util.List;

import managers.SharedPrefManager;

public class ContactUtils {

    private static Cursor getAllContacts(){
        String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Event.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
        };

        return query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);
    }

    private static Cursor getContactMimeTypeData(String[] projection, String mimeType){

        String where = ContactsContract.Contacts.Data.MIMETYPE + "= ?";

        String[] selectionArgs = {mimeType};

        return query(ContactsContract.Data.CONTENT_URI, projection, where, selectionArgs, null);

    }

    private static Cursor getJustContactsBirthdaysOurOwn() {
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.Contacts.Data.DATA1
        };
        return getContactMimeTypeData(projection, Constants.MIME_TYPE_BIRTHDATE);
    }

    private static Cursor getJustContactsBirthdaysNative() {
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.START_DATE,
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };

        return query(uri, projection, where, selectionArgs, null);
    }

    private static Cursor query(Uri uri, String[] projection, String where, String[] selectionArgs, String sortOrder){
        ContentResolver contentResolver = App.get().getContentResolver();
        return contentResolver.query(uri, projection, where, selectionArgs, sortOrder);
    }

    public static List<Contact> getJustContactsWithBirthdayList(){
        Cursor cOurs = getJustContactsBirthdaysOurOwn();
        Cursor cTheDevice = getJustContactsBirthdaysNative();
        List<Contact> list = CursorsToList(cOurs, cTheDevice);
        cOurs.close();
        cTheDevice.close();
        return list;
    }

    public static List<Contact> getAllContactsList(){
        Cursor c = getAllContacts();
        List<Contact> list = CursorToList(c);
        c.close();
        return list;
    }

    public static List<Contact> getAllContactsWithBirthdaysList(){
        Cursor cJustBirthdaysDevice = getJustContactsBirthdaysNative();
        Cursor cOurOwn = getJustContactsBirthdaysOurOwn();
        Cursor cAllContacts = getAllContacts();
        List<Contact> list = CursorsToList(cOurOwn, cAllContacts, cJustBirthdaysDevice);

        cJustBirthdaysDevice.close();
        cOurOwn.close();
        cAllContacts.close();

        return list;
    }

    public static long getRawContactId(Long contactId){
        long rawContactId = -1;

        if(contactId == null) {
            return rawContactId;
        }

        Cursor c = App.get().getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)}, null);

        if (c != null) {
            if(c.moveToFirst()) {
                rawContactId = c.getLong(0);
            }
            c.close();
        }
        return rawContactId;
    }

    public static class Contact{
        public final Long contactId;
        public final String lookupKey;
        private String name;
        private String birthDate;
        private Uri thumbnailUri;

        Contact(Cursor... cursor){
            String cId = getStringColumn(ContactsContract.CommonDataKinds.Event.CONTACT_ID, cursor);
            contactId = StringUtils.isEmpty(cId) ? null : Long.parseLong(cId);

            lookupKey = getStringColumn(ContactsContract.CommonDataKinds.Event.LOOKUP_KEY, cursor);

            String thumbnailPath = getStringColumn(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, cursor);
            thumbnailUri = StringUtils.isEmpty(thumbnailPath) ? null : Uri.parse(thumbnailPath);

            name = getStringColumn(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, cursor);

            birthDate = getStringColumn(ContactsContract.CommonDataKinds.Event.START_DATE, cursor);
        }

        public static String getStringColumn(String columnName, Cursor... cursor){
            for(Cursor c : cursor) {
                int nameIndex = c.getColumnIndex(columnName);
                if(nameIndex >= 0){
                    return c.getString(nameIndex);
                }
            }
            return null;
        }

        public void merge(Contact other){
            if(other == null) {
                return;
            }

            if(StringUtils.isEmpty(name)) {
                name = other.name;
            }

            if(birthDate == null) {
                birthDate = other.birthDate;
            }

            if(thumbnailUri == null) {
                thumbnailUri = other.thumbnailUri;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Contact contact = (Contact) o;

            if (contactId != null ? !contactId.equals(contact.contactId) : contact.contactId != null)
                return false;
            return true;

        }

        @Override
        public int hashCode() {
            return contactId != null ? contactId.hashCode() : 0;
        }

        public String getName() {
            return name;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public Uri getThumbnailUri() {
            return thumbnailUri;
        }


        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }
    }

    private static List<Contact> CursorsToList(Cursor... cursors){
        LongSparseArray<Contact> contactsMap = CursorToMap(cursors[0]);
        for(int i=1 ; i<cursors.length ; i++) {
            List<Contact> otherContactsList = CursorToList(cursors[i]);

            for(Contact otherContact : otherContactsList) {
                Contact contact = contactsMap.get(otherContact.contactId);
                if(contact != null) {
                    contact.merge(otherContact);
                } else {
                    contactsMap.put(otherContact.contactId, otherContact);
                }
            }
        }


        return CollectionUtils.asList(contactsMap);
    }

    private static LongSparseArray<Contact> CursorToMap(Cursor cursor){
        LongSparseArray<Contact> contactsList = new LongSparseArray<>();
        if(cursor.moveToFirst()) {
            do {
                Contact c = new Contact(cursor);
                contactsList.put(c.contactId, c);
            } while(cursor.moveToNext());
        }
        return contactsList;
    }

    private static List<Contact> CursorToList(Cursor cursor){
        List<Contact> contactsList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                contactsList.add(new Contact(cursor));
            } while(cursor.moveToNext());
        }
        return contactsList;
    }
}
