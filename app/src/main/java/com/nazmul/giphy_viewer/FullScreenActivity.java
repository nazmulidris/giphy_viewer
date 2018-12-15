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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.giphy.sdk.core.models.Media;

public class FullScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_activity);
        Uri imageUri = getIntent().getData();
        ImageView gifView = findViewById(R.id.fullscreen_gif);
        GlideApp.with(this).load(imageUri).into(gifView).clearOnDetach();
    }

    public static Intent getIntent(Context context, Media item) {
        Intent intent = new Intent(context, FullScreenActivity.class);
        intent.setData(Uri.parse(item.getImages().getOriginal().getGifUrl()));
        return intent;
    }
}
