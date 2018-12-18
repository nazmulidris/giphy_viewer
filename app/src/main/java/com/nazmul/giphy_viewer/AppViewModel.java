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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

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
public class AppViewModel extends AndroidViewModel {

    public static final String TAG = "logtag";
    public int position = 0;
    public final ArrayList<Media> underlyingData = new ArrayList<>();
    public final GiphyClient giphyClient;

    /** ViewModel.ON_CREATE */
    public AppViewModel(@NonNull Application application) {
        super(application);
        Fresco.initialize(application);
        giphyClient = new GiphyClient();
        Log.d(TAG, "AppViewModel: create giphyClient and init Fresco");
    }

    /** ViewModel.ON_DESTROY */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "AppViewModel: shutdown giphyClient and Fresco");
        Fresco.shutDown();
    }

    // Methods that UI can use to request API calls.

    public void requestRefreshData(@Nullable Runnable runOnRefreshComplete) {
        if (!isSearchMode()) {
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
                    query,
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
        if (!isSearchMode()) {
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
                    query,
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

    // Enable or disable "search" mode.
    // With Search mode enabled, the "search" API endpoint is used.
    // With it disabled, the "trending" API endpoint is used.

    public String query = null;

    public boolean isSearchMode() {
        return query != null;
    }

    public void setSearchMode(String query) {
        this.query = query;
    }

    public void clearSearchMode() {
        query = null;
    }

    // Methods that modify the underlyingData & update the RecyclerView.

    private void updateData(List<Media> newData) {
        underlyingData.addAll(newData);
        Log.d(TAG, "updateData: data size: " + underlyingData.size());
        EventBus.getDefault().post(new UpdateDataEvent(newData, underlyingData));
    }

    private void resetData(List<Media> newData) {
        underlyingData.clear();
        underlyingData.addAll(newData);
        Log.d(TAG, "resetData: data size: " + underlyingData.size());
        EventBus.getDefault().post(new RefreshDataEvent(underlyingData));
    }

    private void errorData() {
        EventBus.getDefault().post(new ErrorDataEvent());
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
