// Generated from src\main\antlr4\nl\han\ica\icss\parser\ICSS.g4 by ANTLR 4.8
package nl.han.ica.icss.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ICSSParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ICSSVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ICSSParser#stylesheet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStylesheet(ICSSParser.StylesheetContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#style_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyle_rule(ICSSParser.Style_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#identity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentity(ICSSParser.IdentityContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(ICSSParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#style_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyle_line(ICSSParser.Style_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#prop_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProp_name(ICSSParser.Prop_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#prop_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProp_value(ICSSParser.Prop_valueContext ctx);
}