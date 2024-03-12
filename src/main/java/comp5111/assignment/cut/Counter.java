package comp5111.assignment.cut;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Counter {
    private static final HashMap<String, Integer> counterB = new HashMap<>();
    private static final HashMap<String, Integer> counterS = new HashMap<>();

    private static int numStaticInvocations = 0;
    private static int numInstanceInvocations = 0;

    private static int numExecutedStatement = 0;

    public static int totalBranches = 0;
    public static int executedBranches = 0;
    private static int totalStatements = 0;


    public static void hitBranch(String branchID) {
        if (!counterB.containsKey(branchID)) {
            counterB.put(branchID, 1);
            executedBranches++;
        }
    }
    public static synchronized void hitStatement(String statementID) {
        if (!counterS.containsKey(statementID)) {
            counterS.put(statementID, 1);
            numExecutedStatement++;
        }
    }
    //
    public static synchronized void incrementTotalBranches() {
        totalBranches += 2;
    }
    public static synchronized void incrementTotalStatement() {
        totalStatements += 1;
    }

    //
    public static void printCoveragePercentage() {
        if (totalBranches > 0) {
            double coverage = ((double) executedBranches / totalBranches) * 100;
            System.out.printf("Branch Coverage Percentage: %.2f%%\n", coverage);
        } else {
            System.out.println("No branches.");
        }
    }


    public static void addStaticInvocation(int n) {
        numStaticInvocations += n;
    }

    public static void addInstanceInvocation(int n) {
        numInstanceInvocations += n;
    }



    public static void addExecutedStatement(int n) {
        numExecutedStatement += n;
    }
    public static int getNumInstanceInvocations() {
        return numInstanceInvocations;
    }

    public static int getNumStaticInvocations() {
        return numStaticInvocations;
    }

    public static int getTotalBranchesNum() {
        return totalBranches;
    }

    public static int getTotalStatementsNum() {
        return totalStatements;
    }
    public static int getExecutedStatementsNum() {
        return numExecutedStatement;
    }
    public static HashMap
            <java.lang.String,
                    java.lang.Integer> getExecutedStatementsCounter() {
        return counterS;
    }

    public static HashMap
            <java.lang.String,
                    java.lang.Integer> getExecutedBranchesCounter() {
        return counterB;
    }
    public static int getExecutedBranchesNum() {
        return executedBranches;
    }
}
