package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
/**
 * Created by saunievi on 1/20/20.
 */
public abstract class AbstractDeclParam extends Tree {

    public abstract Type verifyDeclMembers(DecacCompiler compiler,
                                           EnvironmentExp localEnv,
                                           ClassDefinition currentClass) throws ContextualError;
    public abstract void verifyDeclBody(DecacCompiler compiler,
                                        EnvironmentExp localEnv,
                                        ClassDefinition currentClass) throws ContextualError;


}
