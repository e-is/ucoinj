package io.ucoin.ucoinj.core.util;

import java.io.File;
import java.io.IOException;

/**
 * Created by blavenie on 05/01/16.
 */
public class FileUtils {

    public static void forceMkdir(File directory) throws IOException {
        String message;
        if(directory.exists()) {
            if(!directory.isDirectory()) {
                message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else if(!directory.mkdirs() && !directory.isDirectory()) {
            message = "Unable to create directory " + directory;
            throw new IOException(message);
        }

    }
}
