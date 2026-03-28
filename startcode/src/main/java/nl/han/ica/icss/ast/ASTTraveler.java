package nl.han.ica.icss.ast;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;

import java.util.HashMap;

public abstract class ASTTraveler<T> {
    protected IHANLinkedList<HashMap<String, T>> scopeStack;

    public ASTTraveler(){
        scopeStack = new HANLinkedList<>();
    }

    public void travel(AST ast){
        scopeStack = new HANLinkedList<>();
        scopeStack.addFirst(new HashMap<>());
        if (ast != null) visitNode(ast.root);
    }

    protected abstract void handleNode(ASTNode node);
    protected abstract void handleAfterScope(ASTNode node);

    protected void visitNode(ASTNode node) {
        if (node == null) return;

        boolean newScope = requiresNewScope(node);
        if (newScope) enterScope();

        handleNode(node);
        visitChildren(node);

        if (newScope) {
            handleAfterScope(node);
            exitScope();
        }

    }

    private boolean requiresNewScope(ASTNode node) {
        return node instanceof Stylerule || node instanceof Stylesheet || node instanceof IfClause || node instanceof ElseClause;
    }

    private void enterScope() {
        scopeStack.addFirst(new HashMap<>());
    }

    private void exitScope() {
        if (scopeStack != null && scopeStack.getSize() > 1) {
            scopeStack.removeFirst();
        }
    }

    protected void visitChildren(ASTNode node) {
        if (node == null) return;
        for (ASTNode child : node.getChildren()) visitNode(child);
    }

    protected T getVar(String varName) {
        if (varName == null) return null;
        for (int i = 0; i < scopeStack.getSize(); i++) {
            HashMap<String, T> scope = scopeStack.get(i);
            if (scope.containsKey(varName)) return scope.get(varName);
        }
        return null;
    }

    protected void createVarInCurrentScope(String name, T value) {
        if (name == null) return;
        scopeStack.getFirst().put(name, value);
    }
}
