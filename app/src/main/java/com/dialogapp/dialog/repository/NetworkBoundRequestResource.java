package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.Objects;
import com.dialogapp.dialog.util.Resource;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public abstract class NetworkBoundRequestResource<ResponseType> {
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Event<Resource<Boolean>>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundRequestResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        runTask();
    }

    public LiveData<Event<Resource<Boolean>>> asEventLiveData() {
        return result;
    }

    private void runTask() {
        appExecutors.networkIO().execute(() -> {
            Response<ResponseType> response;
            try {
                response = createCall().execute();
                ApiResponse<ResponseType> apiResponse = new ApiResponse<>(response);
                if (apiResponse.isSuccessful()) {
                    boolean requestWasSuccessful = wasExpectedResponse(apiResponse);

                    if (requestWasSuccessful) {
                        appExecutors.diskIO().execute(this::performDbOperation);
                        postValue(Event.createEvent(Resource.success(true)));
                    } else {
                        postValue(Event.createEvent(Resource.error("Unexpected response from server", false)));
                    }
                } else {
                    Timber.e("Call unsuccessful : %s", apiResponse.errorMessage);
                    postValue(Event.createEvent(Resource.error(apiResponse.errorMessage, false)));
                }
            } catch (IOException e) {
                Timber.e("Unexpected IOException : %s", e.getMessage());
                postValue(Event.createEvent(Resource.error(e.getMessage(), false)));
            }
        });
    }

    @WorkerThread
    private void postValue(Event<Resource<Boolean>> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.postValue(newValue);
        }
    }

    @NonNull
    @WorkerThread
    protected abstract Call<ResponseType> createCall();

    @WorkerThread
    protected abstract boolean wasExpectedResponse(ApiResponse<ResponseType> apiResponse);

    @WorkerThread
    protected void performDbOperation() {
    }
}
