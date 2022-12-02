package fr.cnumr.java.checks;

import org.apache.commons.lang3.StringUtils;

public class AvoidStringReplaceCheck { 

    AvoidStringReplaceCheck(AvoidStringReplaceCheck mc) {}
    
    public void testWithStringReplace() {
        String test = "Toto tata";

        test.replace("tata", "titi"); // Noncompliant
    }

    public void testWithStringUtilsReplace() {
        String test = "Toto tata";
    
        StringUtils.replace(test, "tata", "titi");
    }
}
