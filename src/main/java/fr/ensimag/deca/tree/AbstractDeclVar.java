package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Variable declaration
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractDeclVar extends Tree {
    private DAddr pile_address;
        
        
    public DAddr getPile_address() {
        return pile_address;
    }

    public void setPile_address(DAddr pile_address) {
        this.pile_address = pile_address;
    }
    
    /**
     * Implements non-terminal "decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to the "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the synthetized attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     */    
    protected abstract void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Génération de code pour lors de la déclaration de variables
     * @param compiler
     */
    abstract void codeGenDeclVar(DecacCompiler compiler);
}
