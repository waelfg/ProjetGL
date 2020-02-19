package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/23/20.
 */
public class This extends AbstractExpr{
	
	/**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un this
     */
    @Override
    public void decompile(IndentPrintStream s) {
    	s.print("this");
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Vérifie qu'on se trouve à l'intérieur d'une classe. Fixe le type à cette classe
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
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        if (currentClass == null){
            throw new ContextualError("this ne peut être appelé que dans une classe", this.getLocation());
        }
        Type t = currentClass.getType();
        this.setType(t);
        return t;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {}

    @Override
    protected void iterChildren(TreeFunction f) {}


    /**
     * fonction générant le code assembleur pour this
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DAddr addr = new RegisterOffset(-2, Register.LB);
        int numreg = compiler.managerReg.getLastRegused();
        compiler.addInstruction(new LOAD(addr,Register.getR(numreg)));
    }
}
