package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.DivideOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker extends ASTTraveler<ExpressionType> {

    public void check(AST ast) {
        travel(ast);
    }

    @Override
    protected void handleNode(ASTNode node) {
        if (node == null) return;

        if (node instanceof VariableAssignment) {
            createVariableAssignment((VariableAssignment) node);
        } else if (node instanceof VariableReference) {                                    /// Controleert verdere plekken waar expressies gebruikt worden
            checkVariableIsDefined((VariableReference) node);
        } else if (node instanceof Declaration) {
            checkAllowedDeclaration((Declaration) node);
        } else if (node instanceof Expression) {
            checkExpressionType((Expression) node);
        }
    }

    @Override
    protected void handleAfterScope(ASTNode node) {
        if (node == null) return;
        if (node instanceof IfClause) checkIfBoolUsage((IfClause) node);
    }

    private void createVariableAssignment(VariableAssignment node) {
        VariableReference varRef = node.name;
        ExpressionType type = checkExpressionType(node.expression);
        createVarInCurrentScope(varRef.name, type);
    }

    /// CH01: Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn
    private void checkVariableIsDefined(VariableReference variableReference) {
        String name = variableReference.name;
        ExpressionType found = getVar(variableReference.name);
        if (found == null) variableReference.setError("Undefined variable: " + name);
    }

    /// CH02, CH03 + meer: Recursive controle op alle mogelijke expression types
    public ExpressionType checkExpressionType(Expression expr) {
        if (expr == null) return ExpressionType.UNDEFINED;

        /// Literals: directe waardes
        if (expr instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expr instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expr instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expr instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expr instanceof ScalarLiteral) return ExpressionType.SCALAR;

        if (expr instanceof VariableReference) {
            String refName = ((VariableReference) expr).name;
            ExpressionType refType = lookupVariableType(refName);
            if (refType == ExpressionType.UNDEFINED) {
                expr.setError("Undefined variable: " + refName);
            }
            return refType;
        }

        /// Operations: Waardes worden recursief uit de expressions gehaald.
        if (expr instanceof AddOperation || expr instanceof SubtractOperation
                || expr instanceof MultiplyOperation || expr instanceof DivideOperation) {

            Expression lhs = (Expression) expr.getChildren().get(0);
            Expression rhs = (Expression) expr.getChildren().get(1);

            ExpressionType lhsType = checkExpressionType(lhs);
            ExpressionType rhsType = checkExpressionType(rhs);

            /// Geeft mogelijk 'UNDEFINED' mee naar niveau hier boven
            if (lhsType == ExpressionType.UNDEFINED || rhsType == ExpressionType.UNDEFINED) {
                return ExpressionType.UNDEFINED;
            }

            if (isNonNumeric(lhsType) || isNonNumeric(rhsType)) {
                expr.setError("Invalid expression type for mathematics: " + lhsType + " and/or " + rhsType);
                return ExpressionType.UNDEFINED;
            }

            /// '+/-': type waardes moeten gelijk zijn.
            if (expr instanceof AddOperation || expr instanceof SubtractOperation) {
                if (lhsType != rhsType) {
                    expr.setError("Expressions of + and - must have the same type (" + lhsType + " +/- " + rhsType + ")");
                    return ExpressionType.UNDEFINED;
                }
                return lhsType;
            }

            /// '*': vereist minimaal 1 SCALA type
            if (expr instanceof MultiplyOperation) {
                if (isUnitType(lhsType) && isUnitType(rhsType)) {
                    expr.setError("Cannot multiply two unit types (" + lhsType + " * " + rhsType + ")");
                    return ExpressionType.UNDEFINED;
                }
                if (lhsType == ExpressionType.SCALAR) return rhsType;
                if (rhsType == ExpressionType.SCALAR) return lhsType;
                expr.setError("Multiplication requires at least one scalar (found: " + lhsType + " * " + rhsType + ")");
                return ExpressionType.UNDEFINED;
            }

            /// '/': rhs moet een SCALAR zijn
            if (expr instanceof DivideOperation) {
                if (rhsType != ExpressionType.SCALAR) {
                    expr.setError("Division requires rhs scalar (found divisor type: " + rhsType + ")");
                    return ExpressionType.UNDEFINED;
                }
                if (lhsType == ExpressionType.SCALAR) return ExpressionType.SCALAR;
                if (isUnitType(lhsType)) return lhsType;
                expr.setError("Invalid dividend type for division: " + lhsType);
                return ExpressionType.UNDEFINED;
            }
        }

        return ExpressionType.UNDEFINED;
    }

    /// CH04: Controleer of bij declaraties het type van de value klopt met de property
    private void checkAllowedDeclaration(Declaration declaration) {
        String propertyName = declaration.property.name;
        ExpressionType expressionType = checkExpressionType(declaration.expression);
        boolean mismatch = false;
        if (isColorProperty(propertyName) && !(expressionType == ExpressionType.COLOR)) mismatch = true;
        if (isScalarProperty(propertyName) && !(expressionType == ExpressionType.PIXEL)) mismatch = true;
        if (mismatch) declaration.setError("Mismatch of expression type: " + expressionType + " for the property: " + propertyName);
    }

    /// CH05: Controleer of de conditie bij een if-statement van het type boolean is
    private void checkIfBoolUsage(IfClause if_clause) {
        if (if_clause == null) return;
        ExpressionType expressionType = checkExpressionType(if_clause.conditionalExpression);
        if (expressionType != ExpressionType.BOOL) if_clause.setError("If clause requires a boolean expression (Was given " + expressionType + ")");
    }

    /// CH05 & Helper function: haalt expression type op uit variable types top-down tot het huidige scope level
    private ExpressionType lookupVariableType(String name) {
        ExpressionType expressionType = getVar(name);
        return expressionType == null ? ExpressionType.UNDEFINED : expressionType;
    }

    /// Helper function: controleert of property een SCALAR waarde verwacht
    private boolean isScalarProperty(String propertyName) {
        return propertyName.equals("width") || propertyName.equals("height");
    }

    /// Helper function: controleert of property een COLOR waarde verwacht
    private boolean isColorProperty(String propertyName) {
        return propertyName.equals("color") || propertyName.equals("background-color");
    }

    /// Helper function: controleert op unit types
    private boolean isUnitType(ExpressionType t) {
        return t == ExpressionType.PIXEL || t == ExpressionType.PERCENTAGE;
    }

    /// Helper function: controleert op niet wiskundige types
    private boolean isNonNumeric(ExpressionType t) {
        return t == ExpressionType.COLOR || t == ExpressionType.BOOL || t == ExpressionType.UNDEFINED;
    }


}

