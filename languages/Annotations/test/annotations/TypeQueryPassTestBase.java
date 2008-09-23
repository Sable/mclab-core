package annotations;

import java.io.*;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import annotations.ast.*;
import beaver.Symbol;

/**
 * Parent class of the generated AnnotationParserPassTests class.
 */

class TypeQueryPassTestBase extends TestCase
{
    /* Construct a scanner that will read from the specified file. */
    static AnnotationScanner getScanner(String filename) throws FileNotFoundException {
        return new AnnotationScanner(new BufferedReader(new FileReader(filename)));
    }
}