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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Specifies whether the app is in "search" or "trending" mode.
 *
 * <ol>
 *   <li>With Search mode enabled, the "search" API endpoint is used.
 *   <li>With it disabled, the "trending" API endpoint is used.
 * </ol>
 */
public class AppMode {

    public static final class Builder {

        private Mode mode = Mode.Trending;
        private String query = null;

        public static Builder builder() {
            return new Builder();
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public AppMode build() {
            return new AppMode(mode, query);
        }
    }

    private AppMode(@NonNull Mode mode, @Nullable String query) {
        this.mode = mode;
        this.query = query;
    }

    public enum Mode {
        Search,
        Trending
    }

    private Mode mode;

    public boolean isTrendingMode() {
        return mode == Mode.Trending;
    }

    public boolean isSearchingMode() {
        return mode == mode.Search;
    }

    private String query;

    public String getSearchQuery() {
        return query;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (mode) {
            case Search:
                stringBuilder.append(Mode.Search.name()).append(", query:").append(query);
                break;
            case Trending:
                stringBuilder.append(Mode.Trending.name());
                break;
        }
        return stringBuilder.toString();
    }
}
