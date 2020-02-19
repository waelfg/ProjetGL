package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }


    /**
     * Codegen pour l'opération booléenne And
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeOpbool(compiler);
    }


}
