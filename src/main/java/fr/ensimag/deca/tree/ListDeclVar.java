package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl49
 * @date 01/01/2020
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {


    /**
     * fonction générant la déclaration de l'ensemble des variables de l'environnement local en assembleur
     * @param compiler
     */
    void codeGenListDeclVar(DecacCompiler compiler) {
        Iterator<AbstractDeclVar> it = this.iterator();
        int i = 1;
        if (compiler.methode){
            i=3;
        }

        while (it.hasNext()){
            if (!compiler.methode) {
                AbstractDeclVar var = it.next();
                var.setPile_address(new RegisterOffset(i, Register.GB));
                var.codeGenDeclVar(compiler);
                int last = compiler.managerReg.getLastRegused();

                compiler.addInstruction(new STORE(Register.getR(last), new RegisterOffset(i, Register.GB)));
                compiler.managerReg.freeReg(last, compiler);
            }
            else{
                AbstractDeclVar var = it.next();
                var.setPile_address(new RegisterOffset(-i, Register.SP));
                var.codeGenDeclVar(compiler);
                int last = compiler.managerReg.getLastRegused();

                compiler.addInstruction(new STORE(Register.getR(last), new RegisterOffset(-i, Register.SP)));
                compiler.managerReg.freeReg(last, compiler);
            }

            i++;
        }
        compiler.addInstruction(new TSTO(new ImmediateInteger(i)));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));
        compiler.addInstruction(new ADDSP(i));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        Iterator<AbstractDeclVar> it = this.iterator();
        int i = 1;
        while (it.hasNext()){
            AbstractDeclVar var = it.next();
            var.decompile(s);
            //s.print("; ");
            i++;
        }
        s.print("\n");
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */    
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            List<AbstractDeclVar> liste = getList();
            for (int i = 0; i<liste.size();i++){
                liste.get(i).verifyDeclVar(compiler, localEnv, currentClass);
            }
    }


}
