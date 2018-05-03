package com.dialogapp.dialog.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.util.CountingAppExecutors;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.InstantAppExecutors;
import com.dialogapp.dialog.util.Resource;
import com.dialogapp.dialog.util.Status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class NetworkBoundRequestResourceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private NetworkBoundRequestResource<ResponseBody> networkBoundRequestResource;
    private CountingAppExecutors countingAppExecutors;
    private final boolean useRealExecutors;
    private AppExecutors appExecutors;

    @Parameterized.Parameters
    public static List<Boolean> param() {
        return Arrays.asList(true, false);
    }

    public NetworkBoundRequestResourceTest(boolean useRealExecutors) {
        this.useRealExecutors = useRealExecutors;
        if (useRealExecutors) {
            countingAppExecutors = new CountingAppExecutors();
        }
    }

    @Before
    public void init() {
        appExecutors = useRealExecutors
                ? countingAppExecutors.getAppExecutors()
                : new InstantAppExecutors();
    }

    @Test
    public void basicFromNetwork() {
        networkBoundRequestResource = new NetworkBoundRequestResource<ResponseBody>(appExecutors) {
            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                return new Call<ResponseBody>() {
                    @Override
                    public Response<ResponseBody> execute() throws IOException {
                        return createResponse(3);
                    }

                    @Override
                    public void enqueue(Callback<ResponseBody> callback) {

                    }

                    @Override
                    public boolean isExecuted() {
                        return false;
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public boolean isCanceled() {
                        return false;
                    }

                    @Override
                    public Call<ResponseBody> clone() {
                        return null;
                    }

                    @Override
                    public Request request() {
                        return null;
                    }
                };
            }

            @Override
            protected boolean wasExpectedResponse(ApiResponse<ResponseBody> apiResponse) {
                try {
                    return apiResponse.body.string().equals("{}");
                } catch (IOException e) {
                    return false;
                }
            }
        };
        Observer<Event<Resource<Boolean>>> observer = Mockito.mock(Observer.class);
        networkBoundRequestResource.asEventLiveData().observeForever(observer);
        drain();
        Event<Resource<Boolean>> event = networkBoundRequestResource.asEventLiveData().getValue();
        assertThat(event.peekContent().data, is(true));
        assertThat(event.peekContent().status, is(Status.SUCCESS));
        verify(observer).onChanged(event);
    }

    @Test
    public void failureFromApi() {
        networkBoundRequestResource = new NetworkBoundRequestResource<ResponseBody>(appExecutors) {
            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                return new Call<ResponseBody>() {
                    @Override
                    public Response<ResponseBody> execute() throws IOException {
                        return createResponse(1);
                    }

                    @Override
                    public void enqueue(Callback<ResponseBody> callback) {

                    }

                    @Override
                    public boolean isExecuted() {
                        return false;
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public boolean isCanceled() {
                        return false;
                    }

                    @Override
                    public Call<ResponseBody> clone() {
                        return null;
                    }

                    @Override
                    public Request request() {
                        return null;
                    }
                };
            }

            @Override
            protected boolean wasExpectedResponse(ApiResponse<ResponseBody> apiResponse) {
                try {
                    return apiResponse.body.string().equals("{}");
                } catch (IOException e) {
                    return false;
                }
            }
        };
        Observer<Event<Resource<Boolean>>> observer = Mockito.mock(Observer.class);
        networkBoundRequestResource.asEventLiveData().observeForever(observer);
        drain();
        Event<Resource<Boolean>> event = networkBoundRequestResource.asEventLiveData().getValue();
        assertThat(event.peekContent().data, is(false));
        assertThat(event.peekContent().status, is(Status.ERROR));
        verify(observer).onChanged(event);
    }

    @Test
    public void unsuccessfulNetwork() {
        networkBoundRequestResource = new NetworkBoundRequestResource<ResponseBody>(appExecutors) {
            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                return new Call<ResponseBody>() {
                    @Override
                    public Response<ResponseBody> execute() throws IOException {
                        return createResponse(0);
                    }

                    @Override
                    public void enqueue(Callback<ResponseBody> callback) {

                    }

                    @Override
                    public boolean isExecuted() {
                        return false;
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public boolean isCanceled() {
                        return false;
                    }

                    @Override
                    public Call<ResponseBody> clone() {
                        return null;
                    }

                    @Override
                    public Request request() {
                        return null;
                    }
                };
            }

            @Override
            protected boolean wasExpectedResponse(ApiResponse<ResponseBody> apiResponse) {
                try {
                    return apiResponse.body.string().equals("{}");
                } catch (IOException e) {
                    return false;
                }
            }
        };
        Observer<Event<Resource<Boolean>>> observer = Mockito.mock(Observer.class);
        networkBoundRequestResource.asEventLiveData().observeForever(observer);
        drain();
        Event<Resource<Boolean>> event = networkBoundRequestResource.asEventLiveData().getValue();
        assertThat(event.peekContent().data, is(false));
        assertThat(event.peekContent().status, is(Status.ERROR));
        verify(observer).onChanged(event);
    }

    private void drain() {
        if (!useRealExecutors) {
            return;
        }
        try {
            countingAppExecutors.drainTasks(1, TimeUnit.SECONDS);
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    private Response<ResponseBody> createResponse(int responseType) {
        switch (responseType) {
            case 0:
                return Response.error(500, ResponseBody.create(MediaType.parse("txt"), "error"));
            case 1:
                return Response.success(ResponseBody.create(MediaType.parse("txt"), "{error}"));
            default:
                return Response.success(ResponseBody.create(MediaType.parse("txt"), "{}"));
        }
    }
}
