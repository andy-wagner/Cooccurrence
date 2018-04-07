package org.cogcomp.nlp.statistics.cooccurrence.datastructure;

import mikera.vectorz.Vector2;


/**
 * A simplified implementation for Compressed Column Storage sparse matrix for double values.
 * This implemenation requires the matrix non-zero entries positions are immutable.
 *
 * Currently the matrix value is native and read-only, so it's thread-safe.
 *
 * Vector math operations involved in this implementation are supported by netlib-java {@link }
 *
 * @author Sihao
 */
public class SparseDoubleCCSMatrix {

    int[] rowidx;

    int[] colptr;

    double[] value;

    int numRow;

    int numCol;

    public SparseDoubleCCSMatrix(int[] rowidx, int[] colptr, double[] value) {
        this.rowidx = rowidx;
        this.colptr = colptr;
        this.value = value;
        this.numCol = colptr.length - 1;

    }

    public Vector2 getRow(int i) {

        for (int cid = 0; cid < numCol; cid++) {
            
        }
        Vector2 row = new Vector2();
    }

    public void writeToFile(String path) {

    }
}
