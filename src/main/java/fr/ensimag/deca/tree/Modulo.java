package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     * Vérifie que les deux opérandes soient des entiers
     * Fixe le type à int
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @return
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type gauchetype = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type droitetype = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((!gauchetype.isInt()) || (!droitetype.isInt())){
            throw new ContextualError("Les deux termes doivent être des entiers : membre gauche"
                                        + gauchetype.getName() + " membre droit :" + droitetype.getName()
                                        , this.getLocation());

        }
        this.setType(gauchetype);
        return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeExpInt(compiler);

    }

}
