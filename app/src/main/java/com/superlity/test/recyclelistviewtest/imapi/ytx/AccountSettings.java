package com.superlity.test.recyclelistviewtest.imapi.ytx;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lion on 2015/8/12 0012.
 *
 */
public class AccountSettings {

    private static final String NAME = "AccountSettings";
    private static final String MY_ACCOUNT = "my_account";

    private static AccountSettings instance;
    private Context context;
    private SharedPreferences prefrences;

    public static AccountSettings getInstance( Context context ) {
        if ( instance == null ){
            instance = new AccountSettings( context );
        }
        return instance;
    }

    private AccountSettings(Context context) {
        this.context = context;
        prefrences = context.getSharedPreferences( NAME, Context.MODE_PRIVATE );
    }

    private SharedPreferences.Editor getEditor(){
        return prefrences.edit();
    }

    public synchronized void setMyAccount( String account ){
        if ( account == null ){
            return;
        }

        getEditor().putString( MY_ACCOUNT, account ).commit();
    }

    public synchronized String getMyAccount(){
        return prefrences.getString( MY_ACCOUNT, "" );
    }
}
