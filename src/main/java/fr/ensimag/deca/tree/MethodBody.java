package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/21/20.
 */
public class MethodBody extends AbstractMethodBody{

    private ListDeclVar listDeclVar;
    private ListInst listInst;

    public MethodBody(ListDeclVar listDeclVar, ListInst listInst) {
        Validate.notNull(listDeclVar);
        Validate.notNull(listInst);
        this.listDeclVar = listDeclVar;
        this.listInst = listInst;
    }

    /**
     * Vérifie que la liste de variables et la liste d'instructions sont correctes.
     * Fixe le type de retour de la méthode.
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @param retour
     * @throws ContextualError
     */
    @Override
    public void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type retour) throws ContextualError {
        this.listDeclVar.verifyListDeclVariable(compiler, localEnv, currentClass);
        this.listInst.verifyListInst(compiler, localEnv, currentClass, retour);
        this.setRetour(retour);
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	this.listDeclVar.decompile(s);
    	this.listInst.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	listDeclVar.prettyPrint(s, prefix, false);
    	listInst.prettyPrint(s, prefix, false);
    }



    @Override
    protected void iterChildren(TreeFunction f) {
    	this.listDeclVar.iterChildren(f);
    	this.listInst.iterChildren(f);
    }


    /**
     * fonction générant le code assembleur du corps d'une méthode
     * @param compiler
     */

    public void codeGenBody(DecacCompiler compiler) {
        compiler.methode = true;
        listDeclVar.codeGenListDeclVar(compiler);
        listInst.codeGenListInst(compiler);
    }
}
