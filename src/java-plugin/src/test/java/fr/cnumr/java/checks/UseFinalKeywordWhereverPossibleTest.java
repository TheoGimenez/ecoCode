package fr.cnumr.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

public class UseFinalKeywordWhereverPossibleTest {
    @Test
    void test() {
        CheckVerifier
                .newVerifier()
                .onFile("src/test/files/UseFinalKeywordWhereverPossible.java")
                .withCheck(new UseFinalKeywordCheck())
                .verifyIssues();
    }
}
