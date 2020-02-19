/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;
import java.util.HashMap;

/**
 *
 * @author ollierv
 */
public class EnvironmentType {
    public HashMap<SymbolTable.Symbol,Definition> type = new HashMap();
    public HashMap<SymbolTable.Symbol, DAddr> registres = new HashMap<>();


    public EnvironmentType(SymbolTable sym) {
        this.type.put(sym.create("void"), new TypeDefinition(new VoidType(sym.create("void")), Location.BUILTIN));
        this.type.put(sym.create("int"), new TypeDefinition(new IntType(sym.create("int")), Location.BUILTIN));
        this.type.put(sym.create("float"), new TypeDefinition(new FloatType(sym.create("float")), Location.BUILTIN));
        this.type.put(sym.create("boolean"), new TypeDefinition(new BooleanType(sym.create("boolean")), Location.BUILTIN));
        ClassType Object = new ClassType(sym.create("Object"), Location.BUILTIN, null);
        Object.getDefinition().setNumberOfMethods(1);
        Object.getDefinition().setNumberOfFields(0);
        Object.getDefinition().setAdresspile(1);
        this.type.put(sym.create("Object"), Object.getDefinition());
    }

        
        
    
        
    @Override
    public String toString() {
        return "EnvironmentType{" + "type=" + type + '}';
    }
    
    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }
    
        public Definition get(SymbolTable.Symbol key) {
        if (!type.containsKey(key)){
            return null;
        }
        return type.get(key);
        }
        
        
        
        public Definition put(SymbolTable.Symbol key, Definition def) throws DoubleDefException{
            if (type.containsKey(key)){
                throw new DoubleDefException();
            }
            else {
                type.put(key, def);
            }
            return def;
        }

    public EnvironmentType() {
    }
        
        
        
        
}
