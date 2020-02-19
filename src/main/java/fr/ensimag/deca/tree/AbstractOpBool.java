package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    private static int ind = 0;

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type gauchetype = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type droitetype = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!gauchetype.isBoolean()){
            if (!droitetype.isBoolean()){
                throw new ContextualError("Les deux termes ne sont pas des booléens: " +
                                          "\n membre gauche:" + gauchetype.getName() +
                                          "\n membre droit:" + droitetype.getName(), this.getLocation());
            }
            else{
                throw new ContextualError("Le terme de gauche n'est pas un booléen mais un :"
                                          + gauchetype.getName(), this.getLocation());
            }
        }
        if (!droitetype.isBoolean()){
            throw new ContextualError("Le terme de droite n'est pas un booléen mais un :"
                    + droitetype.getName(), this.getLocation());
        }
        this.setType(gauchetype);
        return this.getType();
    }

    /**
     * Génération de code pour les opérations booléennes
     * @param compiler
     */
    protected void codeOpbool (DecacCompiler compiler) {



        int left;
        int right;

        if (compiler.managerReg.fullRegistres()) {
            getLeftOperand().codeGenInst(compiler);


            getRightOperand().codeGenInst(compiler);
            ind++;


            compiler.managerReg.Recupval(compiler);
            left = 0;
            right = compiler.managerReg.getLastRegused();
        }

        else{

            getLeftOperand().codeGenInst(compiler);
            left = compiler.managerReg.getLastRegused();
            if (compiler.managerReg.fullRegistres()){
                getRightOperand().codeGenInst(compiler);
                compiler.managerReg.Recupval(compiler);
                left = 0;
                right = compiler.managerReg.getLastRegused();
            }
            else {
                getRightOperand().codeGenInst(compiler);
                right = compiler.managerReg.getLastRegused();
            }
            ind++;

        }

        if (getOperatorName() =="&&") {
            compiler.addInstruction((new CMP(0, Register.getR(left))));
            compiler.addInstruction(new BEQ(new Label("Faux_and"+ind)));


            compiler.addInstruction(new CMP(0, Register.getR(right)));
            compiler.addInstruction(new BEQ(new Label("Faux_and"+ind)));


            compiler.addInstruction(new LOAD(1, Register.getR(left)));
            compiler.addInstruction(new BRA(new Label("Vrai_and"+ind)));

            compiler.addLabel(new Label("Faux_and"+ind));
            compiler.addInstruction(new LOAD(0, Register.getR(left)));

            compiler.addLabel(new Label("Vrai_and"+ind));
            compiler.managerReg.setLastRegused(left);
            ind++;
        }

        if (getOperatorName() == "||" ) {


            compiler.addInstruction((new CMP(1, Register.getR(left))));
            compiler.addInstruction(new BEQ(new Label("Vrai_or"+ind)));


            compiler.addInstruction(new CMP(1, Register.getR(right)));
            compiler.addInstruction(new BEQ(new Label("Vrai_or"+ind)));

            compiler.addInstruction(new LOAD(0, Register.getR(left)));
            compiler.addInstruction(new BRA(new Label("Fin_or"+ind)));

            compiler.addLabel(new Label("Vrai_or"+ind));
            compiler.addInstruction(new LOAD(1, Register.getR(right)));
            compiler.addLabel(new Label("Fin_or"+ind));
            compiler.managerReg.setLastRegused(left);
            ind++;
        }
    }
}
