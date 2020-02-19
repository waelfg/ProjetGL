package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RINT;

import java.io.PrintStream;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class ReadInt extends AbstractReadExpr {

    /**
     * Fixe le type à int
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
        Type t = new IntType(compiler.sym.create("int"));
        this.setType(t);
        return this.getType();
    }

    
    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un readint
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readInt()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    /**
     * fonction générante le code assembleur pour la lecture d'un entier
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new RINT());
        int ind = compiler.managerReg.affectReg(compiler);
        compiler.addInstruction(new LOAD(Register.R1,Register.getR(ind)));
    }
}
