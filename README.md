# Cooccurrence

A java library for fast, large-scale computation of text co-occurrence statistics. 

This library supports two types of co-occurrence statistics. First, the [`ImmutableTermDocMatrix`](src/main/java/org/cogcomp/nlp/cooccurrence/core/ImmutableTermDocMatrix.java) 
class provides abstraction of a [Term-Document Matrix](https://en.wikipedia.org/wiki/Document-term_matrix) over a collection of "documents". The Term-Document Matrix
can in turn be used to create the second type of cooc matrix -- [`ImmutableCoocMatrix`](src/main/java/org/cogcomp/nlp/cooccurrence/core/ImmutableCoocMatrix.java),
-- a Term-Term co-occurrence matrix that stores co-occurrence counts of two term over the entire collection of documents. 

This library is for you if:
- You need to compute n-gram counts from a humongous collection of documents, and you don't have much time to spare.
- You don't need full index from lucene.
- You don't have access to a fancy server with 100GB+ RAM 

## Usage
Here's the steps you need to take to compute cooc statistics with this library
### 1. Process Term-Document co-occurrence counts
(Skipping this part, since it's not relevant for now; TODO: finish this)
### 2. Retrieve counts from saved Term-Document matrix
```java
String directory = "...";      // Directory where you saved the matrix
String savename = "...";       // Name you saved the matrix under
ImmutableTermDocMatrix tdmat = CoocMatrixFactory.createTermDocMatFromSave(directory, savename);
```
To retrieve counts of a term appearing in a document
```java
String term = "...";
String documentId = "...";
int count = tdmat.getTermCountInDoc(term, documentId);
```
To retrieve co-occurrence counts of two terms over the entire document collection
```java
String term1 = "Obama";
String term2 = "Hillary";
int count = tdmat.getCoocCount(term1, term2);
```
To retrieve total counts of a term over the entire document collection
```java
String term = "Obama";
int count = tdmat.getTermTotalCount(term);
```
