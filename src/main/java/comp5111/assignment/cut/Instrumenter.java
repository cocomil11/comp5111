package comp5111.assignment.cut;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;
import soot.jimple.internal.JIfStmt;

import java.util.*;
import java.lang.reflect.*;

import static comp5111.assignment.cut.Counter.incrementTotalBranches;


public class Instrumenter extends BodyTransformer {
    private static int testType;
    public Instrumenter(int testType){
        this.testType = testType;
        System.out.println(testType);
    }

    /* some internal fields */
    static SootClass counterClass;
    static SootMethod addStaticInvocationMethod,
            addInstanceInvocationMethod, hitBranchMethod,
            addExecutedStatementMethod, hitStatementMethod,
            incrementTotalStatementMethod, incrementTotalBranchesMethod;


    static {
        counterClass = Scene.v().loadClassAndSupport("comp5111.assignment.cut.Counter");
        addStaticInvocationMethod = counterClass.getMethod("void addStaticInvocation(int)");
        addInstanceInvocationMethod = counterClass.getMethod("void addInstanceInvocation(int)");
        hitBranchMethod = counterClass.getMethod("void hitBranch(java.lang.String)");
        hitStatementMethod = counterClass.getMethod("void hitStatement(java.lang.String)");
        incrementTotalStatementMethod = counterClass.getMethod("void incrementTotalStatement()");
        incrementTotalBranchesMethod = counterClass.getMethod("void incrementTotalBranches()");

    }

    /*
     * internalTransform goes through a method body and inserts counter
     * instructions before method returns
     */
    @Override
    protected void internalTransform(Body body, String phase, Map options) {
        // body's method
        SootMethod method = body.getMethod();

        // we dont instrument constructor (<init>) and static initializer (<clinit>)
        // Note that you should instrument the constructor and static initializer in your Assignments.
//        if (method.isConstructor() || method.isStaticInitializer()) {
//            System.out.println("testType");
//
//            return;
//        }

        // debugging
        System.out.println("instrumenting method: " + method.getSignature());

        // get body's unit as a chain
        Chain<Unit> units = body.getUnits();

        // get a snapshot iterator of the unit since we are going to
        // mutate the chain when iterating over it.
        //
        Iterator<?> stmtIt = units.snapshotIterator();

        // typical while loop for iterating over each statement
        while (stmtIt.hasNext()) {

            // cast back to a statement.
            Stmt stmt = (Stmt) stmtIt.next();
            System.out.println("stmt");

            // there are many kinds of statements, here we are only
            // interested in return statements
            // NOTE: there are two kinds of return statements, with or without return value
            if (stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt) {
                // now we reach the real instruction
                // call Chain.insertBefore() to insert instructions
                //
                // 1. first, make a new invoke expression
                InvokeExpr incExpr = null;
                if (method.isStatic()) {
                    // if current method is static, we add static method invocation counter
                    incExpr = Jimple.v().newStaticInvokeExpr(
                            addStaticInvocationMethod.makeRef(), IntConstant.v(1));
                } else {
                    // if current method is instance method, we add instance method invocation counter
                    incExpr = Jimple.v().newStaticInvokeExpr(
                            addInstanceInvocationMethod.makeRef(), IntConstant.v(1));
                }
                System.out.println("testType");
                System.out.println(testType);
                System.out.println(this.testType);
                if (testType == 0){
                    InvokeExpr incExpr1 = null;
                    InvokeExpr incExpr2 = null;


                    incExpr1 = Jimple.v().newStaticInvokeExpr(
                            incrementTotalStatementMethod.makeRef());
                    Stmt incStmt1 = Jimple.v().newInvokeStmt(incExpr1);
                    units.insertBefore(incStmt1, stmt);

                    if (stmt instanceof Stmt) {
                        System.out.println("joge");

                        String statementID = body.getMethod().getSignature() + "@" + stmt.getJavaSourceStartLineNumber();

                        incExpr2 = Jimple.v().newStaticInvokeExpr(
                                hitStatementMethod.makeRef(), StringConstant.v(statementID));
                        Stmt incStmtMy = Jimple.v().newInvokeStmt(incExpr2);
                        units.insertBefore(incStmtMy, stmt);
                    }

                }
                else if (testType == 1){
                    InvokeExpr incExpr1 = null;
                    InvokeExpr incExpr2 = null;

                    // branch  coverage
                    String branchID = body.getMethod().getSignature() + "@" + stmt.getJavaSourceStartLineNumber();
                    System.out.println("branchID");
                    System.out.println(branchID);

                    incExpr1 = Jimple.v().newStaticInvokeExpr(
                            incrementTotalBranchesMethod.makeRef());
                    Stmt incStmt1 = Jimple.v().newInvokeStmt(incExpr1);
                    units.insertBefore(incStmt1, stmt);

                    if (stmt instanceof JIfStmt || stmt instanceof SwitchStmt) {
                        // なぜかうまくいかない。
                        System.out.println("joge");
//                        incrementTotalBranchesMethod.makeRef();

                        incExpr2 = Jimple.v().newStaticInvokeExpr(
                                hitBranchMethod.makeRef(), StringConstant.v(branchID));
                        Stmt incStmt2 = Jimple.v().newInvokeStmt(incExpr2);
                        units.insertBefore(incStmt2, stmt);
                    }
                }

                // 2. then, make a invoke statement
                Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);

                // 3. insert new statement into the chain, before return statement
                // (we are mutating the unit chain).
                units.insertBefore(incStmt, stmt);
//                units.insertBefore(incStmtMy, stmt);
            }
        }
    }
}
