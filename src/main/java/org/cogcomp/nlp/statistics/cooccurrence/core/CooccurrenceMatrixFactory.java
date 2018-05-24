package org.cogcomp.nlp.statistics.cooccurrence.core;

import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.io.*;
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
    public static ImmutableTermDocMatrix createImmutableTermDocMatFromSave(String lexPath, String matPath) throws IOException, IllegalArgumentException{

        System.out.println("Loading Matrix Data...");
        Scanner scan = new Scanner(new FileReader(matPath));
        scan.nextInt();
        BufferedReader br = new BufferedReader(new FileReader(matPath));

        System.out.print("\tLoading column pointers...");
        String _colptr = br.readLine();
        int[] colptr = Util.parseIntArray(_colptr);
        _colptr = null;
        System.gc();
        System.out.println("Done!");

        System.out.print("\tLoading row indices...");
        String _rowidx = br.readLine();
        int[] rowidx = Util.parseIntArray(_rowidx);
        _rowidx = null;
        System.gc();
        System.out.println("Done!");

        System.out.print("\tLoading entry values...");
        String _val = br.readLine();
        double[] val = Util.parseDoubleArray(_val);
        _val = null;
        System.gc();
        System.out.println("Done!");


        IncrementalIndexedLexicon lex = createIndexedLexiconFromSave(lexPath);

        return new ImmutableTermDocMatrix(lex.size(), colptr.length - 1, colptr, rowidx, val, lex);
    }

    /**
     * Load an instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.IncrementalIndexedLexicon} from previous save.
     *
     * @param lexPath path to the saved lexicon
     * @return An instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.IncrementalIndexedLexicon}
     * @throws IOException when fails to read save
     * @throws IllegalArgumentException when the save is not valid
     */
    public static IncrementalIndexedLexicon createIndexedLexiconFromSave(String lexPath) throws IOException, IllegalArgumentException{

        System.out.print("Loading Lexicon...");

        String line;
        BufferedReader br = new BufferedReader(new FileReader(lexPath));

        IncrementalIndexedLexicon lex = new IncrementalIndexedLexicon();

        while ((line = br.readLine()) != null) {
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\t");
            if (parts.length != 2)
                throw new IllegalArgumentException("The lexicon save (\".lex\") is not valid! " + lexPath +
                        "\nError at line: " + line);
            lex.putOrGet(parts[1]);
        }

        System.out.println("Done");
        return lex;
    }

}
