package cs2110;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;
import java.util.LinkedList;

/**
 * The main program of the A4 assignment. It reads an academic genealogy in CSV format and supports
 * various commands for querying the genealogy tree.
 */
public class PhDApp {

    /**
     * Create and run an application instance using the program arguments `args`.  Exit with error
     * code 1 on failure.
     */
    public static void main(String[] args) {
        // Why create an instance of `PhDApp` rather than doing everything statically?  Because this
        // pattern promotes code reuse.  Code with mutable static variables typically cannot be used
        // more than once in the same application (since there is only one copy of those variables),
        // and code that calls `System.exit()` cannot be composed with other code that might want to
        // handle such errors and keep running.  This pattern minimizes the code that can't be
        // composed, confining it to a very short `main()` method.
        // When would you want to reuse or compose code for a user-facing application?  This
        // actually happens more often than you might think: testing, application severs (serving
        // multiple users), and distributed computing (code running on multiple computers) all
        // benefit from composable code at the application level.

        try {
            PhDApp app = new PhDApp(args);
            boolean success = app.run();
            if (!success) {
                System.exit(1);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(1);
        }
    }

    /**
     * Name of the file to read commands from, or an empty string if commands should be read from
     * `System.in`.
     */
    private String inputFile = "";

    /**
     * Name of the file to read the genealogy tree from.  Defaults to "professors.csv" in the
     * current working directory.
     */
    private String csvFileName = "professors-shortened.csv";

    /**
     * The academic genealogy tree to be read and queried.
     */
    private PhDTree professorTree;

    /**
     * Create a new application instance and configure it from the arguments in `args`. See output
     * of `printUsage()` for valid arguments.  Throws `IllegalArgumentException` if arguments are
     * invalid.
     */
    public PhDApp(String[] args) throws IllegalArgumentException {
        processProgramArguments(args);
    }

    /**
     * Create a PhDTree from the contents of the configured genealogy tree file, then read and
     * respond to queries from the configured command input source.  Return false if a PhDTree could
     * not be read from the file, otherwise true.
     */
    public boolean run() {
        try {
            professorTree = csvToTree(new FileReader(csvFileName));
        } catch (IOException e) {
            System.err.println("Could not read tree file: " + e.getMessage());
            return false;
        } catch (InputFormatException e) {
            System.err.println("Invalid file format: " + e.getMessage());
            return false;
        }
        processCommands();
        return true;
    }

    /**
     * Print a usage message to System.err.
     */
    public static void printUsage() {
        System.err.println("Usage: java cs2110.PhDApp [--help] [-i <input script>] [filename.csv]");
    }

    /**
     * Parse the arguments given in `args` and set configuration fields appropriately. Throws
     * `IllegalArgumentException` if arguments are invalid or if "--help" was provided.
     */
    private void processProgramArguments(String[] args) throws IllegalArgumentException {
        int i;
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                if (i + 1 < args.length) {
                    inputFile = args[i + 1];
                    i++;
                } else {
                    throw new IllegalArgumentException("Missing argument after -i");
                }
            } else if (args[i].equals("--help")) {
                throw new IllegalArgumentException("Help requested");
            } else {
                break;
            }
        }
        if (i < args.length) {
            csvFileName = args[i];
            i++;
        }
        if (i != args.length) {
            throw new IllegalArgumentException("Too many arguments");
        }
    }

    /**
     * Returns a PhDTree representation of the CSV file named by filename.  Throws
     * `InputFormatException` if the file format is invalid or represents an invalid tree.
     */
    public static PhDTree csvToTree(Reader in) throws InputFormatException {
        try (Scanner sc = new Scanner(in)) {
            // Read and validate header
            String[] header = sc.nextLine().split(",", -1);
            if (!Arrays.equals(header, new String[]{"advisee", "year", "advisor"})) {
                throw new InputFormatException("Unexpected header");
            }

            // TODO 10: Implement the remainder of this method according to its specification.
            // Use `sc` to read lines and `split()` to separate fields.
            // Remember that the first line after the header is special - it represents the root.
            //throw new UnsupportedOperationException();
            String[] firstProf = sc.nextLine().split(",",-1);
            System.out.println(firstProf.length + firstProf[0]);
            if(!firstProf[2].equals("")){
                throw new InputFormatException("Unexpected root professor");
            }
            int year;
            try{
                year = Integer.parseInt(firstProf[1]);
            } catch(NumberFormatException e){
                throw new InputFormatException("Year is not a number");
            }
            Professor rootProf = new Professor(firstProf[0],year);
            PhDTree tree = new PhDTree(rootProf);
            while(sc.hasNext()){
                String[] professorInput = sc.nextLine().split(",",-1);
                if(professorInput.length != 3){
                    throw new InputFormatException("Unexpected root professor");
                }
                try{
                    year = Integer.parseInt(professorInput[1]);
                } catch(NumberFormatException e){
                    throw new InputFormatException("Year is not a number");
                }
                String advisor = professorInput[2];
                Professor professorIn = new Professor(professorInput[0],year);
                    try {
                        tree.insert(advisor, professorIn);
                    }catch (NotFound e){
                        throw new InputFormatException("Advisor is not previously declared "
                                + "as an advisee");
                    } catch (AssertionError e) {
                        throw new InputFormatException("Duplicate advisees");
                    }
            }
            return tree;
        }
    }

    /**
     * Read commands from the configured input source and execute them.  If the input source is
     * `System.in`, a prompt is printed to `System.out` to request the next command.  Command
     * output, as well as any command error messages, is printed to `System.out`.  Returns when the
     * "exit" command is read or when there is no more input.  If the input source is a file that
     * cannot be found, a message is printed to `System.err` and the method returns.
     */
    public void processCommands() {
        Scanner sc;
        if (inputFile.isEmpty()) {
            sc = new Scanner(System.in);
        } else {
            try {
                sc = new Scanner(new File(inputFile));
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
                return;
            }
        }
        while (true) {
            if (inputFile.isEmpty()) {
                System.out.print("Please enter a command: ");
            }
            try {
                String input = sc.nextLine().trim();
                String[] cmdParts = input.split("\\s+", 2);
                String cmd = cmdParts[0].toLowerCase();
                String arg = (cmdParts.length > 1) ? cmdParts[1] : "";
                try {
                    switch (cmd) {
                        case "help":
                            doHelp();
                            break;
                        case "print":
                            doPrint(arg);
                            break;
                        case "contains":
                            doContains(arg);
                            break;
                        case "size":
                            doSize(arg);
                            break;
                        case "advisor":
                            doAdvisor(arg);
                            break;
                        case "ancestor":
                            doAncestor(arg);
                            break;
                        case "lineage":
                            doLineage(arg);
                            break;
                        case "exit":
                            return;
                        default:
                            System.out.println(
                                    "This is not a valid command. For help, enter the command \"help\"");
                    }
                } catch (IllegalArgumentException e) {
                    invalidCommand(cmd, e.getMessage());
                }
            } catch (NoSuchElementException exc) {
                // no more lines on input
                return;
            }
        }
    }

    /**
     * Print a message about the command being invalid to `System.out`.
     */
    private static void invalidCommand(String cmd, String message) {
        System.out.println("Invalid " + cmd + " command: " + message);
        System.out.println("Enter the command \"help\" for information about that command.");
    }

    /**
     * Perform the help command.
     */
    public static void doHelp() {
        System.out.println("help");
        System.out.println("print [<advisor name>]: print every professor in the academic "
                + "genealogy of the given professor (default: root) with their degree year");
        System.out.println("contains <prof name> : whether this professor is in the PhD tree");
        System.out.println("size [<advisor name>] : the number of academic descendants of the "
                + "given professor (default: root), including themselves");
        System.out.println("advisor <advisee name> : the direct advisor of the given professor");
        System.out.println("ancestor <prof 1>, <prof 2> : the common ancestor between the two "
                + "given professors");
        System.out.println("lineage <prof name> : the sequence of advisors from the root to the "
                + "given professor");
        System.out.println("exit : exit the program");
    }

    /**
     * Perform the "print" command with arguments string `arg`.  Arguments must either be empty or
     * contain a single professor's name (surrounding whitespace is ignored).
     */
    public void doPrint(String arg) {
        // Which subtree should be printed (by default, the whole tree)
        PhDTree subtree = professorTree;

        // If an argument was provided, find the subtree rooted at that professor
        if (!arg.isEmpty()) {
            // Trim any whitespace surrounding the argument
            String subtreeRoot = arg.trim();
            try {
                subtree = professorTree.findTree(subtreeRoot);
            } catch (NotFound exc) {
                System.out.println("This person does not exist in the tree.");
                return;
            }
        }

        // Wrap `System.out` in a `PrintWriter` to satisfy `printProfessors()`, then flush the
        // wrapper to ensure output gets written to the underlying stream.
        PrintWriter pw = new PrintWriter(System.out);
        subtree.printProfessors(pw);
        pw.flush();
    }

    /**
     * Perform the "contains" command with arguments string `arg`.  Throws IllegalArgumentException
     * if `arg` does not contain a single professor's name (surrounding whitespace is ignored).
     */
    public void doContains(String arg) {
        // Extract professor's name from arguments and store in `targetName`.
        if (arg.isEmpty()) {
            throw new IllegalArgumentException("Missing argument");
        }
        String targetName = arg.trim();

        // TODO 11: Complete this method to satisfy the application requirements.
        // throw new UnsupportedOperationException();
        PrintWriter pw = new PrintWriter(System.out);
        if(professorTree.contains(targetName)){
            pw.println("This professor is contained in the tree.");
            pw.flush();
        } else{
            pw.println("This professor is not contained in the tree.");
            pw.flush();
        }
    }

    /**
     * Perform the "size" command with arguments string `arg`.  Arguments must either be empty or
     * contain a single professor's name (surrounding whitespace is ignored).
     */
    public void doSize(String arg) {
        // TODO 12: Implement this method to satisfy the application requirements.
        //throw new UnsupportedOperationException();
        PrintWriter pw = new PrintWriter(System.out);
        if (arg.isEmpty()){
            int size = professorTree.size();
            pw.println("The number of nodes in this tree is: " + size + ".");
            pw.flush();
        } else{
            String targetName = arg.trim();
            try{
                int size = professorTree.findTree(targetName).size();
                pw.println("The number of nodes in this tree is: " + size + ".");
                pw.flush();
            } catch (NotFound e){
                pw.println("This professor does not exist in the tree.");
                pw.flush();
            }
        }
    }

    /**
     * Perform the "advisor" command with arguments string `arg`.  Throws IllegalArgumentException
     * if `arg` does not contain a single professor's name (surrounding whitespace is ignored).
     */
    public void doAdvisor(String arg) {
        // TODO 13: Implement this method to satisfy the application requirements.
        // throw new UnsupportedOperationException();
        if (arg.isEmpty()) {
            throw new IllegalArgumentException("Missing argument");
        }
        String targetName = arg.trim();
        PrintWriter pw = new PrintWriter(System.out);
        if (professorTree.prof().name().equals(targetName)){
            pw.println("This professor does not have an advisor in the tree.");
            pw.flush();
        } else {
            try {
                Professor advisor = professorTree.findAdvisor(arg);
                pw.println("The advisor of this advisee is: " + advisor + ".");
                pw.flush();
            } catch (NotFound e){
                pw.println("This professor does not exist in the tree.");
                pw.flush();
            }
        }
    }

    /**
     * Perform the "ancestor" command with arguments string `arg`.  Throws IllegalArgumentException
     * if `arg` does not contain two professors' names separated by a comma (surrounding whitespace
     * is ignored).
     */
    public void doAncestor(String arg) {
        // Extract both professors' names from arguments and store in `profNames`.
        String[] profNames = arg.trim().split("\\s*,\\s*");
        if (profNames.length != 2) {
            throw new IllegalArgumentException("Missing arguments");
        }
        // TODO 14: Complete this method to satisfy the application requirements.
        //throw new UnsupportedOperationException();
        PrintWriter pw = new PrintWriter(System.out);
        try{
            Professor proRet = professorTree.commonAncestor(profNames[0],profNames[1]);
            pw.println("The common ancestor of these professors is: "+proRet+",");
            pw.flush();
        }catch(NotFound ignore){
            System.out.println("These professors do not have a common ancestor in the tree.\n");
            pw.flush();
        }
    }

    /**
     * Perform the "lineage" command with arguments string `arg`.  Throws IllegalArgumentException
     * if `arg` does not contain a single professor's name (surrounding whitespace is ignored).
     */
    public void doLineage(String arg) {
        // TODO 15: Implement this method to satisfy the application requirements.
        //throw new UnsupportedOperationException();
        try{
            List<Professor> linReturn = professorTree.findAcademicLineage(arg);
            StringBuilder s = new StringBuilder();
            for(Professor prof: linReturn){
                s.append(prof + "--");
            }
            String reply = s.toString().substring(0,s.length()-2);
            System.out.println("The lineage is: " + reply + ".");
        }catch(NotFound ignore){
            System.out.println("This professor does not exist in the tree");
        }
    }
}
