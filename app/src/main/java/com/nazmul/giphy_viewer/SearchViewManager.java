package com.nazmul.giphy_viewer;

import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;

import static com.nazmul.giphy_viewer.AppViewModel.TAG;

public class SearchViewManager {
    // SearchView in Toolbar.

    public void setupSearchView(SearchView searchView, MenuItem searchMenuItem,
                                AppViewModel appViewModel) {
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

        // When activity has been thru an orientation change, make sure to restore the SearchView
        // state (if it was in search mode before the orientation change).
        if (appViewModel.isSearchMode()) {
            searchMenuItem.expandActionView();
            searchView.setIconified(false);
            searchView.setQuery(appViewModel.query, false);
        }
    }


}
