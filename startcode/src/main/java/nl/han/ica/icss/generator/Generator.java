package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator extends ASTTraveler<Object> {

	private StringBuilder sb;
	private int ruleDepth = 0;

	public String generate(AST ast) {
		sb = new StringBuilder();
		if (ast == null || ast.root == null) return "";
		travel(ast);
		return sb.toString();
	}

	@Override
	protected void handleNode(ASTNode node) {
		if (node == null) return;

		if (node instanceof Stylerule) {
			Stylerule sr = (Stylerule) node;
			indent(ruleDepth);
			for (int i = 0; i < sr.selectors.size(); i++) {
				Selector s = sr.selectors.get(i);
				sb.append(s.toString());
				if (i < sr.selectors.size() - 1) sb.append(", ");
			}
			sb.append(" {");
			sb.append(System.lineSeparator());
			ruleDepth++;
			return;
		}

		if (node instanceof Declaration) {
			Declaration d = (Declaration) node;
			indent(ruleDepth);
			sb.append(d.property.name);
			sb.append(": ");
			sb.append(expressionToString(d.expression));
			sb.append(";");
			sb.append(System.lineSeparator());
		}
	}

	@Override
	protected void handleAfterScope(ASTNode node) {
		if (node instanceof Stylerule) {
			ruleDepth--;
			indent(ruleDepth);
			sb.append("}");
			sb.append(System.lineSeparator());
		}
	}

	private void indent(int level) {
		for (int i = 0; i < level; i++) sb.append("  ");
	}

	private String expressionToString(Expression expr) {
		if (expr == null) return "";
		if (expr instanceof PixelLiteral) {
			return ((PixelLiteral) expr).value + "px";
		}
		if (expr instanceof PercentageLiteral) {
			return ((PercentageLiteral) expr).value + "%";
		}
		if (expr instanceof ColorLiteral) {
			return ((ColorLiteral) expr).value;
		}
		if (expr instanceof ScalarLiteral) {
			return Integer.toString(((ScalarLiteral) expr).value);
		}
		if (expr instanceof BoolLiteral) {
			return ((BoolLiteral) expr).value ? "TRUE" : "FALSE";
		}
		return expr.toString();
	}

}
