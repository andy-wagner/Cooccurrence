package org.cogcomp.nlp.statistics.cooccurrence.core;

import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;

import java.io.*;

public class CoocMatrixFactory {

    /**
     * Load an instance of {@link org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix} from previous save.
     *
     * @param lexPath path to the saved lexicon
     * @param matPath path to the saved matrix data
     * @return An instance of {@link ImmutableTermDocMatrix}.
     * @throws IOException when fails to read save
     */
    public static ImmutableTermDocMatrix createImmutableTermDocMatFromSave(String lexPath, String matPath) throws IOException {
        return new ImmutableTermDocMatrix(lexPath, matPath);
    }

    /**
     * Load an instance of {@link IncrementalIndexedLexicon} from previous save.
     *
     * @param lexPath path to the saved lexicon
     * @return An loaded instance of {@link IncrementalIndexedLexicon} from previous save
     * @throws IOException when fails to read save
     */
    public static IncrementalIndexedLexicon createIndexedLexiconFromSave(String lexPath) throws IOException {
        return new IncrementalIndexedLexicon(lexPath);
    }
}
