package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.TreeList;

import java.util.HashMap;

public class ClassDef{
    private HashMap<Identifier, DejaDef> classes = new HashMap<Identifier, DejaDef>();

    public ClassDef() {

    }

}
