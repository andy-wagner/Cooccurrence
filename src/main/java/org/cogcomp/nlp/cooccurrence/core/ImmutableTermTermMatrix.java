package org.cogcomp.nlp.cooccurrence.core;

import com.google.common.annotations.Beta;
import org.cogcomp.nlp.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.la4j.Matrix;

import java.util.List;

/**
 * TODO: WIP. Haven't decided what to do with this
 */
@Beta
public class ImmutableTermTermMatrix {

    final Matrix coocMat;

    private final IncrementalIndexedLexicon lex;

    /**
     * Instantiate CoocMatrix from term-document matrix
     * @param tdmat
     */
    ImmutableTermTermMatrix(ImmutableTermDocMatrix tdmat) {
        this.lex = tdmat.getLexicon();
        this.coocMat = tdmat.tdMat.multiplyByItsTranspose();
    }

    public int getCoocCount(String term1, String term2) {
        return (int) coocMat.get(lex.putOrGet(term1), lex.putOrGet(term2));
    }

    private double _getCoocCount(int term1, int term2) {
        return coocMat.get(term1, term2);
    }
}
