/*
 * Copyright (C) 2015-2018 The MoKee Open Source Project
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

import com.android.messaging.R;
import com.android.messaging.Factory;
import com.android.messaging.datamodel.BugleNotifications;
import com.android.messaging.datamodel.DatabaseHelper.ConversationColumns;
import com.android.messaging.datamodel.DatabaseHelper.PartColumns;
import com.android.messaging.datamodel.action.DeleteMessageAction;
import com.android.messaging.sms.MmsUtils;
import com.android.messaging.util.PendingIntentConstants;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import android.support.v4.app.NotificationManagerCompat;

public class CaptchasReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null) {
            // We have nothing, abort
            return;
        }

        String captchas = extras.getString("captchas");
        String conversationId = extras.getString(ConversationColumns.SMS_THREAD_ID);
        if (!TextUtils.isEmpty(captchas)) {
            ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(captchas);
            Toast.makeText(context, String.format(context.getString(R.string.captchas_has_copied), captchas), Toast.LENGTH_SHORT).show();

            if (MmsUtils.allowAutoDeleteCaptchaSms()) {
                String messageId = extras.getString(PartColumns.MESSAGE_ID);
                DeleteMessageAction.deleteMessage(messageId);
            } else {
                // Mark thread as read
                BugleNotifications.markMessagesAsRead(conversationId);
            }

            final NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(Factory.get().getApplicationContext());
            notificationManager.cancel(PendingIntentConstants.CAPTCHAS_NOTIFICATION_ID);
        }
    }
}
