package org.cogcomp.nlp.statistics.cooccurrence.core;

import com.google.common.annotations.Beta;
import javafx.util.Pair;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.la4j.Matrix;

import java.util.List;

/**
 * TODO: WIP. Haven't decided what to do with this
 */
@Beta
public class ImmutableCoocMatrix {

    final Matrix coocMat;

    private final IncrementalIndexedLexicon lex;

    /**
     * Instantiate CoocMatrix from term-document matrix
     * @param tdmat
     */
    ImmutableCoocMatrix(ImmutableTermDocMatrix tdmat) {
        this.lex = tdmat.getLexicon();
        this.coocMat = tdmat.tdMat.multiplyByItsTranspose();
        System.out.println(coocMat.getClass().getCanonicalName());
    }

    public int getCoocCount(String term1, String term2) {
        return (int) coocMat.get(lex.putOrGet(term1), lex.putOrGet(term2));
    }

    private double _getCoocCount(int term1, int term2) {
        return coocMat.get(term1, term2);
    }

    /**
     * Get the list of terms that co-occurred with the given term. The list will be sorted by their co-occurrence conuts
     * @param term
     * @return A sorted list of (term, cooc count) pairs (in descending order by cooc counts)
     */
    public List<Pair<String, Integer>> getCoocTerms(String term) {
        coocMat.getRow(lex.putOrGet(term));
        return null;
    }
}
