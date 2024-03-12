package comp5111.assignment;

import comp5111.assignment.cut.Counter;
import comp5111.assignment.cut.Instrumenter;
import org.junit.runner.JUnitCore;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

import java.io.IOException;
import java.util.Arrays;

public class Assignment1 {
    public static void main(String[] args) throws ClassNotFoundException, IOException {

        /* check the arguments */
        if (args.length <= 1 || (args[0].compareTo("0") != 0 && args[0].compareTo("1") != 0 && args[0].compareTo("2") != 0)) {
            System.err.println("Usage: java comp5111.assignment.Assignment1 [coverage level] test-suite [soot options] " +
                "classname");
            System.err.println("Usage: [coverage level] = 0 for statement coverage");
            System.err.println("Usage: [coverage level] = 1 for branch coverage");
            System.exit(0);
        }

        // these args will be passed into soot.
        String[] classNames = Arrays.copyOfRange(args, 1, args.length);


        if (args[0].compareTo("0") == 0) {
            int testType = 0;
            instrumentWithSoot(testType);
            // after instrument, we run Junit tests
            runJunitTests();
            // after junit test running, we have already get the counting in the Counter class

            System.out.println("Invocation to static methods: " + Counter.getNumStaticInvocations());
            System.out.println("Invocation to instance methods: " + Counter.getNumInstanceInvocations());
            System.out.println("Invocation to statement methods: " + Counter.getTotalStatementsNum());
            System.out.println("Invocation to executed statement methods: " + Counter.getExecutedStatementsNum());
            System.out.println("Invocation to executed statement methods by ID : " + Counter.getExecutedStatementsCounter());


        } else if (args[0].compareTo("1") == 0) {
            int testType = 1;
            instrumentWithSoot(testType);
            // after instrument, we run Junit tests
            runJunitTests();

            System.out.println("Invocation to static methods: " + Counter.getNumStaticInvocations());
            System.out.println("Invocation to instance methods: " + Counter.getNumInstanceInvocations());
            System.out.println("Invocation to branch methods: " + Counter.getTotalBranchesNum());
            System.out.println("Invocation to executed branch methods: " + Counter.getExecutedBranchesNum());
            System.out.println("Invocation to executed branch methods: " + Counter.getExecutedBranchesCounter());
//            Counter.printCoveragePercentage();


        }
    }
    private static void instrumentWithSoot(int testType) {
        // the path to the compiled Subject class file
        String classUnderTestPath = "./raw-classes";
        String targetPath = "./target/classes";

        String classPathSeparator = ":";
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            classPathSeparator = ";";
        }

        /*Set the soot-classpath to include the helper class and class to analyze*/
        Options.v().set_soot_classpath(Scene.v().defaultClassPath() + classPathSeparator + targetPath + classPathSeparator + classUnderTestPath);

        // we set the soot output dir to target/classes so that the instrumented class can override the class file
        Options.v().set_output_dir(targetPath);

        // retain line numbers
        Options.v().set_keep_line_number(true);
        // retain the original variable names
        Options.v().setPhaseOption("jb", "use-original-names:true");

        /* add a phase to transformer pack by call Pack.add */
        Pack jtp = PackManager.v().getPack("jtp");

        Instrumenter instrumenter = new Instrumenter(testType);
        jtp.add(new Transform("jtp.instrumenter", instrumenter));

//        String classUnderTest = "castle.comp5111.example.Subject";
        String classUnderTest = "comp5111.assignment.cut.Subject";
        // pass arguments to soot
        soot.Main.main(new String[]{classUnderTest});  // added phases will be executed in this method
    }
    private static void runJunitTests() {
        Class<?> testClass = null;
        try {
            // here we programmitically run junit tests
            testClass = Class.forName("comp5111.assignment.test.Regression_3_Test");
            JUnitCore junit = new JUnitCore();
            System.out.println("Running junit test: " + testClass.getName());
            junit.run(testClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}