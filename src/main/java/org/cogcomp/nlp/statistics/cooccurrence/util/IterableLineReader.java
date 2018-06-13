package org.cogcomp.nlp.statistics.cooccurrence.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Come in handy when you have a huge text file and you want to iterate through lines without loading them all in memory.
 */
public class IterableLineReader implements Iterable<String> {

    private BufferedReader reader;

    public IterableLineReader(File f) throws IOException {
        reader = new BufferedReader(new FileReader(f));
    }

    public IterableLineReader(String filepath) throws IOException {
        reader = new BufferedReader(new FileReader(filepath));
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private String line = null;

            @Override
            public boolean hasNext() {
                try {
                    line = reader.readLine();
                    if (line != null) {
                        return true;
                    }
                    else {
                        reader.close();
                        return false;
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String next() {
                return line;
            }
        };
    }


}
