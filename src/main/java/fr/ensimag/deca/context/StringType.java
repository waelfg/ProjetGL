package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class StringType extends Type {

    public StringType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isString() {
        return true;
    }

    /**
     * return true if otherType is an other string
     * @param otherType
     * @return boolean
     */
    @Override
    public boolean sameType(Type otherType) {
        return otherType.isString();
        //throw new UnsupportedOperationException("not yet implemented");
    }


}
