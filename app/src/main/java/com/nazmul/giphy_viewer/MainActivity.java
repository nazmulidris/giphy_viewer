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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * The main UI of the application that contains the Toolbar, SearchView, and RecyclerView. It
 * creates a {@link AppViewModel} that is stable across orientation changes, and is only created or
 * destroyed when the user starts the app (launching it from the launcher, not just switching to an
 * already running instance), or exits it (by pressing back and not not just pressing home or
 * switching between apps).
 */
public final class MainActivity extends AppCompatActivity {

private ViewHolder   viewHolder;
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
  viewHolder.setupSearchView(searchView, searchMenuItem, appViewModel);
  return super.onCreateOptionsMenu(menu);
}

// Cold boot the Activity.

private void init() {
  setupViewModel();
  viewHolder = new ViewHolder(this);
  viewHolder.setupSwipeRefreshLayout();
  viewHolder.setupToolbar();
  viewHolder.setupRecyclerView();
  loadData();
}

private void setupViewModel() {
  appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
}

// Load fresh data into the activity.

private void loadData() {
  if (appViewModel.getUnderlyingData().isEmpty()) {
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

/**
 * Convenience class that holds all the views of this MainActivity in one place
 */
private final class ViewHolder {

  MainActivity                         activity;
  Toolbar                              toolbar;
  SwipeRefreshLayout                   swipeRefreshLayout;
  RecyclerView                         recyclerView;
  RecyclerViewManager                  recyclerViewManager;
  SwipeRefreshLayout.OnRefreshListener onRefreshGestureHandler;
  Runnable                             runOnRefreshComplete;
  SearchViewManager                    searchViewManager;

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

  void setupSearchView(
      SearchView searchView, MenuItem searchMenuItem, AppViewModel appViewModel) {
    searchViewManager = new SearchViewManager();
    searchViewManager.setupSearchView(searchView, searchMenuItem, appViewModel);
  }
}
}
