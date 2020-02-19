package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import fr.ensimag.deca.DecacCompiler;

/**
 * @author gl49
 * @date 01/01/2020
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    public static int i = 1 ;

    @Override
    protected String getOperatorName() {
        return "-";
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        super.codeExpInt(compiler);
        i++ ;

    }
    
}
