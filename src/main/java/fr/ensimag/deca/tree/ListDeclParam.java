package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Created by saunievi on 1/20/20.
 */
public class ListDeclParam extends TreeList<AbstractDeclParam>{

    /**
     * Vérifie la bonne initialisation de chaque variable
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyDeclBody(DecacCompiler compiler,
                               EnvironmentExp localEnv,
                               ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclParam param: this.getList()){
            param.verifyDeclBody(compiler, localEnv, currentClass);
        }
    }

    /**
     * Vérifie que la définition et le type de chaque variable est correcte. Construit et renvoie la signature de la méthode.
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @return
     * @throws ContextualError
     */
    public Signature verifyDeclMembers(DecacCompiler compiler,
                                  EnvironmentExp localEnv,
                                  ClassDefinition currentClass) throws ContextualError{
        Signature sig = new Signature();
        for (AbstractDeclParam param: this.getList()){
            Type sigtype = param.verifyDeclMembers(compiler, localEnv, currentClass);
            sig.add(sigtype);
        }
        return sig;

    }



    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("Not supported yet.");
    	int count = this.size();
    	for(AbstractDeclParam a : this.getList()) {
    		a.decompile(s);
    		if(count>1) {
    			s.print(",");
    		}
    		count--;
    	}
    }
}
