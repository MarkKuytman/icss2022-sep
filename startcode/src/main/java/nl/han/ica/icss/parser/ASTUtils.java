package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.ASTNode;

public class ASTUtils {
    public static void addChildToParent (IHANStack<ASTNode> stack) {
        ASTNode node = stack.pop();
        if (node == null) return;
        ASTNode parent = stack.peek();
        if (parent == null) return;
        parent.addChild(node);
    }
    public static <T extends ASTNode> void addChildToParent (IHANStack<ASTNode> stack, Class<T> childType) {
        ASTNode node = stack.pop();
        stack.peek().addChild(childType.cast(node));
    }
}

