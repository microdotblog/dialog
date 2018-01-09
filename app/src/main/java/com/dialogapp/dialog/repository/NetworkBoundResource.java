/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.util.Resource;

import java.util.Objects;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors appExecutors;

    // acts as an intermediary between data source and consumer
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(Resource.loading(null));

        // observe db initially
        LiveData<ResultType> dbSource = loadFromDb();
        // check db data to see if network fetch is required
        result.addSource(dbSource, dbData -> {
            // remove db as a source temporarily
            result.removeSource(dbSource);
            if (shouldFetch(dbData))
                fetchFromNetwork(dbSource);
            else
                // add back the db as a source (state==SUCCESS)
                result.addSource(dbSource, newData -> setValue(Resource.success(dbData)));
        });
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // add back the db as a source to dispatch its value while the network call takes place (state==LOADING)
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        // observe network source as a mediator
        result.addSource(apiResponse, response -> {
            // remove existing sources after response becomes available
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if (response.isSuccessful()) {
                // write to db on a separate thread and send back the data
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    appExecutors.mainThread().execute(() -> {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));
                    });
                });
            } else {
                // Handle network fetch failure in mediator
                onFetchFailed();
                // send back whatever data is in db (state==FAILURE)
                result.addSource(dbSource, newData -> setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue))
            result.setValue(newValue);
    }

    protected void onFetchFailed() {
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract boolean shouldFetch(@Nullable ResultType dbData);

    @NonNull
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @NonNull
    protected abstract LiveData<ResultType> loadFromDb();
}
