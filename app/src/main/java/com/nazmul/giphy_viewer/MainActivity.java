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

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.nazmul.giphy_viewer.AppViewModel.*;

public final class MainActivity extends AppCompatActivity {

    private ViewHolder viewHolder;
    private AppViewModel appViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        setupSearchView(searchView, searchMenuItem);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView(SearchView searchView, MenuItem searchMenuItem) {
        searchView.setOnCloseListener(
                new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        Log.d(TAG, "onClose: clear search mode, and request refresh");
                        appViewModel.clearSearchMode();
                        appViewModel.requestRefreshData(null);
                        return false;
                    }
                });

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Log.d(TAG, "onQueryTextSubmit: " + query);
                        if (!query.isEmpty()) {
                            searchMenuItem.collapseActionView();
                            appViewModel.setSearchMode(query);
                            appViewModel.requestRefreshData(null);
                        }

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
    }

    private void init() {
        setupViewModel();
        viewHolder = new ViewHolder(this);
        viewHolder.setupSwipeRefreshLayout();
        viewHolder.setupToolbar();
        viewHolder.setupRecyclerView();
        loadData();
    }

    private void loadData() {
        if (appViewModel.underlyingData.isEmpty()) {
            // Activity has no data, so perform a refresh now.
            viewHolder.swipeRefreshLayout.setRefreshing(true);
            viewHolder.onRefreshGestureHandler.onRefresh();
        } else {
            // Activity underwent an orientation change. The AppViewModel has data, but pagination
            // isn't attached to the RecyclerView, since this only happens after the first refresh
            // operation occurs (in the block above) when the Activity is created for the very very
            // first time (not an orientation change driven destroy -> instantiate).
            viewHolder.recyclerViewManager.setupInfiniteScrolling();
        }
    }

    private void setupViewModel() {
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    }

    final class ViewHolder {

        MainActivity activity;
        Toolbar toolbar;
        SwipeRefreshLayout swipeRefreshLayout;
        RecyclerView recyclerView;
        RecyclerViewManager recyclerViewManager;
        SwipeRefreshLayout.OnRefreshListener onRefreshGestureHandler;
        Runnable runOnRefreshComplete;

        ViewHolder(MainActivity mainActivity) {
            this.activity = mainActivity;
        }

        void setupToolbar() {
            toolbar = findViewById(R.id.app_toolbar);
            setSupportActionBar(viewHolder.toolbar);
        }

        void setupSwipeRefreshLayout() {
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_container);
            runOnRefreshComplete = () -> swipeRefreshLayout.setRefreshing(false);
            onRefreshGestureHandler = () -> appViewModel.requestRefreshData(runOnRefreshComplete);
            swipeRefreshLayout.setOnRefreshListener(onRefreshGestureHandler);
        }

        void setupRecyclerView() {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerViewManager = new RecyclerViewManager(activity, recyclerView);
        }
    }
}
