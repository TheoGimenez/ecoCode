package fr.cnumr.java.checks;
 
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

public class AvoidStringReplaceCheckTest {
    @Test
    void test() {
      CheckVerifier.
      newVerifier()
        .onFile("src/test/files/AvoidStringReplaceCheck.java")
        .withCheck(new AvoidStringReplaceCheck())
        .verifyIssues();
    }
}