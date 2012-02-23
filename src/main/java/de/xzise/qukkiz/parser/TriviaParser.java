package de.xzise.qukkiz.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.questions.EstimateQuestion;
import de.xzise.qukkiz.questions.ListQuestion;
import de.xzise.qukkiz.questions.MultipleChoiceQuestion;
import de.xzise.qukkiz.questions.QuestionInterface;
import de.xzise.qukkiz.questions.ScrambleQuestion;
import de.xzise.qukkiz.questions.TextQuestion;

/* Reads a default trivia file */
public class TriviaParser implements QuestionParser {

    private final QukkizSettings settings;
    private final XLogger logger;

    public TriviaParser(final QukkizSettings settings, final XLogger logger) {
        this.settings = settings;
        this.logger = logger;
    }

    private static final ImmutableMap<String, TriviaQuestionParser> PARSERS;
    private static final TextParser DEFAULT = new TextParser();

    static {
        PARSERS = ImmutableMap.<String, TriviaQuestionParser>builder().
                put("scramble", new ScrambleParser()).
                put("text", DEFAULT).
                put("estimate", new EstimateParser()).
                put("multiplechoice", new MultipleChoiceParser()).
                put("list", new ListParser()).
                build();
    }

    private static interface TriviaQuestionParser {
        String getName();
        QuestionInterface parse(final String[] segments, final QukkizSettings settings) throws TriviaQuestionParserException;
    }

    private static final class TriviaQuestionParserException extends Exception {

        private static final long serialVersionUID = 5824163525290486079L;

        public TriviaQuestionParserException(final String text) {
            super(text);
        }

        public static TriviaQuestionParserException createNotEnoughtSections() {
            return new TriviaQuestionParserException("Not enough sections");
        }
    }

    private static final class ScrambleParser implements TriviaQuestionParser {
        @Override
        public String getName() {
            return "Scramble";
        }

        @Override
        public QuestionInterface parse(final String[] segments, final QukkizSettings settings) throws TriviaQuestionParserException {
            if (segments.length == 1) {
                return new ScrambleQuestion(segments[0], settings);
            } else {
                throw TriviaQuestionParserException.createNotEnoughtSections();
            }
        }
    }

    private static final class MultipleChoiceParser implements TriviaQuestionParser {
        @Override
        public String getName() {
            return "Multiple choice";
        }

        @Override
        public QuestionInterface parse(String[] segments, QukkizSettings settings) throws TriviaQuestionParserException {
            if (segments.length > 3) {
                String question = segments[0];
                String[] answers = Arrays.copyOfRange(segments, 1, segments.length);
                return new MultipleChoiceQuestion(question, settings, answers);
            } else {
                throw TriviaQuestionParserException.createNotEnoughtSections();
            }
        }
    }

    private static final class EstimateParser implements TriviaQuestionParser {
        @Override
        public String getName() {
            return "Estimate";
        }

        @Override
        public QuestionInterface parse(String[] segments, QukkizSettings settings) throws TriviaQuestionParserException {
            if (segments.length == 2) {
                try {
                    return new EstimateQuestion(segments[0], settings, Integer.parseInt(segments[1]), MinecraftUtil.getFormatWithMinimumDecimals(0, 0));
                } catch (NumberFormatException e) {
                    throw new TriviaQuestionParserException("Bad format in estimate question (unable to parse to int)");
                }
            } else {
                throw TriviaQuestionParserException.createNotEnoughtSections();
            }
        }
    }

    private static final class ListParser implements TriviaQuestionParser {
        @Override
        public String getName() {
            return "List";
        }

        @Override
        public QuestionInterface parse(String[] segments, QukkizSettings settings) throws TriviaQuestionParserException {
            if (segments.length > 2) {
                return new ListQuestion(segments[0], settings, Arrays.copyOfRange(segments, 1, segments.length));
            } else {
                throw TriviaQuestionParserException.createNotEnoughtSections();
            }
        }
    }

    private static final class TextParser implements TriviaQuestionParser {
        @Override
        public String getName() {
            return "Text";
        }

        @Override
        public QuestionInterface parse(String[] segments, QukkizSettings settings) throws TriviaQuestionParserException {
            if (segments.length > 2) {
                return new TextQuestion(segments[0], settings, Arrays.copyOfRange(segments, 1, segments.length));
            } else {
                throw TriviaQuestionParserException.createNotEnoughtSections();
            }
        }
    }

    @Override
    public List<QuestionInterface> getQuestions(File file) {
        List<String> strings = new ArrayList<String>(0);
        try {
            strings = MinecraftUtil.readLines(file);
        } catch (IOException e) {
            logger.warning("Unable to read question file!", e);
        }

        List<QuestionInterface> result = new ArrayList<QuestionInterface>(strings.size());

        for (String string : strings) {
            if (!string.isEmpty()) {
                int comment = string.indexOf('#');
                // Cut off commentary
                if (comment > 0) {
                    string = string.substring(0, comment);
                } else if (comment == 0) {
                    string = "";
                }
                if (!string.matches("\\s*")) {
                    String[] segments = string.split("\\*");
                    if (segments.length > 1) {
                        TriviaQuestionParser parser = PARSERS.get(segments[0].toLowerCase());
                        if (parser == null) {
                            parser = DEFAULT;
                        }
                        QuestionInterface question = null;
                        boolean reported = false;
                        try {
                            question = parser.parse(Arrays.copyOfRange(segments, 1, segments.length), this.settings);
                        } catch (TriviaQuestionParserException e) {
                            this.logger.warning("Bad format in question of type '" + parser.getName() + "' because of '" + e.getMessage() + "': " + string);
                            reported = true;
                        } catch (Exception e) {
                            this.logger.warning("Exception in parsing question of type '" + parser.getName() + "': " + string, e);
                        }
                        if (question == null) {
                            if (!reported) {
                                this.logger.warning("Get no question of type '" + parser.getName() + "': " + string);
                            }
                        } else {
                            result.add(question);
                        }
                    } else {
                        this.logger.warning("Bad format in question: " + string);
                    }
                }
            }
        }

        return result;
    }

}
