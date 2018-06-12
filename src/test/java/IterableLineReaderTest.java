import org.cogcomp.nlp.statistics.cooccurrence.util.IterableLineReader;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IterableLineReaderTest {
    @Test
    public void testReader() {
        try {
            String[] lines = {"1", "2", "3", "4", "5"};
            File tmp = File.createTempFile("iterable-file-reader", ".txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
            for (String line: lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();

            Iterable<String> _lines = new IterableLineReader(tmp);

            int count = 0;
            for (String _line: _lines) {
                assertEquals(lines[count], _line);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
