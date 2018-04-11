package org.cogcomp.nlp.statistics.cooccurrence.core;

import edu.illinois.cs.cogcomp.core.io.LineIO;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CooccurrenceMatrixFactory {

//    /**
//     * Load an instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix} from previous save.
//     *
//     * @param saveDir a directory that is expected to contain two files. One with extension ".lex" that contains
//     *                the term to matrix index mapping.
//     *                See {@link IncrementalIndexedLexicon} for details.
//     *                The other file with ".mat" extension stores the actual matrix data.
//     *                See {@link org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix#saveMat(OutputStream)}
//     *                for more details.
//     *
//     * @return An instance of {@link ImmutableTermDocMatrix}.
//     */
//    public static ImmutableTermDocMatrix createImmutableTermDocMatFromSave(String saveDir) throws IOException {
//        File dir = new File(saveDir);
//
//        if (!dir.isDirectory() || !dir.exists())
//            throw new IOException("Either not a directory or the directory doesn't exist! " + saveDir);
//
//
//
//    }

    /**
     * Load an instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix} from previous save.
     *
     * @param lexPath path to the saved lexicon
     * @param matPath path to the saved matrix data
     * @return An instance of {@link ImmutableTermDocMatrix}.
     * @throws IOException when fails to read save
     * @throws IllegalArgumentException when the save is not valid
     */
//    public static ImmutableTermDocMatrix createImmutableTermDocMatFromSave(String lexPath, String matPath) throws IOException, IllegalArgumentException{
//
////        List<String> _matData =  LineIO.read(matPath); // TODO: Not mem efficient. reading line by line will save memory
//        Scanner scan = new Scanner(new FileReader(matPath));
//        scan.nextInt();
//        BufferedReader br = new BufferedReader(new FileReader(matPath));
//
////        if (_matData.size() < 3)
////            throw new IllegalArgumentException("The matrix data save (\".mat\") is not valid! " + matPath); // TODO: shouldn't be IllegalArgumentException
//
//        String _colptr = br.readLine();
//        int[] colptr = Arrays.stream(_colptr).mapToInt(Integer::parseInt).toArray();
//        _colptr = null;
//        System.gc();
//
//        String[] _rowidx = br.readLine().split(" ");
//        int[] rowidx = Arrays.stream(_rowidx).mapToInt(Integer::parseInt).toArray();
//        _rowidx = null;
//
//        String[] _val = br.readLine().split(" ");
//        double[] val = Arrays.stream(_val).mapToDouble(Double::parseDouble).toArray();
//        _val = null;
//
//        IncrementalIndexedLexicon lex = createIndexedLexiconFromSave(lexPath);
//
//        return new ImmutableTermDocMatrix(lex.size(), colptr.length - 1, colptr, rowidx, val, lex);
//    }

    /**
     * Load an instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.IncrementalIndexedLexicon} from previous save.
     *
     * @param lexPath path to the saved lexicon
     * @return An instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.IncrementalIndexedLexicon}
     * @throws IOException when fails to read save
     * @throws IllegalArgumentException when the save is not valid
     */
    public static IncrementalIndexedLexicon createIndexedLexiconFromSave(String lexPath) throws IOException, IllegalArgumentException{

        List<String> _matData = LineIO.read(lexPath);

        IncrementalIndexedLexicon lex = new IncrementalIndexedLexicon();

        for (String line: _matData) {
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\t");
            if (parts.length != 2)
                throw new IllegalArgumentException("The lexicon save (\".lex\") is not valid! " + lexPath +
                        "\nError at line: " + line);
            lex.putOrGet(parts[1]);
        }

        return lex;
    }
}
