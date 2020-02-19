package fr.ensimag.deca.tree;


import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl49
 * @date 01/01/2020
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue) super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    /**
     * Vérifie si le type de droite est le même que celui de gauche
     * Ajoute un ConvFloat si nécessaire
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
        if (getLeftOperand().getType() == null) {
            getLeftOperand().setType(getLeftOperand().verifyExpr(compiler, localEnv, currentClass));
        }
        setType(getLeftOperand().getType());
        getRightOperand().verifyRValue(compiler, localEnv, currentClass, getLeftOperand().getType());
        if (getLeftOperand().getType().isFloat()) {
            if (getRightOperand().getType().isInt()) {
                ConvFloat flot = new ConvFloat(getRightOperand());
                setRightOperand(flot);
            }
            else if (!getLeftOperand().getType().sameType(getRightOperand().getType())) {
                throw new ContextualError("new ContextualError : right value " + getRightOperand().getType().toString() + " expected " + getLeftOperand().getType().toString(), getLocation());
            }
        } else if (!getLeftOperand().getType().sameType(getRightOperand().getType())) {
            throw new ContextualError("new ContextualError : right value " + getRightOperand().getType().toString() + " expected " + getLeftOperand().getType().toString(), getLocation());
        }

        return getLeftOperand().getType();
    }





    @Override
    protected String getOperatorName() {
        return "=";
    }



    /**
     * génère le code correspondant à l'affectation d'une expression a une variable
>>>>>>> a94a85ec3f958ef46f16f17aee5c87a945afef19
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getRightOperand().codeGenInst(compiler);
        int der = compiler.managerReg.getLastRegused();

        System.out.println(getLeftOperand().getClass().toString());
        if (getLeftOperand().getClass().toString().equals("class fr.ensimag.deca.tree.SelectionField")) {
            getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new STORE(Register.getR(der), compiler.luse));
        } else {
            compiler.addInstruction(new STORE(Register.getR(compiler.managerReg.getLastRegused()), getLeftOperand().getPile_address(compiler)));
            getLeftOperand().setDval(Register.getR(compiler.managerReg.getLastRegused()));
        }
    }
}
