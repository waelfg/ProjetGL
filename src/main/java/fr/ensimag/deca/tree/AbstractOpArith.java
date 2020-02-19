package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type gauchetype = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type droitetype = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (gauchetype.sameType(droitetype)){
            this.setType(gauchetype);
            return this.getType();
        }
        else if (gauchetype.isInt() && droitetype.isFloat()){
            ConvFloat nouveaugauche = new ConvFloat(this.getLeftOperand());
            this.setLeftOperand(nouveaugauche);
            this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass); //verify de ConvFloat différent de int donc obligé de le faire
            this.setType(droitetype);
            return this.getType();
        }
        else if (gauchetype.isFloat() && droitetype.isInt()){
            ConvFloat nouveaudroit = new ConvFloat(this.getRightOperand());
            this.setRightOperand(nouveaudroit);
            this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
            this.setType(gauchetype);
            return this.getType();
        }
        else{
            throw new ContextualError("Opération entre 2 objets de types incompatibles:\n membre gauche :"
                                        + gauchetype.getName() + "\n membre droit :" + droitetype.getName()
                                        , this.getLocation());
        }
    }


    /**
     *
     * @param compiler
     * @param i
     * CodeGen for arithmetic_op with IntLiteral
     */



    protected void codeExpInt (DecacCompiler compiler){

        int left;
        int right;

        if (compiler.managerReg.fullRegistres()) {
            getLeftOperand().codeGenInst(compiler);


            getRightOperand().codeGenInst(compiler);


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

        }

         if (getOperatorName() == "+"  ) {

            compiler.addInstruction(new ADD(Register.getR(left),Register.getR(right)));


         }
         if (getOperatorName() == "-") {
             compiler.addInstruction((new SUB(Register.getR(right),Register.getR(left))));
             compiler.managerReg.setLastRegused(left);
         }
         if (getOperatorName() == "*") {
             compiler.addInstruction(new MUL(Register.getR(left),Register.getR(right)));
             compiler.managerReg.setLastRegused(right);

         }
         if (getOperatorName() == "/" && getLeftOperand().getType().isInt() && getRightOperand().getType().isInt()) {
             compiler.addInstruction(new QUO(Register.getR(right),Register.getR(left)));
             compiler.managerReg.setLastRegused(left);
         }

         if (getOperatorName() == "/" && getLeftOperand().getType().isFloat() && getRightOperand().getType().isFloat()) {
             compiler.addInstruction(new DIV(Register.getR(right),Register.getR(left)));
             compiler.managerReg.setLastRegused(left);
         }
         if (getOperatorName() == "%") {
             compiler.addInstruction(new REM(Register.getR(right),Register.getR(left)));
             compiler.managerReg.setLastRegused(left);
         }
         if (left == compiler.managerReg.getLastRegused()){
             compiler.managerReg.freeReg(right, compiler);
         }
         else {
             compiler.managerReg.freeReg(left,compiler);
         }

    }



}
