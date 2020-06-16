// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.perseverance.phando.customtab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * A BroadcastReceiver that handles the Action Intent from the Custom Tab and shows the Url
 * in a Toast.
 */
public class ActionBroadcastReceiver extends BroadcastReceiver {
    public static final String KEY_ACTION_SOURCE = "org.chromium.customtabsdemos.ACTION_SOURCE";
    public static final int ACTION_ACTION_BUTTON_SHARE = 1;
    public static final int ACTION_ACTION_BUTTON_NOTIFICATION = 2;
    public static final int ACTION_MENU_ITEM = 2;
    public static final int ACTION_TOOLBAR = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            int actionId = intent.getIntExtra(KEY_ACTION_SOURCE, -1);
            switch (actionId) {
                case ACTION_ACTION_BUTTON_SHARE:
                   /* Intent i = new Intent(context, LoaderActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("url", url);
                    context.startActivity(i);*/
                    break;
                default:
                    break;

            }


        }
    }

}
