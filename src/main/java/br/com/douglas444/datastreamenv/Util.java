package br.com.douglas444.datastreamenv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

public class Util {

    public static FileReader getFileReader(String fileName) {
        URL url = Util.class.getClassLoader().getResource(fileName);
        assert url != null;
        File file = new File(url.getFile());
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
