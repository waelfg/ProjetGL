package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeOpCmp(compiler);
    }
    
}
