package com.srh.birthdayassistant;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import managers.FeedbackManager;
import managers.SharedPrefManager;
import utils.BaseActivity;
import utils.CalendarUtils;
import utils.ContactNativeDataUpdater;
import utils.ContactUtils;
import managers.ContactsManager;
import utils.RuntimePermissionRequester;
import utils.StringUtils;

import static utils.CalendarUtils.getFormatedBirthdate;

public class ContactListFragment extends Fragment implements ContactsManager.ContactLoaded{
    private RecyclerView rv;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the super method first
        super.onActivityCreated(savedInstanceState);
        // Initializes the loader
        ContactsManager.get().loadContacts((BaseActivity)getActivity(), ContactsManager.LOAD_ALL_CONTACTS_WITH_BIRTHDAYS, ContactListFragment.this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contact_list_fragment,
                container, false);

        setupRecyclerView(root);
        ((ContactCardsAdapter)rv.getAdapter()).setEventsListener(new ContactCardsAdapter.ContactClickdEvents() {
            @Override
            public void onContactRowClicked(final int pos, final ContactUtils.Contact contact) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String formattedBirthDate = getFormatedBirthdate(year, month, day);
                        contact.setBirthDate(formattedBirthDate);
                        getAdapter().notifyItemChanged(pos);

                        saveContactBirthday(contact, formattedBirthDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();

            }
        });

        return root;
    }

    private void saveContactBirthday(ContactUtils.Contact contact, String birthDate) {
        //SharedPrefManager.put(contact.lookupKey,formattedBirthDate);
        ContactsManager.get().insertContactData((BaseActivity)getActivity(), contact.contactId, Constants.MIME_TYPE_BIRTHDATE, birthDate);

    }

    public ContactCardsAdapter getAdapter(){
        return (ContactCardsAdapter) rv.getAdapter();
    }

    public void setupRecyclerView(View root) {
        rv = (RecyclerView) root.findViewById(R.id.contacts_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(root.getContext()));
        rv.setAdapter(new ContactCardsAdapter(null));
    }

    @Override
    public void onContactLoaded(final List<ContactUtils.Contact> contacts, int whatWasLoaded) {
        //notify data set changed
        Collections.sort(contacts, new Comparator<ContactUtils.Contact>() {
            @Override
            public int compare(ContactUtils.Contact contact, ContactUtils.Contact other) {
                if(contact == null || StringUtils.isEmpty(contact.getName())){
                    //contact does not have a name - move it further down the list
                    return 1;
                }
                if(other == null || StringUtils.isEmpty(other.getName())){
                    //other does not have a name - move it further down the list
                    return -1;
                }

                boolean meHasBirthday = !StringUtils.isEmpty(contact.getBirthDate());
                boolean otherHasBirthday = !StringUtils.isEmpty(other.getBirthDate());

                if(meHasBirthday && !otherHasBirthday) {
                    //contact has birthday and the other does not, move it further down the list
                    return -1;
                }

                if(!meHasBirthday && otherHasBirthday) {
                    //other contact has birthday and contact has not, move it further down the list
                    return 1;
                }

                //sort it alphabetically according to its name
                return contact.getName().compareTo(other.getName());
            }
        });
        FeedbackManager.showToast("contacts loaded: " + contacts.size());
        App.get().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ContactCardsAdapter)rv.getAdapter()).setNewData(contacts);
            }
        });
    }
}
