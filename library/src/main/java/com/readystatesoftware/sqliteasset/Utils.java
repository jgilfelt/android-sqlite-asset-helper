package com.readystatesoftware.sqliteasset;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Utils {

    private static final String TAG = SQLiteAssetHelper.class.getSimpleName();

    public static List<String> splitSqlScript(String script, char delim) {
        List<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inLiteral = false;
        char[] content = script.toCharArray();
        int openDelimiter = -1, lastCharacter = -1;
        for (int i = 0; i < script.length(); i++) {
            if (content[i] == '"' || content[i] == '\'') {
                if (openDelimiter == -1) {
                    // We were not inside a literal; store the delimiter's value.
                    openDelimiter = content[i];
                    inLiteral = true;
                } else if (openDelimiter == content[i] && lastCharacter != '\\') {
                    // We exit from the literal only on the same character
                    // AND unless we're being escaped by a bachslash.
                    inLiteral = false;
                    openDelimiter = -1;
                }
            }
            if (content[i] == delim && !inLiteral) {
                if (sb.length() > 0) {
                    statements.add(sb.toString().trim());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(content[i]);
            }
            lastCharacter = content[i];
        }
        if (sb.length() > 0) {
            statements.add(sb.toString().trim());
        }
        return statements;
    }

    public static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer))>0){
            outs.write(buffer, 0, length);
        }
        outs.flush();
        outs.close();
        in.close();
    }

    public static ZipInputStream getFileFromZip(InputStream zipFileStream) throws IOException {
        ZipInputStream zis = new ZipInputStream(zipFileStream);
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            Log.w(TAG, "extracting file: '" + ze.getName() + "'...");
            return zis;
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) {
        return new Scanner(is).useDelimiter("\\A").next();
    }

}
