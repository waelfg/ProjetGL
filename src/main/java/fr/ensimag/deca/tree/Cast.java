/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

/**
 *
 * @author ollierv
 */
/*
public class Cast extends AbstractUnaryExpr{

    public Cast(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected String getOperatorName() {
        return ("cast");//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if (!getType().isFloat()){
            throw new ContextualError("Operation denied", getLocation());
        }
        if (!getOperand().getType().isInt() || !getOperand().getType().isFloat()){
            throw new ContextualError("Operation denied", getLocation());
        }
        return getType();

    }


    
}
*/
public class Cast extends AbstractExpr{

    AbstractExpr expr;
    AbstractIdentifier cast;

    public Cast(AbstractExpr expr, AbstractIdentifier cast) {
        this.expr = expr;
        this.cast = cast;
    }

    public AbstractExpr getExpr() {
        return expr;
    }

    public AbstractIdentifier getCast() {
        return cast;
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un cast
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(this.getCast().decompile());
        s.print(")");
        s.print("(");
        s.print(this.getExpr().decompile());
        s.print(")");
    }

    /**
     * Vérifie que le type de départ et d'arrivée permettent bien le cast.
     * Change le type si c'est le cas.
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
        Type ancien = expr.verifyExpr(compiler, localEnv, currentClass);
        Type nouveau = cast.verifyExpr(compiler, localEnv, currentClass);
        if(!(ancien.isInt() && nouveau.isFloat()) || !ancien.isSubType(nouveau)){
            throw new ContextualError("Impossible de cast", this.getLocation());
        }
        this.setType(nouveau);
        return nouveau;

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        cast.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        cast.iter(f);
        expr.iter(f);
    }
}