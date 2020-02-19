package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * Class declaration.
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractDeclClass extends Tree {

    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Génération de code pour la déclaration de classes
     * @param compiler
     */
    public abstract void codeGenDeclClass(DecacCompiler compiler);


    /**
     * Génération du code de la table des classes (Les classes déclarées et les super-class des quelles elles héritent
     * @param compiler
     */
    public abstract void codeGenclassTable(DecacCompiler compiler);

    public abstract void setPile_address(RegisterOffset registerOffset);

    private DAddr pile_address;
    public abstract AbstractIdentifier getName();



    public DAddr getPile_address() {
        return pile_address;
    }

    /**
     * Génération de code lors de la déclaration des champs (attributs et méthodes)
     * @param compiler
     */
    public abstract void codeGenDeclFields(DecacCompiler compiler);

}
