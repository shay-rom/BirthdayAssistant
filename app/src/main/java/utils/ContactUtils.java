package utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.LongSparseArray;

import com.srh.birthdayassistant.App;

import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

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

    public static String getEventStartDate(long rawContactId, int eventType) {
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.TYPE,
        };

        String where = ContactsContract.CommonDataKinds.Event.RAW_CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + " = " +
                        eventType;
        String[] selectionArgs = new String[] {
                ""+rawContactId, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };

        Cursor c = query(uri, projection, where, selectionArgs, null);

        String startDate = null;
        if (c != null) {
            if(c.moveToFirst()) {
                startDate = c.getString(0);
            }
            c.close();
        }
        return startDate;
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
        Cursor cTheDevice = getJustContactsBirthdaysNative();
        List<Contact> list = CursorsToList(cTheDevice);
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
        Cursor cAllContacts = getAllContacts();
        List<Contact> list = CursorsToList(cAllContacts, cJustBirthdaysDevice);

        cJustBirthdaysDevice.close();
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
        private Uri thumbnailUri;
        private BirthDateData birthDateData;

        public void setBirthDate(String birthDate) {
            birthDateData = StringUtils.isNotEmpty(birthDate) ? new BirthDateData(birthDate) : null;
        }

        public java.util.Date getBirthDate() {
            return birthDateData != null ? birthDateData.birthDate : null;
        }

        private class BirthDateData{
            private final String birthDateStr;
            private final String birthDateStrWithOutYear;
            private final String age;
            private final String ageNextYear;
            private final Date birthDate;

            BirthDateData(String birthDateStr){
                this.birthDateStr = birthDateStr;
                this.birthDate = StringUtils.isEmpty(birthDateStr) ? null : Date.valueOf(birthDateStr);
                if(birthDate != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(birthDate);
                    birthDateStrWithOutYear = CalendarUtils.getFormatedBirthdate(c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    int ageNum = BirthdayUtils.getAge(birthDate);
                    age = String.valueOf(ageNum);
                    ageNextYear = String.valueOf(ageNum + 1);
                } else {
                    birthDateStrWithOutYear = null;
                    age = null;
                    ageNextYear = null;
                }
            }
        }

        Contact(Cursor... cursor){
            String cId = getStringColumn(ContactsContract.CommonDataKinds.Event.CONTACT_ID, cursor);
            contactId = StringUtils.isEmpty(cId) ? null : Long.parseLong(cId);

            lookupKey = getStringColumn(ContactsContract.CommonDataKinds.Event.LOOKUP_KEY, cursor);

            String thumbnailPath = getStringColumn(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, cursor);
            thumbnailUri = StringUtils.isEmpty(thumbnailPath) ? null : Uri.parse(thumbnailPath);

            name = getStringColumn(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, cursor);

            setBirthDate(getStringColumn(ContactsContract.CommonDataKinds.Event.START_DATE, cursor));
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

            if(birthDateData == null) {
                birthDateData = other.birthDateData;
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

        public String getBirthDateStr() {
            return birthDateData != null ? birthDateData.birthDateStr : null;
        }

        public String getBirthDateStrWithOutYear() {
            return birthDateData != null ? birthDateData.birthDateStrWithOutYear : null;
        }

        public Uri getThumbnailUri() {
            return thumbnailUri;
        }

        public boolean hasBirthday(){
            return birthDateData != null && StringUtils.isNotEmpty(birthDateData.birthDateStr);
        }

        public String getAge() {
            return birthDateData != null ? birthDateData.age : null;
        }

        public String getAgeNextYear() {
            return birthDateData != null ? birthDateData.ageNextYear : null;
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
