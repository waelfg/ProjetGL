package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import java.util.Iterator;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            if (arguments.size() == 0){
                throw new ContextualError("Print ne peut pas prendre de liste vide en argument", this.getLocation());
            }
            Iterator<AbstractExpr> it = arguments.iterator();
            while (it.hasNext()){
                AbstractExpr expr = it.next();
                Type typ = expr.verifyExpr(compiler, localEnv, currentClass);
                if (!typ.isNull() && (!(typ.isFloat() || typ.isInt() || typ.isString()))){
                    throw new ContextualError("Print ne prend en arguments que des int, float ou string", getLocation());
                }

            }
    }


    /**
     * Codegen instructions for print
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            a.codeGenPrint(compiler);
        }
    }
    //hasNext
    private boolean getPrintHex() {
        return printHex;
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     * On construit les differentes instructions print
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print" + getSuffix());
        if (printHex){
            s.print("x");
        }
        s.print("(");
        Iterator<AbstractExpr> it = arguments.iterator();
        AbstractExpr expr = it.next();
        expr.decompile(s);
        while (it.hasNext()){
        	s.print(",");
            expr = it.next();
            expr.decompile(s);
        }
        s.print(')');
        s.print("; \n");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
