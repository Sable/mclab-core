package matlab;

public class MatlabTranslatorTests extends TranslatorTestBase {

	public void test_translator_command_ellipses() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_ellipses.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_ellipses.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_command_quotes() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_quotes.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_quotes.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_command_brackets() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_brackets.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_brackets.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_command_hard() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_hard.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_hard.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_command_tabs() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_tabs.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_tabs.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_command_other() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_other.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_other.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_command_eof() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_command_eof.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_command_eof.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_plus() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_plus.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_plus.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_simple() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_simple.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_simple.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_colsep() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_colsep.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_colsep.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_rowsep() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_rowsep.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_rowsep.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_nested() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_nested.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_nested.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_comments() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_comments.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_comments.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_array_other() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_array_other.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_array_other.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_all() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_all.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_all.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_none() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_none.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_none.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_hidden() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_hidden.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_hidden.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_command() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_command.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_command.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_insertion1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_insertion1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_insertion1.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_insertion2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_insertion2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_insertion2.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_insertion3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_insertion3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_insertion3.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_functionend_insertion4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_functionend_insertion4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_functionend_insertion4.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_mixed_empty() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_mixed_empty.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_mixed_empty.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_mixed_notcmd() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_mixed_notcmd.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_mixed_notcmd.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_mixed_all1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_mixed_all1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_mixed_all1.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_mixed_all2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_mixed_all2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_mixed_all2.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_mixed_all3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_mixed_all3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_mixed_all3.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_mixed_all4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_mixed_all4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_mixed_all4.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_leadingnl_newlinefunction() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_leadingnl_newlinefunction.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_leadingnl_newlinefunction.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_csheader_if_body() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_csheader_if_body.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_csheader_if_body.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_csheader_if_nobody() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_csheader_if_nobody.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_csheader_if_nobody.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_csheader_switch_body() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_csheader_switch_body.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_csheader_switch_body.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_csheader_switch_nobody() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_csheader_switch_nobody.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_csheader_switch_nobody.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_comment_class() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_comment_class.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_comment_class.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_comment_function() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_comment_function.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_comment_function.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_comment_functionlist() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_comment_functionlist.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_comment_functionlist.out");
		assertEquiv(actual, expected);
	}

	public void test_translator_comment_functionend() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/translator_comment_functionend.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/translator_comment_functionend.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpasstest1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpasstest1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpasstest1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpasstest2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpasstest2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpasstest2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_unary() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_unary.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_unary.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_binary() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_binary.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_binary.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_precedence() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_precedence.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_precedence.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_arithprecedence() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_arithprecedence.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_arithprecedence.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_logicalprecedence() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_logicalprecedence.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_logicalprecedence.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_compprecedence() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_compprecedence.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_compprecedence.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_parensprecedence() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_parensprecedence.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_parensprecedence.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_eof1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_eof1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_eof1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_eof2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_eof2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_eof2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_eof3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_eof3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_eof3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_eof4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_eof4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_eof4.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_empty1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_empty1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_empty1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_empty2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_empty2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_empty2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_empty3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_empty3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_empty3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_helpcomments1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_helpcomments1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_helpcomments1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_helpcomments2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_helpcomments2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_helpcomments2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_helpcomments3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_helpcomments3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_helpcomments3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_comments1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_comments1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_comments1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_comments2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_comments2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_comments2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_comments3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_comments3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_comments3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_comments4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_comments4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_comments4.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_comments5() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_comments5.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_comments5.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_comments6() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_comments6.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_comments6.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_misc() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_misc.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_misc.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_matrix1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_matrix1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_matrix1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_matrixempty() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_matrixempty.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_matrixempty.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_matrixunary() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_matrixunary.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_matrixunary.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_matrixbinary() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_matrixbinary.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_matrixbinary.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_matrixcomments() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_matrixcomments.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_matrixcomments.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_matrixaccess() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_matrixaccess.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_matrixaccess.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_onelinefunction() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_onelinefunction.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_onelinefunction.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functionlist() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functionlist.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functionlist.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness4.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness5() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness5.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness5.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness6() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness6.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness6.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness7() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness7.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness7.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness8() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness8.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness8.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness9() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness9.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness9.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness10() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness10.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness10.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness11() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness11.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness11.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness12() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness12.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness12.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness13() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness13.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness13.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness14() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness14.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness14.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness15() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness15.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness15.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_functioncompleteness16() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_functioncompleteness16.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_functioncompleteness16.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_numbers() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_numbers.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_numbers.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_lambda() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_lambda.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_lambda.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cell1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cell1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cell1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellcomments() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellcomments.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellcomments.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellempty() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellempty.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellempty.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellunary() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellunary.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellunary.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_if1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_if1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_if1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_if2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_if2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_if2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifelse2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifelse2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifelse2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifelseif1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifelseif1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifelseif1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifelseif2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifelseif2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifelseif2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifelseif3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifelseif3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifelseif3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifelseifelse1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifelseifelse1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifelseifelse1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifelseifelse2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifelseifelse2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifelseifelse2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_ifend1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_ifend1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_ifend1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch4.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch5() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch5.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch5.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch6() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch6.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch6.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_switch7() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_switch7.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_switch7.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_trycatch1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_trycatch1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_trycatch1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_trycatch2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_trycatch2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_trycatch2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_for1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_for1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_for1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_for2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_for2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_for2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_for3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_for3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_for3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_while1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_while1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_while1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_while2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_while2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_while2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellcontent1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellcontent1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellcontent1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellcontent2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellcontent2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellcontent2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellcontent3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellcontent3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellcontent3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellindex1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellindex1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellindex1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellindex2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellindex2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellindex2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellindex3() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellindex3.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellindex3.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_cellindex4() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_cellindex4.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_cellindex4.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_try1() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_try1.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_try1.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_try2() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_try2.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_try2.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_access() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_access.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_access.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_class() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_class.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_class.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_command() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_command.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_command.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_end() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_end.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_end.out");
		assertEquiv(actual, expected);
	}

	public void test_parserpass_powprefix() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/parserpass_powprefix.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/parserpass_powprefix.out");
		assertEquiv(actual, expected);
	}

	public void test_annotation_parser() throws Exception {
		ActualTranslation actual = parseActualTranslation("test/annotation_parser.in");
		ExpectedTranslation expected = parseExpectedTranslation("test/annotation_parser.out");
		assertEquiv(actual, expected);
	}
}

