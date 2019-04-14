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

/**
 * Represents changes in underlying data (from the Giphy server). Changes can be either:
 *
 * <ol>
 * <li>Refresh - entirely new data set is available.
 * <li>Update - more data was added to existing set (the amount of new data is specified).
 * </ol>
 */
public class DataEvent {

public static final class Builder {

  private Type type    = Type.Refresh;
  private int  newSize = 0;

  public static Builder builder() {
    return new Builder();
  }

  public Builder type(Type mode) {
    this.type = mode;
    return this;
  }

  public Builder newSize(int newSize) {
    this.newSize = newSize;
    return this;
  }

  public DataEvent build() {
    return new DataEvent(type, newSize);
  }
}

private DataEvent(@NonNull Type mode, int query) {
  this.type = mode;
  this.newSize = query;
}

public enum Type {
  Refresh,
  GetMore,
  Error
}

private Type type;

public Type getType() {
  return type;
}

public boolean isErrorType() {
  return type == Type.Error;
}

public boolean isRefreshType() {
  return type == Type.Refresh;
}

public boolean isGetMoreType() {
  return type == Type.GetMore;
}

private int newSize;

public int getNewSize() {
  return newSize;
}

public String toString() {
  StringBuilder stringBuilder = new StringBuilder();
  switch (type) {
    case GetMore:
      stringBuilder.append(Type.Refresh.name()).append(", newSize:").append(newSize);
      break;
    case Refresh:
      stringBuilder.append(Type.Refresh.name());
      break;
    case Error:
      stringBuilder.append(Type.Error.name());
      break;
  }
  return stringBuilder.toString();
}
}
