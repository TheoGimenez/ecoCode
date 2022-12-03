package fr.cnumr.java.checks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

@Rule(
        key = "AvoidStringReplaceRule",
        name = "Developpement",
        description = AvoidStringReplaceCheck.MESSAGERULE,
        priority = Priority.MINOR,
        tags = {"bug"})
public class AvoidStringReplaceCheck extends IssuableSubscriptionVisitor {

    protected static final String  MESSAGERULE = "Use StringUtils.replace instead of String.replace !";

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

            if (symbol.isMethodSymbol() &&
                symbol.name().equals("replace")
                && methodInvocation.symbol().owner().name().equals("String"))
                 {
                    reportIssue(tree, MESSAGERULE);
            }
        }
    }
}