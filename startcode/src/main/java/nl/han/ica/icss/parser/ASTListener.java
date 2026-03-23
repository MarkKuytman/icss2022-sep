package nl.han.ica.icss.parser;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.DivideOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
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
	private IHANStack<Expression> expressions;


	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
		expressions = new HANStack<>();
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
	public void enterAssignment(ICSSParser.AssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}

	@Override
	public void exitAssignment(ICSSParser.AssignmentContext ctx) {
		ASTUtils.addChildToParent(currentContainer);
	}

	@Override
	public void exitVar_name(ICSSParser.Var_nameContext ctx) {
		if (ctx.getParent() instanceof ICSSParser.ExpressionContext) {
			expressions.push(new VariableReference(ctx.getText()));
		} else {
			ASTNode node = currentContainer.peek();
			node.addChild(new VariableReference(ctx.getText()));
		}
	}

	@Override
	public void exitVar_value(ICSSParser.Var_valueContext ctx) {
		ASTNode node = currentContainer.peek();
		if (ctx.COLOR() != null) {
			node.addChild(new ColorLiteral(ctx.getText()));
		} else if (ctx.PIXELSIZE() != null) {
			node.addChild(new PixelLiteral(ctx.getText()));
		} else if (ctx.TRUE() != null || ctx.FALSE() != null) {
			node.addChild(new BoolLiteral(ctx.getText()));
		}
	}

	@Override
	public void enterStyle_rule(ICSSParser.Style_ruleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStyle_rule(ICSSParser.Style_ruleContext ctx) {
		ASTUtils.addChildToParent(currentContainer);
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
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTUtils.addChildToParent(currentContainer);
	}

	@Override
	public void exitProp_name(ICSSParser.Prop_nameContext ctx) {
		ASTNode node = currentContainer.peek();
		node.addChild(new PropertyName(ctx.getChild(0).getText()));
	}

	@Override
	public void exitProp_value(ICSSParser.Prop_valueContext ctx) {
		ASTNode node = currentContainer.peek();
		if (ctx.PIXELSIZE() != null) {
			node.addChild(new PixelLiteral(ctx.getChild(0).getText()));
		} else if (ctx.COLOR() != null) {
			node.addChild(new ColorLiteral(ctx.getChild(0).getText()));
		} else if (ctx.expression() != null) {
			node.addChild(expressions.pop());
		}
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		Expression exp;

		if (ctx.getChildCount() == 1) {
			if (ctx.SCALAR() != null) {
				exp = new ScalarLiteral(ctx.getText());
			} else if (ctx.PIXELSIZE() != null) {
				exp = new PixelLiteral(ctx.getText());
			} else {
				exp = new VariableReference(ctx.getText());
			}
			expressions.push(exp);
			return;
		}

		char operator = ctx.getChild(1).getText().charAt(0);
		switch (operator) {
			case '*':
				exp = new MultiplyOperation();
				break;
			case '+':
				exp = new AddOperation();
				break;
			case '/':
				exp = new DivideOperation();
				break;
			default:
				exp = new SubtractOperation();
				break;
		}
		Expression exp_right = expressions.pop();
		Expression exp_left = expressions.pop();
		exp.addChild(exp_left);
		exp.addChild(exp_right);
		expressions.push(exp);
	}

	@Override
	public void enterIf_clause(ICSSParser.If_clauseContext ctx) {
		currentContainer.push(new IfClause());
	}

	@Override
	public void exitIf_clause(ICSSParser.If_clauseContext ctx) {
		ASTUtils.addChildToParent(currentContainer);
	}

	@Override
	public void enterElse_clause(ICSSParser.Else_clauseContext ctx) {
		currentContainer.push(new ElseClause());
	}

	@Override
	public void exitElse_clause(ICSSParser.Else_clauseContext ctx) {
		ASTNode node = currentContainer.pop();
		ASTNode parent = currentContainer.peek();

		if (!parent.getChildren().isEmpty()) {
			ASTNode lastChild = parent.getChildren().get(
									parent.getChildren().size() - 1);

			if (lastChild instanceof IfClause) {
				lastChild.addChild(node);
			}
		}

		// Uhh exception maybe?
	}
}