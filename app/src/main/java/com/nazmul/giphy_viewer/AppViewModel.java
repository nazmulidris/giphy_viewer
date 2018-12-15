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

import com.giphy.sdk.core.models.Media;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

public class AppViewModel extends AndroidViewModel {

    public int position = 0;
    public ArrayList<Media> underlyingData = new ArrayList<>();

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    // Methods that UI can use to request API calls.

    public void requestRefreshData(@Nullable Runnable runOnRefreshComplete) {
        Log.d("logtag", "requestDataRefresh: make request");
        GiphyClient.makeTrendingRequest(
                runOnRefreshComplete,
                (List<Media> mediaList) -> {
                    Log.d("logtag", "requestDataRefresh: got response: " + mediaList.size());
                    resetData(mediaList);
                },
                null);
    }

    public void requestMoreData() {
        Log.d("logtag", "requestGetMoreData: make request " + underlyingData.size());
        GiphyClient.makeTrendingRequest(
                null,
                (List<Media> mediaList) -> {
                    Log.d("logtag", "requestDataRefresh: got response: " + mediaList.size());
                    updateData(mediaList);
                },
                underlyingData.size());
    }

    // Methods that modify the underlyingData & update the RecyclerView.

    private void updateData(List<Media> newData) {
        underlyingData.addAll(newData);
        Log.d("logtag", "updateData: data size: " + underlyingData.size());
        EventBus.getDefault().post(new UpdateDataEvent(newData, underlyingData));
    }

    // TODO This should be called by Giphy API that clears existing data and replaces it w/ new data
    private void resetData(List<Media> newData) {
        underlyingData.clear();
        underlyingData.addAll(newData);
        Log.d("logtag", "resetData: data size: " + underlyingData.size());
        EventBus.getDefault().post(new RefreshDataEvent(underlyingData));
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
}
