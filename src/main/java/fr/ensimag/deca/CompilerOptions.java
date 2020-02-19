package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl49
 * @date 01/01/2020
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private List<File> sourceFiles = new ArrayList<File>();
    public boolean parse = false;
    public boolean context = false;
    public int reg = 15;

    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
        int place = 0;
        while (place < args.length){
            if (args[place].equals("-p")){
                parse = true;
            }
            else if (args[place].equals("-v")){
                context = true;
            }
            else if (args[place].equals("-r")){
                if (args[place+1].equals(String.valueOf(2))|| args[place+1].equals(String.valueOf(3)) || args[place+1].equals(String.valueOf(1)) || args[place+1].equals(String.valueOf(0))){
                    throw new CLIException("au moins 4 registres");
                }
                boolean cond = false;
                for (int i = 0; i<16; i++){
                    if (args[place+1].equals(String.valueOf(i))){
                        System.out.println(args[place+1].toString());
                        reg = i;
                        cond = true;
                    }
                }
                if (!cond){
                    throw new CLIException("arguments mauvais");
                }
                place++;
            }
            else if (args[place].equals("-b")){
                System.out.println("Projet GL de l'equipe 49");
            }

            place++;
        }

        //throw new UnsupportedOperationException("not yet implemented");
        sourceFiles.add(new File(args[args.length-1]));
    }

    protected void displayUsage() {
        //throw new UnsupportedOperationException("not yet implemented");
    }
}
