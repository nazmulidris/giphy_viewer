/*
 * Copyright 2018 Nazmul Idris. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nazmul.giphy_viewer;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.giphy.sdk.core.models.Media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Creates and manages data that survives past multiple {@link MainActivity} creation and
 * destruction events (due to screen orientation changes).
 *
 * <ol>
 *   <li>
 *       <p>This class should not contain any references to Views.
 *   <li>When the {@link MainActivity} is destroyed by the user leaving the app (by pressing back,
 *       not home), this ViewModel is cleaned up and destroyed.
 * </ol>
 */
public final class AppViewModel extends AndroidViewModel {

    public static final String TAG = "logtag";

    private final GiphyClient giphyClient = new GiphyClient();

    private final MutableLiveData<AppMode> appModeLiveData = new MutableLiveData<>();

    /** ViewModel.ON_CREATE */
    public AppViewModel(@NonNull Application application) {
        super(application);
        Fresco.initialize(application);
        appModeLiveData.setValue(AppMode.Builder.builder().mode(AppMode.Mode.Trending).build());
        Log.d(TAG, "AppViewModel: create giphyClient, init Fresco, set appMode");
    }

    /** ViewModel.ON_DESTROY */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "AppViewModel: shutdown Fresco");
        Fresco.shutDown();
    }

    // Manage AppMode.

    public LiveData<AppMode> getAppModeLiveData() {
        return appModeLiveData;
    }

    public void setTrendingMode() {
        appModeLiveData.setValue(AppMode.Builder.builder().mode(AppMode.Mode.Trending).build());
    }

    public void setSearchMode(String query) {
        appModeLiveData.setValue(
                AppMode.Builder.builder().mode(AppMode.Mode.Search).query(query).build());
    }

    // Current scrolled position of the RecyclerView.

    private int position = 0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    // Underlying data storage.

    private final ArrayList<Media> underlyingData = new ArrayList<>();

    public List<Media> getUnderlyingData() {
        return Collections.unmodifiableList(underlyingData);
    }

    // Broadcast underlying data storage changes.

    private final MutableLiveData<DataEvent> dataEventLiveData = new MutableLiveData<>();

    public LiveData<DataEvent> getDataEventLiveData() {
        return dataEventLiveData;
    }

    // Methods that UI can use to request API calls.

    public void requestRefreshData(@Nullable Runnable runOnRefreshComplete) {
        AppMode appMode = Objects.requireNonNull(appModeLiveData.getValue());
        if (appMode.isTrendingMode()) {
            Log.d(TAG, "requestRefreshData: make trending request");
            giphyClient.makeTrendingRequest(
                    runOnRefreshComplete,
                    new GiphyClient.GiphyResultsHandler() {
                        @Override
                        public void onResponse(List<Media> mediaList) {
                            Log.d(TAG, "requestRefreshData: got response: " + mediaList.size());
                            resetData(mediaList);
                        }

                        @Override
                        public void onError() {
                            errorData();
                        }
                    },
                    null);
        } else {
            Log.d(TAG, "requestRefreshData: make search request");
            giphyClient.makeSearchRequest(
                    appMode.getSearchQuery(),
                    runOnRefreshComplete,
                    new GiphyClient.GiphyResultsHandler() {
                        @Override
                        public void onResponse(List<Media> mediaList) {
                            Log.d(TAG, "requestRefreshData: got response: " + mediaList.size());
                            resetData(mediaList);
                        }

                        @Override
                        public void onError() {}
                    },
                    null);
        }
    }

    public void requestMoreData() {
        AppMode appMode = Objects.requireNonNull(appModeLiveData.getValue());
        if (appMode.isTrendingMode()) {
            Log.d(TAG, "requestMoreData: make trending request: offset= " + underlyingData.size());
            giphyClient.makeTrendingRequest(
                    null,
                    new GiphyClient.GiphyResultsHandler() {
                        @Override
                        public void onResponse(List<Media> mediaList) {
                            Log.d(TAG, "requestMoreData: got response: " + mediaList.size());
                            updateData(mediaList);
                        }

                        @Override
                        public void onError() {
                            errorData();
                        }
                    },
                    underlyingData.size());
        } else {
            Log.d(TAG, "requestMoreData: make search request: offset= " + underlyingData.size());
            giphyClient.makeSearchRequest(
                    appMode.getSearchQuery(),
                    null,
                    new GiphyClient.GiphyResultsHandler() {
                        @Override
                        public void onResponse(List<Media> mediaList) {
                            Log.d(TAG, "requestMoreData: got response: " + mediaList.size());
                            updateData(mediaList);
                        }

                        @Override
                        public void onError() {}
                    },
                    underlyingData.size());
        }
    }

    // Methods that modify the underlyingData & update the RecyclerView.

    private void updateData(List<Media> newData) {
        underlyingData.addAll(newData);
        Log.d(TAG, "updateData: data size: " + underlyingData.size());
        dataEventLiveData.setValue(
                DataEvent.Builder.builder()
                        .type(DataEvent.Type.GetMore)
                        .newSize(newData.size())
                        .build());
    }

    private void resetData(List<Media> newData) {
        underlyingData.clear();
        underlyingData.addAll(newData);
        Log.d(TAG, "resetData: data size: " + underlyingData.size());
        dataEventLiveData.setValue(
                DataEvent.Builder.builder().type(DataEvent.Type.Refresh).build());
    }

    private void errorData() {
        dataEventLiveData.setValue(DataEvent.Builder.builder().type(DataEvent.Type.Error).build());
    }

    // Events.

    public static class UpdateDataEvent {

        public final List<Media> newData;
        public final List<Media> underlyingData;

        public UpdateDataEvent(List<Media> newData, List<Media> underlyingData) {
            this.newData = newData;
            this.underlyingData = underlyingData;
        }
    }

    public static class RefreshDataEvent {

        public final List<Media> underlyingData;

        public RefreshDataEvent(List<Media> underlyingData) {
            this.underlyingData = underlyingData;
        }
    }

    public static class ErrorDataEvent {}
}
