package com.sallyjayz.distresssms.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sallyjayz.distresssms.data.ContactRepository;
import com.sallyjayz.distresssms.model.DistressContact;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private ContactRepository mRepository;
    private LiveData<List<DistressContact>> mAllDistressContacts;
    private LiveData<Integer> mAllCount;
    private LiveData<List<String>> mAllPhoneNumbers;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ContactRepository(application);
        mAllDistressContacts = mRepository.getAllDistressContacts();
        mAllCount = mRepository.getAllCount();
        mAllPhoneNumbers = mRepository.getAllPhoneNumbers();
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

    public void insert(DistressContact distressContact) {
        mRepository.insert(distressContact);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteContact(DistressContact distressContact) {
        mRepository.deleteContact(distressContact);
    }
}
