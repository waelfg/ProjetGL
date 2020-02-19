package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl49
 * @date 01/01/2020
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }


    /**
     * Fixe le type à float
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        Type t = new FloatType(compiler.sym.create("float"));
        this.setType(t);
        return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }



    /**
     * fonction générant le code assembleur d'un entier en flottant
>>>>>>> a94a85ec3f958ef46f16f17aee5c87a945afef19
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        getOperand().codeGenInst(compiler);
        int i = compiler.managerReg.getLastRegused();
        compiler.addInstruction(new FLOAT(Register.getR(i),Register.getR(i)));
        setDval(Register.getR(i));
    }
}
