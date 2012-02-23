package de.xzise.qukkiz.questions;

import de.xzise.qukkiz.questioner.Questioner;

public interface QuestionInterface {

    /**
     * Returns how much the answer is correct.
     * 
     * @param answer
     *            Tested answer.
     * @return Following values could be returned:
     *         <ul>
     *         <li>null – the answer is incorrect and impossible</li>
     *         <li>{@link Integer#MAX_VALUE}/{@link Integer#MIN_VALUE} – the
     *         answer was incorrect but possible</li>
     *         <li>0 – the answer is perfect.</li>
     *         <li>Otherwise it define the difference to the answer.</li>
     *         </ul>
     */
    Integer testAnswer(String answer);

    String getAnswer();

    Questioner createQuestioner();

    String getQuestion();

    int getMaximumHints();
}
