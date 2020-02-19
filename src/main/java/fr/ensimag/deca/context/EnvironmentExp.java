package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.util.HashMap;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl49
 * @date 01/01/2020
 */
public class EnvironmentExp {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).
    HashMap<Symbol,ExpDefinition> env;

    public HashMap<Symbol, ExpDefinition> getEnv() {
        return env;
    }

    EnvironmentExp parentEnvironment;
    static boolean racine = true;
    
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.env = new HashMap<Symbol, ExpDefinition>();
    }

    public EnvironmentExp getParentEnvironment() {
        return parentEnvironment;
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key) {
        if (this.parentEnvironment == null){
            if (!this.env.containsKey(key)){
                return null;
            }
            return this.env.get(key);
        }
        else{
            return this.parentEnvironment.get(key);
        }
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        /*if (env.containsKey(name)&&racine){
            throw new DoubleDefException();
        }
        else if (env.containsKey(name)&&!racine){
            env.remove(name);
        }
        else if (racine){
            racine = false;
            if (parentEnvironment != null) {
                parentEnvironment.declare(name, def);
            }
            env.put(name,def);
            racine = true;
        }
        else{
            parentEnvironment.declare(name,def);
        }
        */
        if (this.env.containsKey(name)){
            throw new DoubleDefException();
        }
        else{
            this.env.put(name, def);
        }
    }

}
