package com.suncode.pegimakan.utils;

public interface LoginViewCallback {
    void onSuccessAuthFirebase(String uid);
    void onFailedAuthFirebase();
    void onShowProgress();
    void onHideProgress();

}