// Generated from Octave.g4 by ANTLR 4.5

    import java.util.*;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link OctaveParser}.
 */
public interface OctaveListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link OctaveParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(OctaveParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(OctaveParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(OctaveParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(OctaveParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#scripending}.
	 * @param ctx the parse tree
	 */
	void enterScripending(OctaveParser.ScripendingContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#scripending}.
	 * @param ctx the parse tree
	 */
	void exitScripending(OctaveParser.ScripendingContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(OctaveParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(OctaveParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#stmt_separator}.
	 * @param ctx the parse tree
	 */
	void enterStmt_separator(OctaveParser.Stmt_separatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#stmt_separator}.
	 * @param ctx the parse tree
	 */
	void exitStmt_separator(OctaveParser.Stmt_separatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#stmt_body}.
	 * @param ctx the parse tree
	 */
	void enterStmt_body(OctaveParser.Stmt_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#stmt_body}.
	 * @param ctx the parse tree
	 */
	void exitStmt_body(OctaveParser.Stmt_bodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ASSIGN}
	 * labeled alternative in {@link OctaveParser#maybe_cmd}.
	 * @param ctx the parse tree
	 */
	void enterASSIGN(OctaveParser.ASSIGNContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ASSIGN}
	 * labeled alternative in {@link OctaveParser#maybe_cmd}.
	 * @param ctx the parse tree
	 */
	void exitASSIGN(OctaveParser.ASSIGNContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IGNORE}
	 * labeled alternative in {@link OctaveParser#maybe_cmd}.
	 * @param ctx the parse tree
	 */
	void enterIGNORE(OctaveParser.IGNOREContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IGNORE}
	 * labeled alternative in {@link OctaveParser#maybe_cmd}.
	 * @param ctx the parse tree
	 */
	void exitIGNORE(OctaveParser.IGNOREContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#not_cmd_lookahead}.
	 * @param ctx the parse tree
	 */
	void enterNot_cmd_lookahead(OctaveParser.Not_cmd_lookaheadContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#not_cmd_lookahead}.
	 * @param ctx the parse tree
	 */
	void exitNot_cmd_lookahead(OctaveParser.Not_cmd_lookaheadContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#op}.
	 * @param ctx the parse tree
	 */
	void enterOp(OctaveParser.OpContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#op}.
	 * @param ctx the parse tree
	 */
	void exitOp(OctaveParser.OpContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#after_op}.
	 * @param ctx the parse tree
	 */
	void enterAfter_op(OctaveParser.After_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#after_op}.
	 * @param ctx the parse tree
	 */
	void exitAfter_op(OctaveParser.After_opContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CMD_ARGS}
	 * labeled alternative in {@link OctaveParser#cmd_args}.
	 * @param ctx the parse tree
	 */
	void enterCMD_ARGS(OctaveParser.CMD_ARGSContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CMD_ARGS}
	 * labeled alternative in {@link OctaveParser#cmd_args}.
	 * @param ctx the parse tree
	 */
	void exitCMD_ARGS(OctaveParser.CMD_ARGSContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#cmd_args_helper}.
	 * @param ctx the parse tree
	 */
	void enterCmd_args_helper(OctaveParser.Cmd_args_helperContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#cmd_args_helper}.
	 * @param ctx the parse tree
	 */
	void exitCmd_args_helper(OctaveParser.Cmd_args_helperContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#cmd_args_tail}.
	 * @param ctx the parse tree
	 */
	void enterCmd_args_tail(OctaveParser.Cmd_args_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#cmd_args_tail}.
	 * @param ctx the parse tree
	 */
	void exitCmd_args_tail(OctaveParser.Cmd_args_tailContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NO_COMMA}
	 * labeled alternative in {@link OctaveParser#compound_stmt_header_sep}.
	 * @param ctx the parse tree
	 */
	void enterNO_COMMA(OctaveParser.NO_COMMAContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NO_COMMA}
	 * labeled alternative in {@link OctaveParser#compound_stmt_header_sep}.
	 * @param ctx the parse tree
	 */
	void exitNO_COMMA(OctaveParser.NO_COMMAContext ctx);
	/**
	 * Enter a parse tree produced by the {@code COMMA_INSERT}
	 * labeled alternative in {@link OctaveParser#compound_stmt_header_sep}.
	 * @param ctx the parse tree
	 */
	void enterCOMMA_INSERT(OctaveParser.COMMA_INSERTContext ctx);
	/**
	 * Exit a parse tree produced by the {@code COMMA_INSERT}
	 * labeled alternative in {@link OctaveParser#compound_stmt_header_sep}.
	 * @param ctx the parse tree
	 */
	void exitCOMMA_INSERT(OctaveParser.COMMA_INSERTContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#sep_stmt_list}.
	 * @param ctx the parse tree
	 */
	void enterSep_stmt_list(OctaveParser.Sep_stmt_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#sep_stmt_list}.
	 * @param ctx the parse tree
	 */
	void exitSep_stmt_list(OctaveParser.Sep_stmt_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function_list}.
	 * @param ctx the parse tree
	 */
	void enterFunction_list(OctaveParser.Function_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function_list}.
	 * @param ctx the parse tree
	 */
	void exitFunction_list(OctaveParser.Function_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(OctaveParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(OctaveParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function_beginning}.
	 * @param ctx the parse tree
	 */
	void enterFunction_beginning(OctaveParser.Function_beginningContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function_beginning}.
	 * @param ctx the parse tree
	 */
	void exitFunction_beginning(OctaveParser.Function_beginningContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GAP}
	 * labeled alternative in {@link OctaveParser#function_separator}.
	 * @param ctx the parse tree
	 */
	void enterGAP(OctaveParser.GAPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GAP}
	 * labeled alternative in {@link OctaveParser#function_separator}.
	 * @param ctx the parse tree
	 */
	void exitGAP(OctaveParser.GAPContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function_separator_blob}.
	 * @param ctx the parse tree
	 */
	void enterFunction_separator_blob(OctaveParser.Function_separator_blobContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function_separator_blob}.
	 * @param ctx the parse tree
	 */
	void exitFunction_separator_blob(OctaveParser.Function_separator_blobContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function_ending}.
	 * @param ctx the parse tree
	 */
	void enterFunction_ending(OctaveParser.Function_endingContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function_ending}.
	 * @param ctx the parse tree
	 */
	void exitFunction_ending(OctaveParser.Function_endingContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function_body}.
	 * @param ctx the parse tree
	 */
	void enterFunction_body(OctaveParser.Function_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function_body}.
	 * @param ctx the parse tree
	 */
	void exitFunction_body(OctaveParser.Function_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#input_params}.
	 * @param ctx the parse tree
	 */
	void enterInput_params(OctaveParser.Input_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#input_params}.
	 * @param ctx the parse tree
	 */
	void exitInput_params(OctaveParser.Input_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#input_param_list}.
	 * @param ctx the parse tree
	 */
	void enterInput_param_list(OctaveParser.Input_param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#input_param_list}.
	 * @param ctx the parse tree
	 */
	void exitInput_param_list(OctaveParser.Input_param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#output_params}.
	 * @param ctx the parse tree
	 */
	void enterOutput_params(OctaveParser.Output_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#output_params}.
	 * @param ctx the parse tree
	 */
	void exitOutput_params(OctaveParser.Output_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#output_param_list}.
	 * @param ctx the parse tree
	 */
	void enterOutput_param_list(OctaveParser.Output_param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#output_param_list}.
	 * @param ctx the parse tree
	 */
	void exitOutput_param_list(OctaveParser.Output_param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#name_list}.
	 * @param ctx the parse tree
	 */
	void enterName_list(OctaveParser.Name_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#name_list}.
	 * @param ctx the parse tree
	 */
	void exitName_list(OctaveParser.Name_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#stmt_or_function}.
	 * @param ctx the parse tree
	 */
	void enterStmt_or_function(OctaveParser.Stmt_or_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#stmt_or_function}.
	 * @param ctx the parse tree
	 */
	void exitStmt_or_function(OctaveParser.Stmt_or_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#class_def}.
	 * @param ctx the parse tree
	 */
	void enterClass_def(OctaveParser.Class_defContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#class_def}.
	 * @param ctx the parse tree
	 */
	void exitClass_def(OctaveParser.Class_defContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#fill_sep}.
	 * @param ctx the parse tree
	 */
	void enterFill_sep(OctaveParser.Fill_sepContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#fill_sep}.
	 * @param ctx the parse tree
	 */
	void exitFill_sep(OctaveParser.Fill_sepContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#attributes}.
	 * @param ctx the parse tree
	 */
	void enterAttributes(OctaveParser.AttributesContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#attributes}.
	 * @param ctx the parse tree
	 */
	void exitAttributes(OctaveParser.AttributesContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(OctaveParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(OctaveParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#superclass_list}.
	 * @param ctx the parse tree
	 */
	void enterSuperclass_list(OctaveParser.Superclass_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#superclass_list}.
	 * @param ctx the parse tree
	 */
	void exitSuperclass_list(OctaveParser.Superclass_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#class_body}.
	 * @param ctx the parse tree
	 */
	void enterClass_body(OctaveParser.Class_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#class_body}.
	 * @param ctx the parse tree
	 */
	void exitClass_body(OctaveParser.Class_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#properties_block}.
	 * @param ctx the parse tree
	 */
	void enterProperties_block(OctaveParser.Properties_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#properties_block}.
	 * @param ctx the parse tree
	 */
	void exitProperties_block(OctaveParser.Properties_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#properties_body}.
	 * @param ctx the parse tree
	 */
	void enterProperties_body(OctaveParser.Properties_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#properties_body}.
	 * @param ctx the parse tree
	 */
	void exitProperties_body(OctaveParser.Properties_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#methods_block}.
	 * @param ctx the parse tree
	 */
	void enterMethods_block(OctaveParser.Methods_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#methods_block}.
	 * @param ctx the parse tree
	 */
	void exitMethods_block(OctaveParser.Methods_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#methods_body}.
	 * @param ctx the parse tree
	 */
	void enterMethods_body(OctaveParser.Methods_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#methods_body}.
	 * @param ctx the parse tree
	 */
	void exitMethods_body(OctaveParser.Methods_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#events_block}.
	 * @param ctx the parse tree
	 */
	void enterEvents_block(OctaveParser.Events_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#events_block}.
	 * @param ctx the parse tree
	 */
	void exitEvents_block(OctaveParser.Events_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#events_body}.
	 * @param ctx the parse tree
	 */
	void enterEvents_body(OctaveParser.Events_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#events_body}.
	 * @param ctx the parse tree
	 */
	void exitEvents_body(OctaveParser.Events_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#function_signature}.
	 * @param ctx the parse tree
	 */
	void enterFunction_signature(OctaveParser.Function_signatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#function_signature}.
	 * @param ctx the parse tree
	 */
	void exitFunction_signature(OctaveParser.Function_signatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#property_access}.
	 * @param ctx the parse tree
	 */
	void enterProperty_access(OctaveParser.Property_accessContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#property_access}.
	 * @param ctx the parse tree
	 */
	void exitProperty_access(OctaveParser.Property_accessContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(OctaveParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(OctaveParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#short_or_expr}.
	 * @param ctx the parse tree
	 */
	void enterShort_or_expr(OctaveParser.Short_or_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#short_or_expr}.
	 * @param ctx the parse tree
	 */
	void exitShort_or_expr(OctaveParser.Short_or_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#short_and_expr}.
	 * @param ctx the parse tree
	 */
	void enterShort_and_expr(OctaveParser.Short_and_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#short_and_expr}.
	 * @param ctx the parse tree
	 */
	void exitShort_and_expr(OctaveParser.Short_and_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#or_expr}.
	 * @param ctx the parse tree
	 */
	void enterOr_expr(OctaveParser.Or_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#or_expr}.
	 * @param ctx the parse tree
	 */
	void exitOr_expr(OctaveParser.Or_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#and_expr}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expr(OctaveParser.And_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#and_expr}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expr(OctaveParser.And_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#comp_expr}.
	 * @param ctx the parse tree
	 */
	void enterComp_expr(OctaveParser.Comp_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#comp_expr}.
	 * @param ctx the parse tree
	 */
	void exitComp_expr(OctaveParser.Comp_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#colon_expr}.
	 * @param ctx the parse tree
	 */
	void enterColon_expr(OctaveParser.Colon_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#colon_expr}.
	 * @param ctx the parse tree
	 */
	void exitColon_expr(OctaveParser.Colon_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#plus_expr}.
	 * @param ctx the parse tree
	 */
	void enterPlus_expr(OctaveParser.Plus_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#plus_expr}.
	 * @param ctx the parse tree
	 */
	void exitPlus_expr(OctaveParser.Plus_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#binary_expr}.
	 * @param ctx the parse tree
	 */
	void enterBinary_expr(OctaveParser.Binary_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#binary_expr}.
	 * @param ctx the parse tree
	 */
	void exitBinary_expr(OctaveParser.Binary_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#prefix_expr}.
	 * @param ctx the parse tree
	 */
	void enterPrefix_expr(OctaveParser.Prefix_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#prefix_expr}.
	 * @param ctx the parse tree
	 */
	void exitPrefix_expr(OctaveParser.Prefix_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#pow_expr}.
	 * @param ctx the parse tree
	 */
	void enterPow_expr(OctaveParser.Pow_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#pow_expr}.
	 * @param ctx the parse tree
	 */
	void exitPow_expr(OctaveParser.Pow_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#prefix_postfix_expr}.
	 * @param ctx the parse tree
	 */
	void enterPrefix_postfix_expr(OctaveParser.Prefix_postfix_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#prefix_postfix_expr}.
	 * @param ctx the parse tree
	 */
	void exitPrefix_postfix_expr(OctaveParser.Prefix_postfix_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#postfix_expr}.
	 * @param ctx the parse tree
	 */
	void enterPostfix_expr(OctaveParser.Postfix_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#postfix_expr}.
	 * @param ctx the parse tree
	 */
	void exitPostfix_expr(OctaveParser.Postfix_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#primary_expr}.
	 * @param ctx the parse tree
	 */
	void enterPrimary_expr(OctaveParser.Primary_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#primary_expr}.
	 * @param ctx the parse tree
	 */
	void exitPrimary_expr(OctaveParser.Primary_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterArg(OctaveParser.ArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitArg(OctaveParser.ArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#short_or_arg}.
	 * @param ctx the parse tree
	 */
	void enterShort_or_arg(OctaveParser.Short_or_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#short_or_arg}.
	 * @param ctx the parse tree
	 */
	void exitShort_or_arg(OctaveParser.Short_or_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#short_and_arg}.
	 * @param ctx the parse tree
	 */
	void enterShort_and_arg(OctaveParser.Short_and_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#short_and_arg}.
	 * @param ctx the parse tree
	 */
	void exitShort_and_arg(OctaveParser.Short_and_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#or_arg}.
	 * @param ctx the parse tree
	 */
	void enterOr_arg(OctaveParser.Or_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#or_arg}.
	 * @param ctx the parse tree
	 */
	void exitOr_arg(OctaveParser.Or_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#and_arg}.
	 * @param ctx the parse tree
	 */
	void enterAnd_arg(OctaveParser.And_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#and_arg}.
	 * @param ctx the parse tree
	 */
	void exitAnd_arg(OctaveParser.And_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#comp_arg}.
	 * @param ctx the parse tree
	 */
	void enterComp_arg(OctaveParser.Comp_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#comp_arg}.
	 * @param ctx the parse tree
	 */
	void exitComp_arg(OctaveParser.Comp_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#colon_arg}.
	 * @param ctx the parse tree
	 */
	void enterColon_arg(OctaveParser.Colon_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#colon_arg}.
	 * @param ctx the parse tree
	 */
	void exitColon_arg(OctaveParser.Colon_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#plus_arg}.
	 * @param ctx the parse tree
	 */
	void enterPlus_arg(OctaveParser.Plus_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#plus_arg}.
	 * @param ctx the parse tree
	 */
	void exitPlus_arg(OctaveParser.Plus_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#binary_arg}.
	 * @param ctx the parse tree
	 */
	void enterBinary_arg(OctaveParser.Binary_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#binary_arg}.
	 * @param ctx the parse tree
	 */
	void exitBinary_arg(OctaveParser.Binary_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#prefix_arg}.
	 * @param ctx the parse tree
	 */
	void enterPrefix_arg(OctaveParser.Prefix_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#prefix_arg}.
	 * @param ctx the parse tree
	 */
	void exitPrefix_arg(OctaveParser.Prefix_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#pow_arg}.
	 * @param ctx the parse tree
	 */
	void enterPow_arg(OctaveParser.Pow_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#pow_arg}.
	 * @param ctx the parse tree
	 */
	void exitPow_arg(OctaveParser.Pow_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#prefix_postfix_arg}.
	 * @param ctx the parse tree
	 */
	void enterPrefix_postfix_arg(OctaveParser.Prefix_postfix_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#prefix_postfix_arg}.
	 * @param ctx the parse tree
	 */
	void exitPrefix_postfix_arg(OctaveParser.Prefix_postfix_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#postfix_arg}.
	 * @param ctx the parse tree
	 */
	void enterPostfix_arg(OctaveParser.Postfix_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#postfix_arg}.
	 * @param ctx the parse tree
	 */
	void exitPostfix_arg(OctaveParser.Postfix_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#primary_arg}.
	 * @param ctx the parse tree
	 */
	void enterPrimary_arg(OctaveParser.Primary_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#primary_arg}.
	 * @param ctx the parse tree
	 */
	void exitPrimary_arg(OctaveParser.Primary_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#access}.
	 * @param ctx the parse tree
	 */
	void enterAccess(OctaveParser.AccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#access}.
	 * @param ctx the parse tree
	 */
	void exitAccess(OctaveParser.AccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#paren_access}.
	 * @param ctx the parse tree
	 */
	void enterParen_access(OctaveParser.Paren_accessContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#paren_access}.
	 * @param ctx the parse tree
	 */
	void exitParen_access(OctaveParser.Paren_accessContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#cell_access}.
	 * @param ctx the parse tree
	 */
	void enterCell_access(OctaveParser.Cell_accessContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#cell_access}.
	 * @param ctx the parse tree
	 */
	void exitCell_access(OctaveParser.Cell_accessContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void enterArg_list(OctaveParser.Arg_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void exitArg_list(OctaveParser.Arg_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(OctaveParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(OctaveParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#matrix}.
	 * @param ctx the parse tree
	 */
	void enterMatrix(OctaveParser.MatrixContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#matrix}.
	 * @param ctx the parse tree
	 */
	void exitMatrix(OctaveParser.MatrixContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#cell_array}.
	 * @param ctx the parse tree
	 */
	void enterCell_array(OctaveParser.Cell_arrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#cell_array}.
	 * @param ctx the parse tree
	 */
	void exitCell_array(OctaveParser.Cell_arrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#optional_row_list}.
	 * @param ctx the parse tree
	 */
	void enterOptional_row_list(OctaveParser.Optional_row_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#optional_row_list}.
	 * @param ctx the parse tree
	 */
	void exitOptional_row_list(OctaveParser.Optional_row_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#row_list}.
	 * @param ctx the parse tree
	 */
	void enterRow_list(OctaveParser.Row_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#row_list}.
	 * @param ctx the parse tree
	 */
	void exitRow_list(OctaveParser.Row_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#row}.
	 * @param ctx the parse tree
	 */
	void enterRow(OctaveParser.RowContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#row}.
	 * @param ctx the parse tree
	 */
	void exitRow(OctaveParser.RowContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#row_separator_list}.
	 * @param ctx the parse tree
	 */
	void enterRow_separator_list(OctaveParser.Row_separator_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#row_separator_list}.
	 * @param ctx the parse tree
	 */
	void exitRow_separator_list(OctaveParser.Row_separator_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#quiet_row_separator_list}.
	 * @param ctx the parse tree
	 */
	void enterQuiet_row_separator_list(OctaveParser.Quiet_row_separator_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#quiet_row_separator_list}.
	 * @param ctx the parse tree
	 */
	void exitQuiet_row_separator_list(OctaveParser.Quiet_row_separator_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#row_separator}.
	 * @param ctx the parse tree
	 */
	void enterRow_separator(OctaveParser.Row_separatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#row_separator}.
	 * @param ctx the parse tree
	 */
	void exitRow_separator(OctaveParser.Row_separatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#quiet_row_separator}.
	 * @param ctx the parse tree
	 */
	void enterQuiet_row_separator(OctaveParser.Quiet_row_separatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#quiet_row_separator}.
	 * @param ctx the parse tree
	 */
	void exitQuiet_row_separator(OctaveParser.Quiet_row_separatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#element_list}.
	 * @param ctx the parse tree
	 */
	void enterElement_list(OctaveParser.Element_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#element_list}.
	 * @param ctx the parse tree
	 */
	void exitElement_list(OctaveParser.Element_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(OctaveParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(OctaveParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#expr_or_tilde}.
	 * @param ctx the parse tree
	 */
	void enterExpr_or_tilde(OctaveParser.Expr_or_tildeContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#expr_or_tilde}.
	 * @param ctx the parse tree
	 */
	void exitExpr_or_tilde(OctaveParser.Expr_or_tildeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ECHO}
	 * labeled alternative in {@link OctaveParser#element_separator_list}.
	 * @param ctx the parse tree
	 */
	void enterECHO(OctaveParser.ECHOContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ECHO}
	 * labeled alternative in {@link OctaveParser#element_separator_list}.
	 * @param ctx the parse tree
	 */
	void exitECHO(OctaveParser.ECHOContext ctx);
	/**
	 * Enter a parse tree produced by the {@code COMMA_INSERT2}
	 * labeled alternative in {@link OctaveParser#element_separator_list}.
	 * @param ctx the parse tree
	 */
	void enterCOMMA_INSERT2(OctaveParser.COMMA_INSERT2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code COMMA_INSERT2}
	 * labeled alternative in {@link OctaveParser#element_separator_list}.
	 * @param ctx the parse tree
	 */
	void exitCOMMA_INSERT2(OctaveParser.COMMA_INSERT2Context ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#quiet_element_separator_list}.
	 * @param ctx the parse tree
	 */
	void enterQuiet_element_separator_list(OctaveParser.Quiet_element_separator_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#quiet_element_separator_list}.
	 * @param ctx the parse tree
	 */
	void exitQuiet_element_separator_list(OctaveParser.Quiet_element_separator_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#quiet_element_separator_comma}.
	 * @param ctx the parse tree
	 */
	void enterQuiet_element_separator_comma(OctaveParser.Quiet_element_separator_commaContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#quiet_element_separator_comma}.
	 * @param ctx the parse tree
	 */
	void exitQuiet_element_separator_comma(OctaveParser.Quiet_element_separator_commaContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(OctaveParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(OctaveParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#name_or_tilde}.
	 * @param ctx the parse tree
	 */
	void enterName_or_tilde(OctaveParser.Name_or_tildeContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#name_or_tilde}.
	 * @param ctx the parse tree
	 */
	void exitName_or_tilde(OctaveParser.Name_or_tildeContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#t_EVENTS}.
	 * @param ctx the parse tree
	 */
	void enterT_EVENTS(OctaveParser.T_EVENTSContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#t_EVENTS}.
	 * @param ctx the parse tree
	 */
	void exitT_EVENTS(OctaveParser.T_EVENTSContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#t_METHODS}.
	 * @param ctx the parse tree
	 */
	void enterT_METHODS(OctaveParser.T_METHODSContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#t_METHODS}.
	 * @param ctx the parse tree
	 */
	void exitT_METHODS(OctaveParser.T_METHODSContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#t_PROPERTIES}.
	 * @param ctx the parse tree
	 */
	void enterT_PROPERTIES(OctaveParser.T_PROPERTIESContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#t_PROPERTIES}.
	 * @param ctx the parse tree
	 */
	void exitT_PROPERTIES(OctaveParser.T_PROPERTIESContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#t_NOT}.
	 * @param ctx the parse tree
	 */
	void enterT_NOT(OctaveParser.T_NOTContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#t_NOT}.
	 * @param ctx the parse tree
	 */
	void exitT_NOT(OctaveParser.T_NOTContext ctx);
	/**
	 * Enter a parse tree produced by {@link OctaveParser#t_END}.
	 * @param ctx the parse tree
	 */
	void enterT_END(OctaveParser.T_ENDContext ctx);
	/**
	 * Exit a parse tree produced by {@link OctaveParser#t_END}.
	 * @param ctx the parse tree
	 */
	void exitT_END(OctaveParser.T_ENDContext ctx);
}