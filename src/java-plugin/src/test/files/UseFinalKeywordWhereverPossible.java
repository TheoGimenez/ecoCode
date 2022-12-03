package fr.cnumr.java.checks;

class UseFinalKeywordWhereverPossible {
    UseFinalKeywordWhereverPossible(UseFinalKeywordWhereverPossible obj) {

    }


    public void testWithFinalVar() {
        final String test = "Toto tata";

        test.replace("tata", "titi");
    }

    public void testWithNotFinalVar() {
        String test = "Toto tata"; // Noncompliant
    
        test.replace("tata", "titi");
    }
}