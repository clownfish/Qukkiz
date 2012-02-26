package de.xzise.qukkiz.hinter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.questions.AliasedAnswer;

public class ListHinter extends DefaultHinter<ListHinterSettings> {

    private final AliasedAnswer[] answers;
    private final boolean[] visible;
    private final Random random = new Random();
    private final int[] answerIdx;

    public ListHinter(AliasedAnswer[] answers, ListHinterSettings settings) {
        super(settings);
        Collections.shuffle(Arrays.asList(answers));
        this.answers = answers;
        this.visible = new boolean[answers.length];
        this.answerIdx = new int[answers.length];
        Random r = new Random();
        for (int i = 0; i < answers.length; i++) {
            this.answerIdx[i] = r.nextInt(answers[i].visibleAnswers.length);
        }
        Arrays.fill(this.visible, false);
    }

    @Override
    public void nextHint() {
        int invisibleCount = this.getInvisibleCount();
        final int hintedWords =  Math.min(this.getSettings().wordsPerHint, invisibleCount - this.getSettings().minimumMasked);
        for (int j = 0; j < hintedWords; j++) {
            int index = this.random.nextInt(invisibleCount - j);
            for (int i = 0; i < this.visible.length; i++) {
                if (!this.visible[i]) {
                    if (index == 0) {
                        this.visible[i] = true;
                    }
                    index--;
                }
            }
        }
    }

    public int getInvisibleCount() {
        int invisibleCount = 0;
        for (boolean vis : this.visible) {
            if (!vis) {
                invisibleCount++;
            }
        }
        return invisibleCount;
    }

    public void nextHint(String name) {
        for (int i = 0; i < this.answers.length; i++) {
            if (this.answers[i].check(name)) {
                this.visible[i] = true;
            }
        }
    }

    @Override
    public String getHint() {
        StringBuilder builder = new StringBuilder(ChatColor.GREEN.toString());
        for (int i = 0; i < this.answers.length; i++) {
            if (i > 0) {
                builder.append(ChatColor.WHITE + ", " + ChatColor.GREEN);
            }
            if (this.visible[i]) {
                builder.append(this.answers[i].visibleAnswers[this.answerIdx[i]]);
            } else {
                builder.append("****");
            }
        }
        return builder.append(ChatColor.WHITE).toString();
    }

    @Override
    public int getMaximumHints() {
        if (this.getSettings().dynamicHints) {
            return Math.max(this.getInvisibleCount() - this.getSettings().minimumMasked, 0);
        } else {
            return -1;
        }
    }

    public Boolean isCorrect(String string) {
        for (int i = 0; i < this.answers.length; i++) {
            if (this.answers[i].check(string)) {
                return !this.visible[i];
            }
        }
        return null;
    }
}
