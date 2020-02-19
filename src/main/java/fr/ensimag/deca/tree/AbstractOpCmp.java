package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    private static int ind = 0;
    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type gauchetype = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type droitetype = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (gauchetype.sameType(droitetype)){
            if ((gauchetype.isInt()) || (gauchetype.isFloat()) || gauchetype.isBoolean()){
                Type bool = new BooleanType(compiler.sym.create("boolean"));
                this.setType(bool);
                return this.getType();
            }
            else{
                throw new ContextualError("Comparaison entre autre chose que des nombres:\n membre gauche:"
                                            + gauchetype.getName() +"\n membre droit : " + droitetype.getName()
                                            , this.getLocation());
            }
        }
        else if (gauchetype.isInt() && droitetype.isFloat()){
            ConvFloat nouveaugauche = new ConvFloat(this.getLeftOperand());
            this.setLeftOperand(nouveaugauche);
            this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass); //verify de ConvFloat différent de int donc obligé de le faire
            Type bool = new BooleanType(compiler.sym.create("boolean"));
            this.setType(bool);
            return this.getType();
        }
        else if (gauchetype.isFloat() && droitetype.isInt()){
            ConvFloat nouveaudroit = new ConvFloat(this.getRightOperand());
            this.setRightOperand(nouveaudroit);
            this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
            Type bool = new BooleanType(compiler.sym.create("boolean"));
            this.setType(bool);
            return this.getType();
        }
        else{
            throw new ContextualError("Comparaison entre deux membres incompatibles : \n membre gauche:"
                                        + gauchetype.getName() + "\n membre droit " + droitetype.getName()
                                        , this.getLocation());
        }
    }

    /**
     * Génération de code pour les opérations de comparaison
     * @param compiler
     */
    protected void codeOpCmp (DecacCompiler compiler) {

        Label end_cond = new Label("end_cond");
        int left;
        int right;

        if (compiler.managerReg.fullRegistres()) {
            getLeftOperand().codeGenInst(compiler);


            getRightOperand().codeGenInst(compiler);
            ind++;


            compiler.managerReg.Recupval(compiler);
            right = 0;
            left = compiler.managerReg.getLastRegused();
        }

        else{

            getLeftOperand().codeGenInst(compiler);
             left = compiler.managerReg.getLastRegused();
             if (compiler.managerReg.fullRegistres()){
                 getRightOperand().codeGenInst(compiler);
                 compiler.managerReg.Recupval(compiler);
                 right = 0;
                 left = compiler.managerReg.getLastRegused();
             }
             else {
                 getRightOperand().codeGenInst(compiler);
                 right = compiler.managerReg.getLastRegused();
             }
                 ind++;

        }
        compiler.addInstruction(new CMP(Register.getR(right),Register.getR(left)));

        if (getOperatorName() == ">="){
            Label greateq = new Label("greateq"+ind) ;
            compiler.addInstruction(new BGE(greateq));
            compiler.addInstruction(new LOAD(0, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addInstruction(new BRA(new Label("Fin_greateq"+ind)));
            compiler.addLabel(greateq);
            compiler.addInstruction(new LOAD(1, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addLabel(new Label("Fin_greateq"+ind));

            compiler.managerReg.freeReg(left, compiler);
        }

        if (getOperatorName() == ">"){


            Label great = new Label("great"+ind) ;
            compiler.addInstruction(new BGT(great));
            compiler.addInstruction(new LOAD(0, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addInstruction(new BRA(new Label("Fin_great"+ind)));
            compiler.addLabel(great);
            compiler.addInstruction(new LOAD(1, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addLabel(new Label("Fin_great"+ind));

            compiler.managerReg.freeReg(left, compiler);
        }

        if (getOperatorName() == "<="){


            Label lowereq = new Label("lowereq"+ind) ;
            compiler.addInstruction(new BLE(lowereq));
            compiler.managerReg.freeReg(right, compiler);
            compiler.addInstruction(new LOAD(0, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addInstruction(new BRA(new Label("Fin_lowereq"+ind)));
            compiler.addLabel(lowereq);
            compiler.addInstruction(new LOAD(1, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addLabel(new Label("Fin_lowereq"+ind));

            compiler.managerReg.freeReg(left, compiler);

        }

        if (getOperatorName() == "<"){


            Label lower = new Label("lower"+ind) ;
            compiler.addInstruction(new BLT(lower));
            compiler.addInstruction(new LOAD(0, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addInstruction(new BRA(new Label("Fin_lower"+ind)));
            compiler.addLabel(lower);
            compiler.addInstruction(new LOAD(1, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addLabel(new Label("Fin_lower"+ind));

            compiler.managerReg.freeReg(left, compiler);
        }

        if (getOperatorName() == "=="){


            Label eq = new Label("eq"+ind) ;
            compiler.addInstruction(new BEQ(eq));
            compiler.addInstruction(new LOAD(0, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addInstruction(new BRA(new Label("Fin_eq"+ind)));
            compiler.addLabel(eq);
            compiler.addInstruction(new LOAD(1, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addLabel(new Label("Fin_eq"+ind));
            compiler.managerReg.freeReg(left, compiler);

        }
        if(getOperatorName() == "!="){


            Label neq = new Label("neq"+ind) ;
            compiler.addInstruction(new BNE(neq));
            compiler.addInstruction(new LOAD(0, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addInstruction(new BRA(new Label("Fin_neq"+ind)));
            compiler.addLabel(neq);
            compiler.addInstruction(new LOAD(1, Register.getR(compiler.managerReg.getLastRegused())));
            compiler.addLabel(new Label("Fin_neq"+ind));
            compiler.managerReg.freeReg(left, compiler);
        }




    }
}
