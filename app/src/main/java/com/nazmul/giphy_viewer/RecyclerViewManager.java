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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    RecyclerViewManager(MainActivity activity, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.appViewModel = ViewModelProviders.of(activity).get(AppViewModel.class);
        setupEventListeners();
        setupLifecycleObservers(activity);
        setupLayoutManager();
        setupDataAdapter();
    }

    private void setupEventListeners() {
        Log.d("logtag", "setupEventListeners: ");
        EventBus.getDefault().register(RecyclerViewManager.this);
    }

    // Infinite scrolling support.

    private boolean isLoading;
    public static final int TRIGGER_LOADING_THRESHOLD = 2;
    private Paginate paginate;

    /**
     * This only needs to be done once for the life of this class. Infinite scrolling only comes
     * into play after the first set of data has been loaded.
     */
    private void setupInfiniteScrolling() {
        if (paginate == null) {
            Log.d("logtag", "setupInfiniteScrolling: setting it up ONCE");
            Paginate.Callbacks callbacks =
                    new Paginate.Callbacks() {
                        @Override
                        public void onLoadMore() {
                            Log.d("logtag", "onLoadMore: ");
                            isLoading = true;
                            appViewModel.requestGetMoreData();
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
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessageEvent(AppViewModel.UpdateDataEvent event) {
        Log.d("logtag", "onMessageEvent: UpdateDataEvent");
        // TODO Replace this w/ a real implementation.
        isLoading = false;
        dataAdapter.notifyItemRangeInserted(
                event.underlyingData.size() - event.newData.size(), event.newData.size());
    }

    /** More info on [threadMode](http://tinyurl.com/yabwdd2a). */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessageEvent(AppViewModel.RefreshDataEvent event) {
        Log.d("logtag", "onMessageEvent: RefreshDataEvent");
        // TODO Replace this w/ a real implementation.
        setupInfiniteScrolling();
        dataAdapter.notifyDataSetChanged();
    }

    // Layout Manager.

    private static final int ORIENTATION = StaggeredGridLayoutManager.VERTICAL;
    private static final int GRID_SPAN_COUNT = 2;
    private StaggeredGridLayoutManager layoutManager;

    private void setupLayoutManager() {
        layoutManager = new StaggeredGridLayoutManager(GRID_SPAN_COUNT, ORIENTATION);
        layoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
    }

    // Saving/restoring list position.

    private void setupLifecycleObservers(MainActivity activity) {
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
                        (String item) -> {
                            // TODO Replace toast w/ actual handling of user clicking on an item.
                            Toast.makeText(
                                            appViewModel.getApplication().getApplicationContext(),
                                            item,
                                            Toast.LENGTH_SHORT)
                                    .show();
                        });
        recyclerView.setAdapter(dataAdapter);
    }

    private class DataAdapter extends RecyclerView.Adapter<RowViewHolder> {

        // TODO Replace String w/ actual data type from API.
        private final ItemClickListener<String> onItemClick;

        // TODO Replace String w/ actual data type from API.
        DataAdapter(ItemClickListener<String> onItemClick) {
            this.onItemClick = onItemClick;
        }

        @NonNull
        @Override
        public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cell =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.staggered_grid_cell, parent, false);
            return new RowViewHolder(cell);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
            holder.bindDataToView(appViewModel.underlyingData.get(position), onItemClick);
        }

        @Override
        public int getItemCount() {
            return appViewModel.underlyingData.size();
        }
    }

    private class RowViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public RowViewHolder(@NonNull View cellView) {
            super(cellView);
            textView = cellView.findViewById(R.id.text_staggered_grid_cell);
        }

        public void bindDataToView(Object data, ItemClickListener<String> onItemClick) {
            textView.setText(data.toString());
            textView.setOnClickListener(v -> onItemClick.onClick(data.toString()));
        }
    }

    interface ItemClickListener<T> {
        void onClick(T item);
    }
}
