package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class VoidType extends Type {

    public VoidType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isVoid() {
        return true;
    }

    /**
     * return if otherType is a void
     * @param otherType
     * @return true if othertype is a void
     */
    @Override
    public boolean sameType(Type otherType) { return otherType.isVoid();//throw new UnsupportedOperationException("not yet implemented");
    }


}
