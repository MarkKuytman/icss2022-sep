package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		currentContainer.push(ast.root);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		if (currentContainer.peek() == ast.root) currentContainer.pop();
	}

	@Override
	public void enterStyle_rule(ICSSParser.Style_ruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStyle_rule(ICSSParser.Style_ruleContext ctx) {
		ASTNode node = currentContainer.pop();
		currentContainer.peek().addChild(node);
	}

	@Override
	public void exitIdentity(ICSSParser.IdentityContext ctx) {
		ASTNode node = currentContainer.peek();
		String name = ctx.getChild(0).getText();
		Selector sel;
		if (ctx.ID_IDENT() != null) {
			sel = new IdSelector(name);
		} else if (ctx.CLASS_IDENT() != null) {
			sel = new ClassSelector(name);
		} else {
			sel = new TagSelector(name);
		}
		node.addChild(sel);
	}

	@Override
	public void enterStyle_line(ICSSParser.Style_lineContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitStyle_line(ICSSParser.Style_lineContext ctx) {
		ASTNode node = currentContainer.pop();
		currentContainer.peek().addChild(node);
	}

	@Override
	public void exitProp_name(ICSSParser.Prop_nameContext ctx) {
		Declaration declaration = (Declaration) currentContainer.peek();
		declaration.addChild(new PropertyName(ctx.getChild(0).getText()));
	}

	@Override
	public void exitProp_value(ICSSParser.Prop_valueContext ctx) {
		Declaration declaration = (Declaration) currentContainer.peek();
		if (ctx.PIXELSIZE() != null) {
			declaration.addChild(new PixelLiteral(ctx.getChild(0).getText()));
		} else if (ctx.COLOR() != null) {
			declaration.addChild(new ColorLiteral(ctx.getChild(0).getText()));
		}
	}
}