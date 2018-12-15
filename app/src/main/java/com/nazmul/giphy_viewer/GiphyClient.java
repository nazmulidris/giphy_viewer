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

import android.os.Looper;
import android.util.Log;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GiphyClient {
    public static final String API_KEY = "mnVttajnx9Twmgp3vFbMQa3Gvn9Rv4Hg";
    public static final GPHApi client = new GPHApiClient(API_KEY);
    public static final int MAX_ITEMS_PER_REQUEST = 25;

    /**
     * @param runOnComplete This Runnable will be executed after the API response is received. If
     *     the response is an error or contains results, this will be run. This is a good place to
     *     put UI code that changes the state of any components that are "waiting". This code is run
     *     on the main thread.
     * @param onResponseHandler The <code>onResponse</code> method is called only if results are
     *     returned in the API response.
     * @param offset This integer contains the next set of images that you want to load from Giphy.
     *     This is usually going to the size of the number of entries that are already downloaded
     *     (ie the size of the underlying data).
     */
    public static void makeTrendingRequest(
            @Nullable Runnable runOnComplete,
            @NonNull GiphyResultsHandler onResponseHandler,
            @Nullable Integer offset) {

        CompletionHandler<ListMediaResponse> completionHandler =
                (results, exception) -> {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        Log.d("logtag", "makeTrendingRequest running in main thread");
                    } else {
                        Log.d("logtag", "🛑 makeTrendingRequest NOT running in main thread");
                    }

                    if (results != null && results.getData() != null) {
                        for (Media gif : results.getData()) {
                            Log.d("logtag", "gif.getId(): " + gif.getId());
                        }
                        onResponseHandler.onResponse(results.getData());
                    }
                    if (runOnComplete != null) runOnComplete.run();
                };

        client.trending(
                /* type= */ MediaType.gif,
                /* limit= */ offset,
                /* offset= */ null,
                /* rating */ RatingType.g,
                /* completionHandler */ completionHandler);
    }

    public interface GiphyResultsHandler {
        void onResponse(List<Media> mediaList);
    }
}
