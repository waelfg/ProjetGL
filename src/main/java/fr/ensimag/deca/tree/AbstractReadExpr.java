package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * read...() statement.
 *
 * @author gl49
 * @date 01/01/2020
 */
public abstract class AbstractReadExpr extends AbstractExpr {

    public AbstractReadExpr() {
        super();
    }
    
    /**
     * @param s
     * Decompile the tree, considering it as an instruction.
     * In most case, this simply calls decompile(), but it may add a semicolon if needed
     */
    @Override
    protected void decompileInst(IndentPrintStream s) {
    	decompile(s);
    }
}
