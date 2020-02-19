package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import java.io.PrintStream;
/**
 * Integer literal
 *
 * @author gl49
 * @date 01/01/2020
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

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
     * génère le code assembleur de l'affichage d'un entier
     * @param compiler
     */

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new ImmediateInteger(value), Register.getR(1)));
        compiler.addInstruction(new WINT());
    }


    /**
     * permet de coder en assembleur l'affectation d'un entier à un registre
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int i = compiler.managerReg.affectReg(compiler);

        compiler.addInstruction(new LOAD(new ImmediateInteger(value), Register.getR(i)));
        setDval(Register.getR(i));
    }
    
    

    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }


	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return new ImmediateInteger(value);
	}
    
    

}
