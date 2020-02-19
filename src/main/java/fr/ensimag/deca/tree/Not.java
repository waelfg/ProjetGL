package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    /**
     * Vérifie que l'opérande est bien un booléen. Fixe le type à boolean
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (t.isBoolean()){
            this.setType(t);
            return this.getType();
        }
        else{
            throw new ContextualError("L'opérateur Not doit être utilisé sur un booléen et pas sur un :"
                                        + t.getName(), this.getLocation());
        }
    }


    /**
     * fonction générante le code assembleur de la fonction not
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getOperand().codeGenInst(compiler);
        int op = compiler.managerReg.getLastRegused();

        compiler.addInstruction(new CMP(1, Register.getR(op)));
        compiler.addInstruction(new BNE(new Label("Faux")));

        compiler.addInstruction(new LOAD(0, Register.getR(op)));
        compiler.addInstruction(new BRA(new Label("Fin")));

        compiler.addLabel(new Label("Faux"));
        compiler.addInstruction(new LOAD(1, Register.getR(op)));
        compiler.addLabel(new Label("Fin"));

    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
}
