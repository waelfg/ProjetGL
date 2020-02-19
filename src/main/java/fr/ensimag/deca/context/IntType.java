package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class IntType extends Type {

    public IntType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isInt() {
        return true;
    }

    /**
     * return true if otherType is an other int
     * @param otherType
     * @return boolean
     */
    @Override
    public boolean sameType(Type otherType) {
        return  otherType.isInt();
        //throw new UnsupportedOperationException("not yet implemented");
    }


}
