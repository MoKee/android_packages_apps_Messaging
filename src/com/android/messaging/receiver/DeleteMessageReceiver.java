/*
 * Copyright (C) 2017-2018 The MoKee Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.messaging.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.android.messaging.Factory;
import com.android.messaging.datamodel.DatabaseHelper.PartColumns;
import com.android.messaging.datamodel.action.DeleteMessageAction;
import com.android.messaging.util.PendingIntentConstants;

public class DeleteMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null) {
            // We have nothing, abort
            return;
        }

        String messageId = extras.getString(PartColumns.MESSAGE_ID);
        if (!TextUtils.isEmpty(messageId)) {
            DeleteMessageAction.deleteMessage(messageId);
            final NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(Factory.get().getApplicationContext());
            notificationManager.cancel(PendingIntentConstants.CAPTCHAS_NOTIFICATION_ID);
        }
    }

}
