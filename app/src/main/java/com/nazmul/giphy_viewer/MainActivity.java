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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public final class MainActivity extends AppCompatActivity {

    private ViewHolder viewHolder;
    private AppViewModel appViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
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
        viewHolder.swipeRefreshLayout.setRefreshing(true);
        viewHolder.onRefreshGestureHandler.onRefresh();
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
            onRefreshGestureHandler = () -> appViewModel.requestDataRefresh(runOnRefreshComplete);
            swipeRefreshLayout.setOnRefreshListener(onRefreshGestureHandler);
        }

        void setupRecyclerView() {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerViewManager = new RecyclerViewManager(activity, recyclerView);
        }
    }
}
