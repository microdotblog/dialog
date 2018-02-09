package com.dialogapp.dialog.api;

import android.support.annotation.Nullable;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.squareup.moshi.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/*
* Retrofit converter factory to unwrap MicroblogResponse into List<Item>
* */
public class MicroblogEnvelopeConverterFactory extends Converter.Factory {
    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (!type.equals(Types.newParameterizedType(List.class, Item.class)))
            return null;

        final Converter<ResponseBody, MicroBlogResponse> delegate =
                retrofit.nextResponseBodyConverter(this, MicroBlogResponse.class, annotations);
        return value -> {
            MicroBlogResponse envelope = delegate.convert(value);
            return envelope.items;
        };
    }
}
