package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import java.io.PrintStream;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    /**
     * Fixe le type à boolean
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
        Type t = new BooleanType(compiler.sym.create("boolean"));
        this.setType(t);
        return this.getType();
    }


    
    /**
     * @param s
     * Decompile the tree, considering it as an instruction.
     * In most case, this simply calls decompile(), but it may add a semicolon if needed
     */

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }




    /**
     * fonction generant le code d'un booléen
     * @param compiler
     */

    protected void codeGenInst(DecacCompiler compiler) {
        int i = compiler.managerReg.affectReg(compiler);
        if (value) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.getR(i)));
        }
        else {
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(i)));
        }
        compiler.managerReg.setReg(i);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

}
