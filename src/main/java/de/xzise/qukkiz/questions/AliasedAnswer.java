package de.xzise.qukkiz.questions;

import de.xzise.EqualCheck;
import de.xzise.MinecraftUtil;
import de.xzise.collections.ArrayReferenceList;

public final class AliasedAnswer {
    public final String[] visibleAnswers;
    public final String[] aliases;

    public AliasedAnswer(final String visibleAnswer) {
        this(new String[] { visibleAnswer }, new String[0]);
    }

    public AliasedAnswer(final String[] visibleAnswer, final String[] aliases) {
        this.visibleAnswers = visibleAnswer;
        this.aliases = aliases;
    }

    public boolean check(final String text) {
        return ArrayReferenceList.contains(text, this.visibleAnswers, EqualCheck.STRING_IGNORE_CASE_EQUAL_CHECKER) || ArrayReferenceList.contains(text, aliases, EqualCheck.STRING_IGNORE_CASE_EQUAL_CHECKER);
    }

    public static AliasedAnswer[] createArray(final String... answers) {
        AliasedAnswer[] answerObjects = new AliasedAnswer[answers.length];
        for (int i = 0; i < answers.length; i++) {
            answerObjects[i] = new AliasedAnswer(answers[i]);
        }
        return answerObjects;
    }

    public static String[] getVisibleAnswers(final AliasedAnswer... answers) {
        final String[] answerHints = new String[answers.length];
        for (int i = 0; i < answers.length; i++) {
            answerHints[i] = MinecraftUtil.getRandom(answers[i].visibleAnswers);
        }
        return answerHints;
    }
}