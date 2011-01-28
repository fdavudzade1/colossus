package com.codexperiments.colossus.utility;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;


public class FileUtility
{
    public static void copyFileToFile (File pSourceFile, File pDestinationFile) throws IOException
    {
        FileChannel lInChannel = new FileInputStream(pSourceFile).getChannel();
        FileChannel lOutChannel = new FileOutputStream(pDestinationFile).getChannel();

        try {
            lInChannel.transferTo(0, lInChannel.size(), lOutChannel);
        } finally {
            if (lInChannel != null) {
                lInChannel.close();
            }
            if (lOutChannel != null) {
                lOutChannel.close();
            }
        }
    }

    public static void backupDatabase (Context pActivityContext, String pDatabaseName, String pExportDir)
    throws IOException
    {
        // Retrieves source file path.
        File lDatabaseFile = pActivityContext.getDatabasePath(pDatabaseName);

        // Retrieves and creates export directory.
        File lExportDir = new File(Environment.getExternalStorageDirectory(), pExportDir);
        if (!lExportDir.exists()) {
            lExportDir.mkdirs();
        }

        // Creates and copy export file.
        File lExportFile = new File(lExportDir, lDatabaseFile.getName());
        lExportFile.createNewFile();
        copyFileToFile(lDatabaseFile, lExportFile);
    }
}
