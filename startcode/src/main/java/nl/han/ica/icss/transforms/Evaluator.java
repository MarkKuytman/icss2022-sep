package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;

import java.util.ArrayList;

public class Evaluator extends ASTTraveler<Literal> implements Transform {

    @Override
    public void apply(AST ast) {
        travel(ast);
    }

    @Override
    protected void handleNode(ASTNode node) {
        if (node == null) return;

        if (node instanceof VariableAssignment) {
            VariableAssignment va = (VariableAssignment) node;
            Literal value = squishExpression(((VariableAssignment) node).expression);
            if (value != null) {
                createVarInCurrentScope(va.name.name, value);
                va.expression = value;
            }
        } else if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            Literal value = squishExpression(declaration.expression);
            if (value != null) declaration.expression = value;
        }
    }

    /// Evalueert een expressie, waarbij variabelen worden vervangen door hun waarde, en operaties worden uitgevoerd indien mogelijk.
    /// Deze methode is recursief, en zal blijven evalueren totdat er geen expressies meer zijn die kunnen worden geëvalueerd.
    private Literal squishExpression(Expression expression) {
        if (expression == null) return null;

        if (expression instanceof PixelLiteral) return (PixelLiteral) expression;
        if (expression instanceof PercentageLiteral) return (PercentageLiteral) expression;
        if (expression instanceof ColorLiteral) return (ColorLiteral) expression;
        if (expression instanceof BoolLiteral) return (BoolLiteral) expression;
        if (expression instanceof ScalarLiteral) return (ScalarLiteral) expression;

        if (expression instanceof VariableReference) {
            String name = ((VariableReference) expression).name;
            return getVar(name);
        }

        if (expression instanceof AddOperation || expression instanceof SubtractOperation
                || expression instanceof MultiplyOperation || expression instanceof DivideOperation) {

            Expression lhsE = (Expression) expression.getChildren().get(0);
            Expression rhsE = (Expression) expression.getChildren().get(1);

            Literal lhs = squishExpression(lhsE);
            Literal rhs = squishExpression(rhsE);

            if (expression instanceof AddOperation || expression instanceof SubtractOperation) {
                boolean add = expression instanceof AddOperation;
                if (lhs instanceof PixelLiteral) {
                    int a = ((PixelLiteral) lhs).value;
                    int b = ((PixelLiteral) rhs).value;
                    return new PixelLiteral(add ? a + b : a - b);
                }
                if (lhs instanceof PercentageLiteral) {
                    int a = ((PercentageLiteral) lhs).value;
                    int b = ((PercentageLiteral) rhs).value;
                    return new PercentageLiteral(add ? a + b : a - b);
                }
                if (lhs instanceof ScalarLiteral) {
                    double a = ((ScalarLiteral) lhs).value;
                    double b = ((ScalarLiteral) rhs).value;
                    return new ScalarLiteral((int) (add ? a + b : a - b));
                }
                return null;
            }

            if (expression instanceof MultiplyOperation) {
                if (lhs instanceof ScalarLiteral && rhs instanceof ScalarLiteral) {
                    return new ScalarLiteral(((ScalarLiteral) lhs).value * ((ScalarLiteral) rhs).value);
                }
                if (lhs instanceof ScalarLiteral && rhs instanceof PixelLiteral) {
                    return new PixelLiteral(((ScalarLiteral) lhs).value * ((PixelLiteral) rhs).value);
                }
                if (rhs instanceof ScalarLiteral && lhs instanceof PixelLiteral) {
                    return new PixelLiteral(((ScalarLiteral) rhs).value * ((PixelLiteral) lhs).value);
                }
                if (lhs instanceof ScalarLiteral && rhs instanceof PercentageLiteral) {
                    return new PercentageLiteral(((ScalarLiteral) lhs).value * ((PercentageLiteral) rhs).value);
                }
                if (rhs instanceof ScalarLiteral && lhs instanceof PercentageLiteral) {
                    return new PercentageLiteral(((ScalarLiteral) rhs).value * ((PercentageLiteral) lhs).value);
                }
                return null;
            }

            if (expression instanceof DivideOperation) {
                double divisor = ((ScalarLiteral) rhs).value;
                if (lhs instanceof ScalarLiteral) {
                    return new ScalarLiteral((int) (((ScalarLiteral) lhs).value / divisor));
                }
                if (lhs instanceof PixelLiteral) {
                    return new PixelLiteral((int) (((PixelLiteral) lhs).value / divisor));
                }
                if (lhs instanceof PercentageLiteral) {
                    return new PercentageLiteral((int) (((PercentageLiteral) lhs).value / divisor));
                }
                return null;
            }
        }
        return null;
    }

    /// Evalueert if-else clauses, waarbij de conditionele expressie wordt geëvalueerd, en afhankelijk van de uitkomst, de body van de if-clause of de else-clause wordt toegevoegd aan de parent body.
    private void squishIfClausesInChildren(ArrayList<ASTNode> list) {
        for (int i = 0; i < list.size(); ) {
            ASTNode child = list.get(i);

            /// base 'for' loop clause
            if (!(child instanceof IfClause)) {
                i++;
                continue;
            }

            IfClause ic = (IfClause) child;
            Literal condLit = squishExpression(ic.conditionalExpression);
            boolean condTrue = (condLit instanceof BoolLiteral) && ((BoolLiteral) condLit).value;

            if (condTrue) {
                list.remove(i);
                if (ic.body != null) {
                    squishBodyValues(list, ic.body);
                    return;
                }
            } else {
                if (ic.elseClause != null && ic.elseClause.body != null) {
                    list.remove(i);
                    squishBodyValues(list, ic.elseClause.body);
                    i += ic.elseClause.body.size();
                } else {
                    list.remove(i);
                }
            }
        }
    }

    /// Controleert op dubbele declaraties in de parent body, en verwijdert deze, voordat de child body wordt toegevoegd aan de parent body.
    private void squishBodyValues(ArrayList<ASTNode> parentBody, ArrayList<ASTNode> childBody) {
        for (ASTNode child : childBody) {
            if (!(child instanceof Declaration)) continue;
            Declaration childDecl = (Declaration) child;

            for (int i = 0; i < parentBody.size() - 1; i++) {
                ASTNode parent = parentBody.get(i);
                if (parent instanceof Declaration) {
                    Declaration parDecl = (Declaration) parent;
                    if (parDecl.property.equals(childDecl.property)) {
                        parentBody.remove(i);
                    }
                }
            }

            parentBody.add(child);
        }
    }

    @Override
    protected void handleAfterScope(ASTNode node) {
        if (node instanceof Stylesheet) {
            squishIfClausesInChildren(((Stylesheet) node).body);
            return;
        }
        if (node instanceof Stylerule) {
            squishIfClausesInChildren(((Stylerule) node).body);
            return;
        }
        if (node instanceof ElseClause) {
            squishIfClausesInChildren(((ElseClause) node).body);
            return;
        }
        if (node instanceof IfClause) {
            squishIfClausesInChildren(((IfClause) node).body);
        }
    }
}
