/*
 * Copyright (c) 2022 2bllw8
 * SPDX-License-Identifier: GPL-3.0-only
 */
package exe.bbllw8.anemo.shell;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.annotation.Nullable;

import exe.bbllw8.anemo.R;
import exe.bbllw8.anemo.documents.home.HomeEnvironment;
import exe.bbllw8.anemo.lock.LockStore;
import exe.bbllw8.anemo.lock.UnlockActivity;

public class LauncherActivity extends Activity {
    // https://cs.android.com/android/platform/superproject/+/master:packages/apps/DocumentsUI/AndroidManifest.xml
    private static final String DOCUMENTS_UI_PACKAGE =
            "com.android.documentsui";
    private static final String DOCUMENTS_UI_ACTIVITY =
            DOCUMENTS_UI_PACKAGE + ".files.FilesActivity";
    private static final String GOOGLE_DOCUMENTS_UI_PACKAGE =
            "com.google.android.documentsui";
    private static final Uri ANEMO_URI = DocumentsContract.buildRootsUri(
            HomeEnvironment.AUTHORITY);

    private final Intent[] LAUNCHER_INTENTS = {
            new Intent(Intent.ACTION_VIEW)
                    .setData(ANEMO_URI)
                    .setClassName(DOCUMENTS_UI_PACKAGE, DOCUMENTS_UI_ACTIVITY),
            new Intent(Intent.ACTION_VIEW)
                    .setData(ANEMO_URI)
                    .setClassName(GOOGLE_DOCUMENTS_UI_PACKAGE, DOCUMENTS_UI_ACTIVITY),
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LockStore.getInstance(this).isLocked()) {
            startActivity(new Intent(this, UnlockActivity.class)
                    .putExtra(UnlockActivity.OPEN_AFTER_UNLOCK, true));
        } else {
            final PackageManager pm = getPackageManager();
            int i = 0;
            while (i < LAUNCHER_INTENTS.length) {
                final Intent intent = LAUNCHER_INTENTS[i];
                if (canHandle(pm, intent)) {
                    startActivity(intent);
                    break;
                }
                i++;
            }
            if (i == LAUNCHER_INTENTS.length) {
                Toast.makeText(this, R.string.launcher_no_activity, Toast.LENGTH_LONG)
                        .show();
            }
        }
        finish();
    }

    private boolean canHandle(PackageManager pm, Intent intent) {
        return pm.resolveActivity(intent, PackageManager.MATCH_ALL) != null;
    }
}
