package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Created by saunievi on 1/21/20.
 */
public abstract class AbstractMethodBody extends Tree{

    private Type retour;

    public Type getRetour() {
        return retour;
    }

    public void setRetour(Type retour) {
        this.retour = retour;
    }

    public abstract void verifyMethodBody(DecacCompiler compiler,
                                          EnvironmentExp localEnv,
                                          ClassDefinition currentClass,
                                          Type retour) throws ContextualError;


    /**
     * Génération de code pour le cors des méthodes déclarées
     * @param compiler
     */
    public abstract void codeGenBody(DecacCompiler compiler);
}
