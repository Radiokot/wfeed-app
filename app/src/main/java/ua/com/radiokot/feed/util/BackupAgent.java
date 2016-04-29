package ua.com.radiokot.feed.util;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.app.backup.RestoreObserver;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;

/**
 * Агент бекапа данных.
 */
public class BackupAgent extends BackupAgentHelper
{
    @Override
    public void onCreate()
    {
        FileBackupHelper fileBackupHelper =
                new FileBackupHelper(this, "../databases/" + Database.DATABASE_NAME);
        addHelper(Database.DATABASE_NAME, fileBackupHelper);

        SharedPreferencesBackupHelper preferencesBackupHelper =
                new SharedPreferencesBackupHelper(this, getPackageName() + "_preferences");
        addHelper("prefs", preferencesBackupHelper);
    }

    // Запрос бекапа.
    public static void requestBackup(Context context)
    {
        BackupManager bm = new BackupManager(context);
        bm.dataChanged();
    }

    // Запрос восстановления.
    public static void requestRestore(Context context)
    {
        BackupManager bm = new BackupManager(context);
        bm.requestRestore(new RestoreObserver()
        {
        });
    }
}
