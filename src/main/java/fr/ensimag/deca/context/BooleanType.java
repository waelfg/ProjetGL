package fr.ensimag.deca.context;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class BooleanType extends Type {

    public BooleanType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    /**
     * return true if otherType is an other boolean
     * @param otherType
     * @return boolean
     */
    @Override
    public boolean sameType(Type otherType) {
        return otherType.isBoolean();
        //throw new UnsupportedOperationException("not yet implemented");
    }


}
