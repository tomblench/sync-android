/**
 * Copyright (c) 2013 Cloudant, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.sync.util;

import com.cloudant.sync.datastore.DocumentBody;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.sqlite.sqlite4java.SQLiteWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.UUID;

public class TestUtils {

    private final static String DATABASE_FILE_EXT = ".sqlite4java";

    public static DocumentBody createBDBody(String filePath) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File(filePath));
        return DocumentBodyFactory.create(data);

    }

    public static SQLiteWrapper createEmptyDatabase(String database_dir, String database_file) throws IOException, SQLException {
        File dbFile = new File(database_dir + File.separator + database_file + DATABASE_FILE_EXT);
        FileUtils.touch(dbFile);
        SQLiteWrapper database = SQLiteWrapper.openSQLiteWrapper(dbFile.getAbsolutePath());
        return database;
    }

    public static void deleteDatabaseQuietly(SQLiteWrapper database) {
        try {
            if (database != null) {
                database.close();
            }
        } catch (Exception e) {
        }

        try {
            if (database != null) {
                FileUtils.deleteQuietly(new File(database.getDatabaseFile()));
            }
        } catch (Exception e) {
        }
    }

    public static String createTempTestingDir(String dirPrefix) {
        String tempPath = String.format(
                "%s_%s",
                dirPrefix,
                UUID.randomUUID().toString()
        );
        File f = new File(
                FileUtils.getTempDirectory().getAbsolutePath(),
                tempPath);
        f.mkdirs();
        return f.getAbsolutePath();
    }

    public static void deleteTempTestingDir(String path) {
        FileUtils.deleteQuietly(new File(path));
    }

    public static String createTempFile(String dir, String file) throws IOException {
        File f = new File(dir + File.separator + file);
        FileUtils.touch(f);
        return f.getAbsolutePath();
    }

    // iterate through both streams byte-for-byte and check they are equal
    // exit false if we get to the end of one stream before the other (they are different lengths)
    // or if two bytes at the same point in the streams aren't equal
    public static boolean streamsEqual(InputStream is1, InputStream is2){
        int c1, c2;
        boolean equal = true;
        try {
            while ((c1 = is1.read()) != -1) {
                c2 = is2.read();
                // % is 'any' metacharacter
                if (c1 == '%') {
                    continue;
                }
                if (c1 != c2) {
                    equal = false;
                    break;
                }
            }
            if (is2.read() != -1) {
                // more bytes in the 2nd stream
                return false;
            }
        } catch (IOException ioe) {
            return false;
        }
        return equal;
    }

}
