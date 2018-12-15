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
import android.os.Handler;
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
    // TODO Add generic type to accept only response data from API models (not String).
    public ArrayList<String> underlyingData = new ArrayList<>();

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    // TODO Remove this when I have real data.
    public ArrayList<String> mockDummyData(int offset) {
        ArrayList<String> dummyData = new ArrayList<>();
        double seed = Math.random();
        for (int i = 0; i < GiphyClient.MAX_ITEMS_PER_REQUEST; i++) {
            dummyData.add("random-" + seed + " " + (i + offset));
        }
        return dummyData;
    }

    // TODO Add Giphy API here.

    // Methods that UI can use to request API calls.

    public void requestRefreshData(@Nullable Runnable runOnRefreshComplete) {
        Log.d("logtag", "requestDataRefresh: ");
        // TODO Replace w/ Giphy API call.
        resetData(mockDummyData(0));
        // TODO Replace following line w/ actual callback to runOnRefreshComplete, not 1s delay.
        if (runOnRefreshComplete != null) {
            new Handler(getApplication().getMainLooper()).postDelayed(runOnRefreshComplete, 1000);
        }

        GiphyClient.makeTrendingRequest(
                null,
                (List<Media> mediaList) -> {
                    Log.d("logtag", "requestDataRefresh mediaList.size: " + mediaList.size());
                },
                null);
    }

    // TODO This needs to be wired to a overscroll detection event and be called.
    public void requestMoreData() {
        // TODO implement this w/ real API call.
        Log.d("logtag", "requestGetMoreData: " + underlyingData.size());
        ArrayList<String> newData = mockDummyData(underlyingData.size());
        updateData(newData);
    }

    // Methods that modify the underlyingData & update the RecyclerView.

    // TODO This should be called by Giphy API that adds responses to the existing data set
    private void updateData(ArrayList<String> newData) {
        for (String datum : newData) underlyingData.add(datum);
        EventBus.getDefault().post(new UpdateDataEvent(newData, underlyingData));
    }

    // TODO This should be called by Giphy API that clears existing data and replaces it w/ new data
    private void resetData(ArrayList<String> newData) {
        underlyingData.clear();
        for (String datum : newData) underlyingData.add(datum);
        Log.d("logtag", "resetData: " + underlyingData.size());
        EventBus.getDefault().post(new RefreshDataEvent(underlyingData));
    }

    // Events.

    public static class UpdateDataEvent {

        public final ArrayList newData;
        public final ArrayList underlyingData;

        public UpdateDataEvent(ArrayList newData, ArrayList underlyingData) {
            this.newData = newData;
            this.underlyingData = underlyingData;
        }
    }

    public static class RefreshDataEvent {

        public final ArrayList underlyingData;

        public RefreshDataEvent(ArrayList underlyingData) {
            this.underlyingData = underlyingData;
        }
    }
}
