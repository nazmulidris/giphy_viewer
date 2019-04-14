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
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.Objects;

import static com.nazmul.giphy_viewer.AppViewModel.TAG;

/**
 * Creates and manages the SearchView (which is in the Toolbar) that is used by the {@link
 * MainActivity}.
 */
final class SearchViewManager {

void setupSearchView(
    SearchView searchView, MenuItem searchMenuItem, AppViewModel appViewModel) {
  searchView.setOnCloseListener(
      new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
          Log.d(TAG, "onClose: clear search mode, and request refresh");
          appViewModel.setTrendingMode();
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

  // When activity has been thru an orientation change, make sure to restore the SearchView
  // state (if it was in search mode before the orientation change).
  AppMode appMode = Objects.requireNonNull(appViewModel.getAppModeLiveData().getValue());
  if (appMode.isSearchingMode()) {
    searchMenuItem.expandActionView();
    searchView.setIconified(false);
    searchView.setQuery(appMode.getSearchQuery(), false);
  }
}
}
