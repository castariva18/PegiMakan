package com.suncode.pegimakan.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    public Session(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setLoginUser(User user) {
        editor.putString("USER_LVL_USER", user.lvl);
        editor.putString("USER_LVL_LOGIN", user.lvl);
        editor.putString("KEY_IS_LOGIN", user.lvl);
        editor.commit();
    }

    public void setLoginAdmin(User Admin) {
        editor.putString("USER_LVL_ADMIN", Admin.lvl);
        editor.putString("USER_LVL_LOGIN", Admin.lvl);
        editor.putString("KEY_IS_LOGIN", Admin.lvl);
        editor.commit();
    }

    public void removeSession() {
        editor.clear();
        editor.commit();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}