package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.locks.Condition;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variableTypes.addFirst(new HashMap<>());
        visitNode(ast.root);
    }

    // Pre-order traversal
    private void visitNode(ASTNode node) {
        if (node == null) return;

        if (node instanceof Stylerule || node instanceof Stylesheet
                || node instanceof IfClause || node instanceof ElseClause) {          /// Alle mogelijke plekken waar een nieuwe scope nodig is
            visitChildrenAndAddScope(node);
            return;
        }

        if (node instanceof VariableAssignment) {
            createVariableAssignment((VariableAssignment) node);
        } else if (node instanceof Expression) {                                    /// Controleert verdere plekken waar expressies gebruikt worden
            checkVariableIsDefined((VariableReference) node);
        } else if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            checkVariableIsDefined((VariableReference) declaration.expression);
        }

        visitChildren(node);
    }

    private void createVariableAssignment(VariableAssignment node) {
        VariableReference varRef = node.name;
        ExpressionType type = ExpressionType.UNDEFINED;

        Expression expression = node.expression;

        if (!expression.getChildren().isEmpty()) {
            ASTNode childNode = expression.getChildren().get(0);

            if (childNode instanceof BoolLiteral) {
                type = ExpressionType.BOOL;
            } else if (childNode instanceof PixelLiteral) {
                type = ExpressionType.PIXEL;
            } else if (childNode instanceof PercentageLiteral) {
                type = ExpressionType.PERCENTAGE;
            } else if (childNode instanceof ColorLiteral) {
                type = ExpressionType.COLOR;
            } else if (childNode instanceof ScalarLiteral) {
                type = ExpressionType.SCALAR;
            } else if (childNode instanceof VariableReference) {
                String refName = ((VariableReference) childNode).name;
                ExpressionType refType = ExpressionType.UNDEFINED;
                for (int i = 0; i < variableTypes.getSize(); i++) {
                    HashMap<String, ExpressionType> scope = variableTypes.get(i);
                    if (scope.containsKey(refName)) {
                        refType = scope.get(refName);
                        break;
                    }
                }
                type = refType;
            }

        }
        variableTypes.getFirst().put(varRef.name, type);
    }



    /// CH01: Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn
    private void checkVariableIsDefined(VariableReference variableReference) {
        String name = variableReference.name;
        boolean found = false;
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(name)) {
                found = true;
                break;
            }
        }
        if (!found) variableReference.setError("Undefined variable: " + name);
    }

    /// CH02: Controleer of de operanden van de operaties plus en min van gelijk type zijn
    private void checkExpressionTypeIsAllowed(Expression expression) {
        if (expression.getChildren().size() == 3) {
            Expression lhs = (Expression) expression.getChildren().get(0);
            Expression rhs = (Expression) expression.getChildren().get(2);

            if ()

        }

    }


    /// Helper function; recursive call over alle mogelijke node children.
    private void visitChildren(ASTNode node) {
        if (node == null) return;
        for (ASTNode child : node.getChildren()) visitNode(child);
    }

    /// Helper function; maakt nieuwe scope aan voor de recursive call op visitChildren()
    public void visitChildrenAndAddScope(ASTNode node) {
        if (node == null) return;
        variableTypes.addFirst(new HashMap<>());
        visitChildren(node);
        variableTypes.removeFirst();
    }


}
