package fr.cnumr.java.checks;

/*
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;
*/

/*
@Rule(key = "UseFinalKeywordRule")
public class UseFinalKeywordCheck extends IssuableSubscriptionVisitor {

    @Override
    public List<Kind> nodesToVisit() {
        //return  Arrays.asList(Kind.VARIABLE);
        return  Collections.unmodifiableList(Arrays.asList(Kind.METHOD_INVOCATION));
    }

    @Override
    public void visitNode(Tree tree) {
        reportIssue(tree, "TEST");
    }
}
*/

/*

@Rule(key = "UseFinalKeywordCheckRule")
public class UseFinalKeywordCheck extends IssuableSubscriptionVisitor {
    @Override
    public List<Kind> nodesToVisit() {
        //return Collections.unmodifiableList(Arrays.asList(Kind.VARIABLE));
        return Collections.unmodifiableList(Arrays.asList(Kind.METHOD_INVOCATION));
    }
    
    @Override
    public void visitNode(Tree tree) {
        reportIssue(tree, "UseFinalKeywordCheckRule");
        if (tree.is(Kind.VARIABLE)) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            Symbol symbol = methodInvocation.symbol();

            //reportIssue(tree, "UseFinalKeywordCheckRule");
        }
    }
}*/

/*
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "UseFinalKeywordRule")
public class UseFinalKeywordCheck extends IssuableSubscriptionVisitor {
    @Override
    public List<Kind> nodesToVisit() {
        return Collections.unmodifiableList(Arrays.asList(Kind.VARIABLE));
        // return Collections.singletonList(Kind.MEMBER_SELECT); //INSTANCE_OF, IMPORT, METHOD_REFERENCE, VARIABLE
    }
    
    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.VARIABLE)) {
            VariableTree variableTree = (VariableTree) tree;
            System.out.println("-------------------------------------------------------");
            System.out.println("-------------------------------------------------------");
            System.out.println("-------------------------------------------------------");
            System.out.println("-------------------------------------------------------");
            System.out.println("-------------------------------------------------------");
            System.out.println(variableTree.symbol().name());
            System.out.println(variableTree.type().symbolType());
            System.out.println(variableTree.symbol());



            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            Symbol symbol = methodInvocation.symbol();

            if (symbol.isMethodSymbol() &&
                symbol.name().equals("replace")
                && methodInvocation.symbol().owner().name().equals("String"))
                 {
                    reportIssue(tree, "Use StringUtils.replace instead of String.replace !");
            }
        }
    }
}

*/
import java.util.HashMap; 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.UnaryExpressionTree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "UseFinalKeywordCheckRule")
public class UseFinalKeywordCheck extends IssuableSubscriptionVisitor {

    private static final Tree.Kind[] ASSIGNMENT_KINDS = {
      Tree.Kind.ASSIGNMENT,
      Tree.Kind.MULTIPLY_ASSIGNMENT,
      Tree.Kind.DIVIDE_ASSIGNMENT,
      Tree.Kind.REMAINDER_ASSIGNMENT,
      Tree.Kind.PLUS_ASSIGNMENT,
      Tree.Kind.MINUS_ASSIGNMENT,
      Tree.Kind.LEFT_SHIFT_ASSIGNMENT,
      Tree.Kind.RIGHT_SHIFT_ASSIGNMENT,
      Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT,
      Tree.Kind.AND_ASSIGNMENT,
      Tree.Kind.XOR_ASSIGNMENT,
      Tree.Kind.OR_ASSIGNMENT
    };
  
    private static final Tree.Kind[] INCREMENT_KINDS = {
      Tree.Kind.POSTFIX_DECREMENT,
      Tree.Kind.POSTFIX_INCREMENT,
      Tree.Kind.PREFIX_DECREMENT,
      Tree.Kind.PREFIX_INCREMENT
    };
  
    private List<VariableTree> variables = new ArrayList<>();
    private List<Symbol> assignments = new ArrayList<>();
  
    @Override
    public List<Kind> nodesToVisit() {
      return new ArrayList<>(List.of(
        Tree.Kind.BLOCK, Tree.Kind.STATIC_INITIALIZER,
        Tree.Kind.FOR_STATEMENT, Tree.Kind.FOR_EACH_STATEMENT, Tree.Kind.TRY_STATEMENT,
        Tree.Kind.EXPRESSION_STATEMENT, Tree.Kind.COMPILATION_UNIT));
    }
  
    @Override
    public void leaveNode(Tree tree) {
      if (hasSemantic()) {
        if (tree.is(Tree.Kind.BLOCK, Tree.Kind.STATIC_INITIALIZER)) {
          BlockTree blockTree = (BlockTree) tree;
          addVariables(blockTree.body());
        } else if (tree.is(Tree.Kind.FOR_STATEMENT)) {
          ForStatementTree forStatementTree = (ForStatementTree) tree;
          addVariables(forStatementTree.initializer());
        } else if (tree.is(Tree.Kind.FOR_EACH_STATEMENT)) {
          ForEachStatement forEachStatement = (ForEachStatement) tree;
          addVariable(forEachStatement.variable());
        } else if (tree.is(Tree.Kind.EXPRESSION_STATEMENT)) {
          leaveExpressionStatement((ExpressionStatementTree) tree);
        } else if (tree.is(Tree.Kind.COMPILATION_UNIT)) {
          checkVariableAssignments();
          variables.clear();
          assignments.clear();
        }
      }
    }
  
    private void leaveExpressionStatement(ExpressionStatementTree expressionStatement) {
      ExpressionTree expression = expressionStatement.expression();
      if (expression.is(ASSIGNMENT_KINDS)) {
        addAssignment(((AssignmentExpressionTree) expression).variable());
      } else if (expression.is(INCREMENT_KINDS)) {
        addAssignment(((UnaryExpressionTree) expression).expression());
      }
    }
  
    private void checkVariableAssignments() {
      for (VariableTree variableTree : variables) {
        Symbol symbol = variableTree.symbol();
        if (!assignments.contains(symbol) && !symbol.isFinal()) {
            reportIssue(variableTree, "This variable \"" + variableTree.simpleName() + "\" must be final.");
        }
      }
    }
  
    public void addVariables(List<StatementTree> statementTrees) {
      for (StatementTree statementTree : statementTrees) {
        if (statementTree.is(Tree.Kind.VARIABLE)) {
          addVariable((VariableTree) statementTree);
        }
      }
    }
  
    private void addVariable(VariableTree variableTree) {
      variables.add(variableTree);
    }
  
    private void addAssignment(ExpressionTree variable) {
      if (variable.is(Tree.Kind.IDENTIFIER)) {
        addAssignment((IdentifierTree) variable);
      }
    }
  
    private void addAssignment(IdentifierTree identifier) {
      Symbol reference = identifier.symbol();
      if (!reference.isUnknown()) {
        assignments.add(reference);
      }
    }
  
  }