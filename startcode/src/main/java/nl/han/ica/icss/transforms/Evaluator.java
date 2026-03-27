package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;

public class Evaluator extends ASTTraveler<Literal> implements Transform {

    @Override
    public void apply(AST ast) {
        travel(ast);
    }

    @Override
    protected void handleNode(ASTNode node) {
        if (node == null) return;

        
    }

    @Override
    protected void handleAfterScope(ASTNode node) {

    }



    
}
