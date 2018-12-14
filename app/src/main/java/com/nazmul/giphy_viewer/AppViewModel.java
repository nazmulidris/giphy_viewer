package com.nazmul.giphy_viewer;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

public class AppViewModel extends AndroidViewModel {
    public static final int MAX_ITEMS_PER_REQUEST = 25;
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
        for (int i = 0; i < MAX_ITEMS_PER_REQUEST; i++) {
            dummyData.add("random-" + seed + " " + (i + offset));
        }
        return dummyData;
    }

    // TODO Add Giphy API here.

    // Methods that UI can use to request API calls.

    public void requestDataRefresh(@Nullable Runnable runOnRefreshComplete) {
        Log.d("logtag", "requestDataRefresh: ");
        // TODO Replace w/ Giphy API call.
        resetData(mockDummyData(0));
        // TODO Replace following line w/ actual callback to runOnRefreshComplete, not 1s delay.
        if (runOnRefreshComplete != null) {
            new Handler(getApplication().getMainLooper()).postDelayed(runOnRefreshComplete, 1000);
        }
    }

    // TODO This needs to be wired to a overscroll detection event and be called.
    public void requestGetMoreData() {
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
