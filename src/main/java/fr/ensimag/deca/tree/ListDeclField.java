package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Created by saunievi on 1/20/20.
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    /**
     * Vérifie la déclaration de chaque champ
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyDeclMembers(DecacCompiler compiler,
                                           EnvironmentExp localEnv,
                                           ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclField field : this.getList()){
            field.verifyDeclMembers(compiler, localEnv, currentClass);
        }
    }

    /**
     * Vérifie l'initialisation de chaque champ
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyDeclBody(DecacCompiler compiler,
                                        EnvironmentExp localEnv,
                                        ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclField field: this.getList()){
            field.verifyDeclBody(compiler, localEnv, currentClass);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	for(AbstractDeclField f:this.getList()) {
    		f.decompile(s);
    	}
    }
}
