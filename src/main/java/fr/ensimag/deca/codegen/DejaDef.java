package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DejaDef extends TreeList<AbstractIdentifier> {



    public DejaDef() {
        SymbolTable sym = new SymbolTable();
        add(new Identifier(sym.create("equals")));
    }

    public boolean isAlreadyDef(AbstractIdentifier name){
        if (this.getList().contains(name)){
            return true;
        }
        return false;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
