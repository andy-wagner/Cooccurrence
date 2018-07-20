package org.cogcomp.nlp.cooccurrence;

import org.la4j.matrix.sparse.CCSMatrix;

public class Test {
    public static void main(String[] args) {
        CCSMatrix mat = new CCSMatrix(3, 4);
        mat.set(0, 0, 1);
        mat.set(2, 1, 2);
    }

}
