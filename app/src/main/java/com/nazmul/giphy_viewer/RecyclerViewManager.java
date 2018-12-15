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

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.giphy.sdk.core.models.Media;
import com.paginate.Paginate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

final class RecyclerViewManager {

    private final RecyclerView recyclerView;
    private final AppViewModel appViewModel;
    private final MainActivity activity;

    RecyclerViewManager(MainActivity activity, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.appViewModel = ViewModelProviders.of(activity).get(AppViewModel.class);
        this.activity = activity;
        setupEventListeners();
        setupLifecycleObservers();
        setupLayoutManager();
        setupDataAdapter();
    }

    private void setupEventListeners() {
        Log.d("logtag", "setupEventListeners: ");
        EventBus.getDefault().register(RecyclerViewManager.this);
    }

    // Infinite scrolling support.

    public static final int TRIGGER_LOADING_THRESHOLD = 2;
    private boolean isLoading;
    private Paginate paginate;

    /**
     * This only needs to be done once for the life of this class. Infinite scrolling only comes
     * into play after the first set of data has been loaded.
     */
    public void setupInfiniteScrolling() {
        if (paginate == null) {
            Log.d("logtag", "setupInfiniteScrolling: setting it up ONCE");
            Paginate.Callbacks callbacks =
                    new Paginate.Callbacks() {
                        @Override
                        public void onLoadMore() {
                            Log.d("logtag", "onLoadMore: ");
                            isLoading = true;
                            appViewModel.requestMoreData();
                        }

                        @Override
                        public boolean isLoading() {
                            Log.d("logtag", "isLoading: " + isLoading);
                            return isLoading;
                        }

                        /** Return false to always allow infinite scrolling */
                        @Override
                        public boolean hasLoadedAllItems() {
                            return false;
                        }
                    };
            paginate =
                    Paginate.with(recyclerView, callbacks)
                            .setLoadingTriggerThreshold(TRIGGER_LOADING_THRESHOLD)
                            .setLoadingListItemSpanSizeLookup(() -> GRID_SPAN_COUNT)
                            .build();
        } else {
            Log.d("logtag", "setupInfiniteScrolling: already setup up");
        }
    }

    // EventBus.

    /** More info on [threadMode](http://tinyurl.com/yabwdd2a). */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppViewModel.UpdateDataEvent event) {
        Log.d("logtag", "onMessageEvent: UpdateDataEvent");
        // TODO Replace this w/ a real implementation.
        isLoading = false;
        dataAdapter.notifyItemRangeInserted(
                event.underlyingData.size() - event.newData.size(), event.newData.size());
    }

    /** More info on [threadMode](http://tinyurl.com/yabwdd2a). */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppViewModel.RefreshDataEvent event) {
        Log.d("logtag", "onMessageEvent: RefreshDataEvent");
        // TODO Replace this w/ a real implementation.
        setupInfiniteScrolling();
        dataAdapter.notifyDataSetChanged();
    }

    /** More info on [threadMode](http://tinyurl.com/yabwdd2a). */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppViewModel.ErrorDataEvent event) {
        Log.d("logtag", "onMessageEvent: ErrorDataEvent");
        // TODO Replace this w/ a real implementation.
        isLoading = false;
        Toast.makeText(activity, "Network error occurred", Toast.LENGTH_LONG).show();
    }

    // Layout Manager.

    private static final int GRID_SPAN_COUNT = 2;
    private StaggeredGridLayoutManager layoutManager;

    private void setupLayoutManager() {
        layoutManager =
                new StaggeredGridLayoutManager(
                        GRID_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
    }

    // Saving/restoring list position.

    private void setupLifecycleObservers() {
        activity.getLifecycle()
                .addObserver(
                        new LifecycleObserver() {
                            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                            void saveListPosition() {
                                appViewModel.position =
                                        layoutManager.findFirstVisibleItemPositions(null)[0];
                            }

                            @OnLifecycleEvent(Lifecycle.Event.ON_START)
                            void restoreListPosition() {
                                layoutManager.scrollToPosition(appViewModel.position);
                            }

                            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                            void unregisterEventBus() {
                                EventBus.getDefault().unregister(RecyclerViewManager.this);
                            }
                        });
    }

    // Data Adapter.

    private DataAdapter dataAdapter;

    private void setupDataAdapter() {
        dataAdapter =
                new DataAdapter(
                        (Media item) -> {
                            activity.startActivity(FullScreenActivity.getIntent(activity, item));
                        });
        recyclerView.setAdapter(dataAdapter);
    }

    private class DataAdapter extends RecyclerView.Adapter<RowViewHolder> {

        private final ItemClickListener<Media> onItemClickHandler;

        DataAdapter(ItemClickListener<Media> onItemClick) {
            this.onItemClickHandler = onItemClick;
        }

        @NonNull
        @Override
        public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.grid_cell, parent, false);
            return new RowViewHolder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
            holder.bindDataToView(appViewModel.underlyingData.get(position), onItemClickHandler);
        }

        @Override
        public int getItemCount() {
            return appViewModel.underlyingData.size();
        }
    }

    private class RowViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView imageView;

        public RowViewHolder(@NonNull View imageView) {
            super(imageView);
            this.imageView = imageView.findViewById(R.id.image_grid_cell);
        }

        public void bindDataToView(Media data, ItemClickListener<Media> onItemClick) {
            imageView.setOnClickListener(v -> onItemClick.onClick(data));
            final Uri imageUri = Uri.parse(data.getImages().getFixedWidthDownsampled().getGifUrl());
            imageView.setAspectRatio(
                    (float) data.getImages().getFixedWidthDownsampled().getWidth()
                            / (float) data.getImages().getFixedWidthDownsampled().getHeight());
            imageView.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setUri(imageUri)
                            .setAutoPlayAnimations(true)
                            .build());
        }
    }

    interface ItemClickListener<T> {
        void onClick(T item);
    }
}
