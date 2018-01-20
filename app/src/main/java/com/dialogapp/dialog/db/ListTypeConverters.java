package com.dialogapp.dialog.db;

import android.arch.persistence.room.TypeConverter;

import com.dialogapp.dialog.model.AccountResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListTypeConverters {
    @TypeConverter
    public List<AccountResponse.PaidSite> stringToPaidSiteList(String data) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, AccountResponse.PaidSite.class);
        JsonAdapter<List<AccountResponse.PaidSite>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(data);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @TypeConverter
    public String PaidSiteListToString(List<AccountResponse.PaidSite> list) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, AccountResponse.PaidSite.class);
        JsonAdapter<List<AccountResponse.PaidSite>> adapter = moshi.adapter(type);
        return adapter.toJson(list);
    }

    @TypeConverter
    public List<AccountResponse.RssFeed> stringToRssFeedList(String data) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, AccountResponse.RssFeed.class);
        JsonAdapter<List<AccountResponse.RssFeed>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(data);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @TypeConverter
    public String RssFeedListToString(List<AccountResponse.RssFeed> list) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, AccountResponse.RssFeed.class);
        JsonAdapter<List<AccountResponse.RssFeed>> adapter = moshi.adapter(type);
        return adapter.toJson(list);
    }
}
