package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/14/20.
 */
public class Null extends AbstractExpr{


	/**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un null
     */
    @Override
    public void decompile(IndentPrintStream s) {
            s.print("null");
    }

    /**
     * Fixe le type à null
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
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type t = new NullType(compiler.sym.create("null"));
        this.setType(t);
        return this.getType();
    }

    @Override
    String prettyPrintNode() {
        return "Null";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    /**
     * fonction générant un élément null en assembleur
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int numreg = compiler.managerReg.getLastRegused();
        GPRegister reg = Register.getR(numreg);
        compiler.addInstruction(new LOAD(new NullOperand(),reg));
    }
}
