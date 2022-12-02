package fr.cnumr.java.checks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

@Rule(key = "AvoidStringReplaceRule")
public class AvoidStringReplaceCheck extends IssuableSubscriptionVisitor {
    @Override
    public List<Kind> nodesToVisit() {
        return Collections.unmodifiableList(Arrays.asList(Kind.METHOD_INVOCATION));
        // return Collections.singletonList(Kind.MEMBER_SELECT); //INSTANCE_OF, IMPORT, METHOD_REFERENCE, VARIABLE
    }
    
    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.METHOD_INVOCATION)) {
            MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
            Symbol symbol = methodInvocation.symbol();

            System.out.print("\t firstToken name =>");
            System.out.println("\t" + methodInvocation.symbol().owner().name());

            if (symbol.isMethodSymbol() &&
                symbol.name().equals("replace")
                && !methodInvocation.symbol().owner().name().equals("StringUtils"))
                 {
                    reportIssue(tree, "Use StringUtils.replace instead of String.replace !");
            }
        }
    }
}