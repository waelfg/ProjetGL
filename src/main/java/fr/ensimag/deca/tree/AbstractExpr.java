package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }
    

    private DVal dval;
    

    public DVal getDval() {
        return this.dval;
	}

	public void setDval(DVal dval) {
		this.dval = dval;
	}

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        if (getType() == null){
            Type type = verifyExpr(compiler, localEnv, currentClass);
            setType(type);
        }

        if (expectedType.isFloat()){
            if (getType().isInt()){
                ConvFloat flot = new ConvFloat(this);
                return flot;
            }
        }

        if (getType().isVoid()){
            throw new ContextualError(getType().toString() + " impossible", getLocation());
        }
        return this;
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            verifyExpr(compiler, localEnv, currentClass);

    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type condition = this.verifyExpr(compiler, localEnv, currentClass);
        if (!condition.isBoolean()){
            throw new ContextualError("Une condition ne peut être qu'un booléen et non un :"
                                        + condition.getName(), this.getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compilerde
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
    }


    /**
     * Instructions de génération de code pour toutes les classes qui héritent de AbstractExpr
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
    /**
     * On decompile l'arbre
     * On appelle decompile
     * @param s
     */
	@Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
