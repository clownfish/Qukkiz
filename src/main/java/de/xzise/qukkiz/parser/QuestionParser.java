package de.xzise.qukkiz.parser;

import java.io.File;
import java.util.List;

import de.xzise.qukkiz.questions.QuestionInterface;

public interface QuestionParser {

    List<QuestionInterface> getQuestions(File file);
}
