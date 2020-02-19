package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.util.List;

/**
 * Created by saunievi on 1/20/20.
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    /**
     * Vérifie que la déclaration de chaque méthode est correcte
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyDeclMembers(DecacCompiler compiler,
                                  EnvironmentExp localEnv,
                                  ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclMethod method: this.getList()){
            method.verifyDeclMembers(compiler, localEnv, currentClass);
        }
    }

    /**
     * Vérifie que le corps de chaque méthode est correct
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyDeclBody(DecacCompiler compiler,
                               EnvironmentExp localEnv,
                               ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclMethod method: this.getList()){
            method.verifyDeclBody(compiler, localEnv, currentClass);
        }
    }

    /**
     * fonction générant le code de l'ensemble des méthodes d'une classe en assembleur
     * @param compiler
     * @param classname
     */

    public void codeGenlistMethod (DecacCompiler compiler, AbstractIdentifier classname){
        List<AbstractDeclMethod> meth = getList();
        for (int i = 0; i<meth.size(); i++){
            meth.get(i).codeGenMethod(compiler, classname);
        }
    }


    @Override
    public void decompile(IndentPrintStream s) {
    	for (AbstractDeclMethod method: this.getList()) {
    		method.decompile(s);
    	}
    }


}
