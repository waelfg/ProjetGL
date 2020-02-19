package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class NullType extends Type {

    public NullType(SymbolTable.Symbol name) {
        super(name);
    }

    /**
     * return true if otherType is an other Null type
     * @param otherType
     * @return boolean
     */
    @Override
    public boolean sameType(Type otherType) {
        return otherType.isNull();
        //throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }


}
