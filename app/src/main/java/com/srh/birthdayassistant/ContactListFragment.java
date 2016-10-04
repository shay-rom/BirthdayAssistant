package com.srh.birthdayassistant;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import managers.ContactsManager;
import managers.FeedbackManager;
import utils.BaseActivity;
import utils.BirthdayUtils;
import utils.CalendarUtils;
import utils.ContactUtils;
import utils.Resources;
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
                String birthDate = contact.getBirthDateStr();
                if(StringUtils.isNotEmpty(birthDate)) {
                    calendar.setTime(Date.valueOf(birthDate));
                }
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    private boolean alreadyDateSet = false;//allow to set date only once - called several two times when clicking the ok button
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        if(alreadyDateSet){
                            return;
                        }
                        alreadyDateSet = true;

                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        if(CalendarUtils.isFutureDate(c.getTime())){
                            FeedbackManager.showToast(R.string.cannot_set_birthday_as_future_date);
                            return;
                        }

                        String formattedBirthDate = getFormatedBirthdate(year, month, day);

                        if(!formattedBirthDate.equals(contact.getBirthDateStr())) {
                            contact.setBirthDate(formattedBirthDate);
                            getAdapter().notifyItemChanged(pos);

                            saveContactBirthday(contact, formattedBirthDate);
                        }

                        Calendar eventDate = BirthdayUtils.getCorrectYearForFutureBirthday(CalendarUtils.getCalendarFromDate(formattedBirthDate));
                        eventDate.set(Calendar.HOUR_OF_DAY, 10);
                        eventDate.set(Calendar.MINUTE, 0);

                        CalendarUtils.startCalendar(getActivity(),
                                Resources.getString(R.string.add_birthday_to_calendar_title, contact.getName()),
                                eventDate);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.setTitle(Resources.getString(R.string.dialog_birthdate_picker_title, contact.getName()));
                dialog.show();

            }
        });

        return root;
    }

    private void saveContactBirthday(ContactUtils.Contact contact, String birthDate) {
        //SharedPrefManager.put(contact.lookupKey,formattedBirthDate);
        ContactsManager.get().insertContactEvent((BaseActivity)getActivity(), contact.contactId, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, birthDate);

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
            private Calendar c1 = Calendar.getInstance();
            private Calendar c2 = Calendar.getInstance();
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

                if(contact.hasBirthday()) {
                    if(!other.hasBirthday()) {
                        //contact has birthday and the other does not, move it further down the list
                        return -1;
                    } else {
                        //both has birthday
                        c1.setTime(contact.getBirthDate());
                        c2.setTime(other.getBirthDate());
                        c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
                        return c1.after(c2) ? -1 : 1;
                    }
                } else if(other.hasBirthday()){
                    //only other has birthdate - move him further down the list
                    return 1;
                } else {
                    //both dont have birthday - sort it alphabetically according to its name
                    return contact.getName().compareTo(other.getName());
                }
            }
        });
        FeedbackManager.debug("contacts loaded: " + contacts.size());
        App.get().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ContactCardsAdapter)rv.getAdapter()).setNewData(contacts);
            }
        });
    }
}
