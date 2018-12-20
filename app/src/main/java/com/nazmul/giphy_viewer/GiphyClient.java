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

import static com.nazmul.giphy_viewer.AppViewModel.TAG;

/**
 * Wraps the <a href="https://github.com/Giphy/giphy-android-sdk-core">Giphy Android SDK</a> for use
 * by the {@link AppViewModel}.
 *
 * <ol>
 *   <li>This class spawns its own executors under the covers and its own network classes (via <a
 *       href="http://tinyurl.com/ybrz4wod">DefaultNetworkSession</a> ). When the {@link
 *       AppViewModel} is destroyed, the network resources are garbage collected as well.
 *   <li>The <a href="http://tinyurl.com/ydac4992">Media</a> class is the Giphy API model that is
 *       used the most.
 *   <li>The source code for the <a href="http://tinyurl.com/ycvfz5mk">GPHApiClient can be found
 *       here.</a>
 * </ol>
 */
final class GiphyClient {
    public static final String API_KEY = "mnVttajnx9Twmgp3vFbMQa3Gvn9Rv4Hg";
    public static final int MAX_ITEMS_PER_REQUEST = 25;

    // Constructor.

    public final GPHApi client;

    public GiphyClient() {
        client = new GPHApiClient(API_KEY);
    }

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
    public void makeTrendingRequest(
            @Nullable Runnable runOnComplete,
            @NonNull GiphyResultsHandler onResponseHandler,
            @Nullable Integer offset) {

        CompletionHandler<ListMediaResponse> completionHandler =
                (results, exception) -> {
                    // This code runs in the main thread.
                    if (results == null) {
                        onResponseHandler.onError();
                    } else if (results.getData() != null) {
                        onResponseHandler.onResponse(results.getData());
                    }
                    if (runOnComplete != null) runOnComplete.run();
                };

        Log.d(TAG, "makeTrendingRequest: offset: " + offset + ", limit: " + MAX_ITEMS_PER_REQUEST);

        client.trending(
                /* type= */ MediaType.gif,
                /* limit= */ MAX_ITEMS_PER_REQUEST,
                /* offset= */ offset,
                /* rating */ RatingType.g,
                /* completionHandler */ completionHandler);
    }

    public void makeSearchRequest(
            @Nullable String query,
            @Nullable Runnable runOnComplete,
            @NonNull GiphyResultsHandler onResponseHandler,
            @Nullable Integer offset) {

        CompletionHandler<ListMediaResponse> completionHandler =
                (results, exception) -> {
                    // This code runs in the main thread.
                    if (results == null) {
                        onResponseHandler.onError();
                    } else if (results.getData() != null) {
                        onResponseHandler.onResponse(results.getData());
                    }
                    if (runOnComplete != null) runOnComplete.run();
                };

        Log.d(TAG, "makeSearchRequest: offset: " + offset + ", limit: " + MAX_ITEMS_PER_REQUEST);

        client.search(
                /* query= */ query == null ? "" : query,
                /* type= */ MediaType.gif,
                /* limit= */ MAX_ITEMS_PER_REQUEST,
                /* offset= */ offset,
                /* rating= */ RatingType.g,
                /* lang= */ null,
                /* completionHandler= */ completionHandler);
    }

    public interface GiphyResultsHandler {
        void onResponse(List<Media> mediaList);

        void onError();
    }
}
