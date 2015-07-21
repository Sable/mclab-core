package octave;

import java.util.ArrayList;
import java.util.List;

public class OctaveScannerTests extends ScannerTestBase {

	public void test_scanner_allsymbols() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_allsymbols.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_allsymbols.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_numbers() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_numbers.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_numbers.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_ellipsis() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_ellipsis.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_ellipsis.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_comment() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_comment.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_comment.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_keywords() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_keywords.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_keywords.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_operators() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_operators.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_operators.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_dot() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_dot.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_dot.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_string() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_string.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_string.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_shell() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_shell.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_shell.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_badbracketcomment() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_badbracketcomment.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_badbracketcomment.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_statestack() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_statestack.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_statestack.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_classdef() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_classdef.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_classdef.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_terminator() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_terminator.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_terminator.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_transpose() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_transpose.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_transpose.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_badtranspose() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_badtranspose.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_badtranspose.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_ellipsistranspose() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_ellipsistranspose.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_ellipsistranspose.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_scanner_class() throws Exception {
		OctaveLexer lexer = getLexer("test/scanner_class.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/scanner_class.out", symbols);
		checkScan(lexer, symbols, exception);
	}

	public void test_annotation_scanner() throws Exception {
		OctaveLexer lexer = getLexer("test/annotation_scanner.in");
		List<Symbol> symbols = new ArrayList<Symbol>();
		TextPosition exception = parseSymbols("test/annotation_scanner.out", symbols);
		checkScan(lexer, symbols, exception);
	}
}

