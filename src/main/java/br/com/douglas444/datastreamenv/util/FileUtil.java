package br.com.douglas444.datastreamenv.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;

public class FileUtil {

    public static FileReader getFileReader(final String fileName) throws MalformedURLException {

        File file = new File(fileName);
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return fileReader;
    }

}
