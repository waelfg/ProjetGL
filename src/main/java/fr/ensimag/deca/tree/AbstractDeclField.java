package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/20/20.
 */

public abstract class AbstractDeclField extends Tree {

    public abstract void verifyDeclMembers(DecacCompiler compiler,
                                           EnvironmentExp localEnv,
                                           ClassDefinition currentClass) throws ContextualError;
    public abstract void verifyDeclBody(DecacCompiler compiler,
                                        EnvironmentExp localEnv,
                                        ClassDefinition currentClass) throws ContextualError;

    /**
     * Génération de code lors de la déclaration des champs (attributs et méthodes)
     * @param compiler
     * @param classe
     */
    public abstract void codeGenDeclField(DecacCompiler compiler, AbstractIdentifier classe);
}