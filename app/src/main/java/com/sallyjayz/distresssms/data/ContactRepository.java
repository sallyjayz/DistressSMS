package com.sallyjayz.distresssms.data;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.sallyjayz.distresssms.model.DistressContact;
import java.util.List;

public class ContactRepository {

    private ContactDao mContactDao;
    private LiveData<List<DistressContact>> mAllDistressContacts;
    private LiveData<Integer> mAllCount;
    private LiveData<List<String>> mAllPhoneNumbers;

    public ContactRepository(Application application) {
        DistressAppDatabase db = DistressAppDatabase.getDatabase(application);
        mContactDao = db.contactDao();
        mAllDistressContacts = mContactDao.getAllContacts();
        mAllCount = mContactDao.getAllCount();
        mAllPhoneNumbers = mContactDao.getAllPhoneNumbers();
    }

    public LiveData<List<DistressContact>> getAllDistressContacts() {
        return mAllDistressContacts;
    }

    public LiveData<Integer> getAllCount() {
        return mAllCount;
    }

    public LiveData<List<String>> getAllPhoneNumbers() {
        return mAllPhoneNumbers;
    }

    public void insert (DistressContact distressContact) {
        new insertAsyncTask(mContactDao).execute(distressContact);
    }

    public  void deleteAll() {
        new deleteAllContactsAsyncTask(mContactDao).execute();
    }

    public void deleteContact(DistressContact distressContact) {
        new deleteContactAsyncTask(mContactDao).execute(distressContact);
    }

    private static class insertAsyncTask extends AsyncTask<DistressContact, Void, Void> {

        private ContactDao mAsyncTaskDao;

        public insertAsyncTask(ContactDao contactDao) {
            mAsyncTaskDao = contactDao;
        }

        @Override
        protected Void doInBackground(final DistressContact... distressContacts) {
            mAsyncTaskDao.insert(distressContacts[0]);
            return null;
        }
    }

    private static class deleteAllContactsAsyncTask extends AsyncTask<Void, Void, Void> {

        private ContactDao mContactDao;

        deleteAllContactsAsyncTask(ContactDao contactDao) {
            mContactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mContactDao.deleteAll();
            return null;
        }
    }

    private static class deleteContactAsyncTask extends AsyncTask<DistressContact, Void, Void> {

        private ContactDao mContactDao;

        deleteContactAsyncTask(ContactDao contactDao) {
            mContactDao = contactDao;
        }

        @Override
        protected Void doInBackground(DistressContact... distressContacts) {
            mContactDao.deleteContact(distressContacts[0]);
            return null;
        }
    }
}
