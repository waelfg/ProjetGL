package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl49
 * @date 01/01/2020
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((!t.isInt()) && (!t.isFloat())){
            throw new ContextualError("Le terme à rendre négatif doit être un nombre et non un :"
                                        + t.getName(), this.getLocation());
        }
        setType(t);
        return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getOperand().codeGenInst(compiler);
        int left = compiler.managerReg.getLastRegused();
        if (getOperand().getType().isInt()){
            compiler.addInstruction(new SUB(new ImmediateInteger(0),Register.getR(left)));
        }
        else if (getOperand().getType().isFloat()){
            compiler.addInstruction(new SUB(new ImmediateFloat(0),Register.getR(left)));
        }


        compiler.managerReg.setLastRegused(left);
    }

}
