package com.readystatesoftware.sqliteasset;

import android.util.Log;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper.SQLiteAssetException;

/**
 * Compare paths by their upgrade version numbers, instead of using
 * alphanumeric comparison on plain file names. This prevents the upgrade
 * scripts from being applied out of order when they first move to double-,
 * triple-, etc. digits.
 * <p>
 * For example, this fixes an upgrade that would apply 2 different upgrade
 * files from version 9 to 11 (<code>..._updated_9_10</code> and
 * <code>..._updated_10_11</code>) from using the <em>incorrect</em>
 * alphanumeric order of <code>10_11</code> before <code>9_10</code>.
 * </p>
 */
class VersionComparator implements Comparator<String> {

    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();

    private Pattern pattern = Pattern
            .compile(".*_upgrade_([0-9]+)-([0-9]+).*");

    /**
     * Compares the two specified upgrade script strings to determine their
     * relative ordering considering their two version numbers. Assumes all
     * database names used are the same, as this function only compares the
     * two version numbers.
     *
     * @param file0
     *            an upgrade script file name
     * @param file1
     *            a second upgrade script file name to compare with file0
     * @return an integer < 0 if file0 should be applied before file1, 0 if
     *         they are equal (though that shouldn't happen), and > 0 if
     *         file0 should be applied after file1.
     *
     * @exception SQLiteAssetException
     *                thrown if the strings are not in the correct upgrade
     *                script format of:
     *                <code>databasename_fromVersionInteger_toVersionInteger</code>
     */
    @Override
    public int compare(String file0, String file1) {
        Matcher m0 = pattern.matcher(file0);
        Matcher m1 = pattern.matcher(file1);

        if (!m0.matches()) {
            Log.w(TAG, "could not parse upgrade script file: " + file0);
            throw new SQLiteAssetException("Invalid upgrade script file");
        }

        if (!m1.matches()) {
            Log.w(TAG, "could not parse upgrade script file: " + file1);
            throw new SQLiteAssetException("Invalid upgrade script file");
        }

        int v0_from = Integer.valueOf(m0.group(1));
        int v1_from = Integer.valueOf(m1.group(1));
        int v0_to = Integer.valueOf(m0.group(2));
        int v1_to = Integer.valueOf(m1.group(2));

        if (v0_from == v1_from) {
            // 'from' versions match for both; check 'to' version next

            if (v0_to == v1_to) {
                return 0;
            }

            return v0_to < v1_to ? -1 : 1;
        }

        return v0_from < v1_from ? -1 : 1;
    }
}
