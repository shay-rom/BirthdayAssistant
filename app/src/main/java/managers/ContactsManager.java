package managers;

import android.database.Cursor;
import android.util.SparseArray;

import java.util.List;

import utils.AskRunTimePermissionsUtils;
import utils.BaseActivity;
import utils.CollectionUtils;
import utils.ContactNativeDataUpdater;
import utils.ContactUtils;
import utils.RuntimePermissionRequester;

public class ContactsManager {
    private static ContactsManager instance;
    public static final int LOAD_ALL_CONTACTS = 0;
    public static final int LOAD_ALL_CONTACTS_WITH_BIRTHDAYS = 1;
    public static final int LOAD_JUST_CONTACTS_WITH_BIRTHDAYS = 2;

    public interface ContactLoaded {
        void onContactLoaded(List<ContactUtils.Contact> contacts, int whatWasLoaded);
    }

    private final SparseArray<List<ContactUtils.Contact>> contactLoadedMap = new SparseArray<>();

    public static ContactsManager get() {
        if(instance == null) {
            synchronized (ContactsManager.class) {
                instance = new ContactsManager();
            }
        }
        return instance;
    }

    public void loadContacts(RuntimePermissionRequester reqSender, final int whatToLoad, final ContactLoaded listener){
        reqSender.requestRuntimePermission(AskRunTimePermissionsUtils.Permission.ReadContacts, new BaseActivity.PermissionResult() {
            @Override
            public void onPermissionGranted() {
                loadContacts(whatToLoad, listener);
            }
        });
    }

    public void insertContactEvent(RuntimePermissionRequester reqSender, final Long contactId, final int event, final String data){
        reqSender.requestRuntimePermission(AskRunTimePermissionsUtils.Permission.WriteContacts, new BaseActivity.PermissionResult() {
            @Override
            public void onPermissionGranted() {
                long rawContactId = ContactUtils.getRawContactId(contactId);
                if(rawContactId != -1) {
                    ContactNativeDataUpdater.insertOrUpdateBirthDateEvent(rawContactId, data);
                }
            }
        });
    }

    public void insertContactData(RuntimePermissionRequester reqSender, final Long contactId, final String mimeType, final String data){
        reqSender.requestRuntimePermission(AskRunTimePermissionsUtils.Permission.WriteContacts, new BaseActivity.PermissionResult() {
            @Override
            public void onPermissionGranted() {
                long rawContactId = ContactUtils.getRawContactId(contactId);
                if(rawContactId != -1) {
                    ContactNativeDataUpdater.insertType(rawContactId, mimeType, data);
                }
            }
        });
    }

    public void getContactData(RuntimePermissionRequester reqSender, final Long contactId, final String mimeType){
        reqSender.requestRuntimePermission(AskRunTimePermissionsUtils.Permission.ReadContacts, new BaseActivity.PermissionResult() {
            @Override
            public void onPermissionGranted() {
                long rawContactId = ContactUtils.getRawContactId(contactId);
                if(rawContactId != -1) {
                    ContactNativeDataUpdater.readType(rawContactId, mimeType);
                }
            }
        });
    }

    private void loadContacts(final int whatToLoad, final ContactLoaded listener){
        //check if already loaded
        List<ContactUtils.Contact> loadedContacts = contactLoadedMap.get(whatToLoad);
        if(CollectionUtils.isNotEmpty(loadedContacts)) {
            //contacts already loaded - notify right away
            if(listener != null) {
                listener.onContactLoaded(loadedContacts, whatToLoad);
            }
        } else {
            //need to load contacts
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<ContactUtils.Contact> contacts;
                    switch(whatToLoad) {
                        case LOAD_ALL_CONTACTS:
                            contacts = ContactUtils.getAllContactsList();
                            break;
                        case LOAD_ALL_CONTACTS_WITH_BIRTHDAYS:
                            contacts = ContactUtils.getAllContactsWithBirthdaysList();
                            break;
                        case LOAD_JUST_CONTACTS_WITH_BIRTHDAYS:
                            contacts = ContactUtils.getJustContactsWithBirthdayList();
                            break;
                        default:
                            contacts = null;
                            break;
                    }

                    contactLoadedMap.put(whatToLoad, contacts);

                    if(listener != null) {
                        listener.onContactLoaded(contacts, whatToLoad);
                    }
                }
            }).start();
        }
    }
}
