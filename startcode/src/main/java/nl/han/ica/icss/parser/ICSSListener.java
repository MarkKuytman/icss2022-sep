// Generated from src\main\antlr4\nl\han\ica\icss\parser\ICSS.g4 by ANTLR 4.8
package nl.han.ica.icss.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ICSSParser}.
 */
public interface ICSSListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ICSSParser#stylesheet}.
	 * @param ctx the parse tree
	 */
	void enterStylesheet(ICSSParser.StylesheetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#stylesheet}.
	 * @param ctx the parse tree
	 */
	void exitStylesheet(ICSSParser.StylesheetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#style_rule}.
	 * @param ctx the parse tree
	 */
	void enterStyle_rule(ICSSParser.Style_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#style_rule}.
	 * @param ctx the parse tree
	 */
	void exitStyle_rule(ICSSParser.Style_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#identity}.
	 * @param ctx the parse tree
	 */
	void enterIdentity(ICSSParser.IdentityContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#identity}.
	 * @param ctx the parse tree
	 */
	void exitIdentity(ICSSParser.IdentityContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(ICSSParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(ICSSParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#style_line}.
	 * @param ctx the parse tree
	 */
	void enterStyle_line(ICSSParser.Style_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#style_line}.
	 * @param ctx the parse tree
	 */
	void exitStyle_line(ICSSParser.Style_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#prop_name}.
	 * @param ctx the parse tree
	 */
	void enterProp_name(ICSSParser.Prop_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#prop_name}.
	 * @param ctx the parse tree
	 */
	void exitProp_name(ICSSParser.Prop_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ICSSParser#prop_value}.
	 * @param ctx the parse tree
	 */
	void enterProp_value(ICSSParser.Prop_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ICSSParser#prop_value}.
	 * @param ctx the parse tree
	 */
	void exitProp_value(ICSSParser.Prop_valueContext ctx);
}