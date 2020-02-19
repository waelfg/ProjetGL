package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Objet;
//import fr.ensimag.deca.codegen.Object;
import fr.ensimag.deca.codegen.Objet;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl49
 * @date 01/01/2020
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    /**
     * Vérification contextuelle globale du programme, on fait les 3 passes nécessaires.
     * On vérifie d'abord la déclaration des classes, puis des champs et méthodes,
     * puis l'initialisation des champs et le corps des méthodes, puis on fait la vérification du main
     * @param compiler
     * @throws ContextualError
     */
    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        this.getClasses().verifyListClass(compiler);
        this.getClasses().verifyListClassMembers(compiler);
        this.getClasses().verifyListClassBody(compiler);
        main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    /**
     * fonction générant le code assembleur de l'ensemble d'un programme deca
     * @param compiler
     */
    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        int i = getClasses().size();

        // A FAIRE: compléter ce squelette très rudimentaire de code
        compiler.addInstruction(new BRA(new Label("Deb_prog")));
        classes.codeGenDeclField(compiler);

        compiler.addComment("Début du programme");
        compiler.addLabel(new Label("Deb_prog"));
        classes.codeGenListDeclClass(compiler);
        compiler.addComment("Main program");
        main.codeGenMain(compiler);


        compiler.addInstruction(new HALT());





        Objet.codeGenEq(compiler);

        compiler.addLabel(new Label("pile_pleine"));
        compiler.addInstruction(new WSTR("Erreur: la pile est pleine"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("dereferencement_null"));
        compiler.addInstruction(new WSTR("Erreur: dereferencement de null"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("tas_plein"));
        compiler.addInstruction(new WSTR("Erreur: allocation impossible, tas plein"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

    }
    
    
    /**
     * @param s
     * On decompile l'arbre.
     * On appelle le decompile
     */

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
