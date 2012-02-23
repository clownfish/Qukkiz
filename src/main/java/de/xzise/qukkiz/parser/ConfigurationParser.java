package de.xzise.qukkiz.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.bukkit.util.MemorySectionFromMap;
import de.xzise.qukkiz.QukkizSettings;
import de.xzise.qukkiz.questions.AliasedAnswer;
import de.xzise.qukkiz.questions.EstimateQuestion;
import de.xzise.qukkiz.questions.ListQuestion;
import de.xzise.qukkiz.questions.MultipleChoiceQuestion;
import de.xzise.qukkiz.questions.QuestionInterface;
import de.xzise.qukkiz.questions.ScrambleQuestion;
import de.xzise.qukkiz.questions.TextQuestion;

public class ConfigurationParser implements QuestionParser {

    //@formatter:off
    /*
     * questions:
     *   scramble:
     *     - answer: "World"
     *     - answer: "Foo"
     *   text:
     *     - question: "What is the answer?"
     *       answers:
     *         - fortytwo
     *         - 42
     *     - question: "Who is the captain of the USS Enterprise D in Star Trek?"
     *       # All answers which could be hinted
     *       answers:
     *         - Jean-Luc Picard
     *       # Answers which won't be hinted
     *       aliases:
     *         - Picard
     *   estimate:
     *     - question: "How large is the population of Hamburg, Germany?"
     *       answer: 1798455
     *     - question: "The definition of PI."
     *       answer: 3.1415926535897932384626433
     *   multiplechoice:
     *     - question: "How many degrees make an right angle?"
     *       answers: ninety
     *       aliases:
     *         - 90
     *       wronganswers:
     *         - answer: zero
     *           aliases: [ '0' ]
     *         - answer: onehundreteighty
     *           aliases: [ '180' ]
     *   list:
     *     - question: "List all directions."
     *       # All hintable answers
     *       answers:
     *         - answer: North
     *           aliases: [ 'N' ]
     *         - answer: East
     *           aliases: [ 'E' ]
     *         - answer: South
     *           aliases: [ 'S' ]
     *         - answer: West
     *           aliases: [ 'W' ]
     */
    //@formatter:on

    private static final ImmutableMap<String, ConfigParser> PARSERS;

    static {
        //@formatter:off
        PARSERS = ImmutableMap.<String, ConfigParser>builder().
                    put("scramble", new ScrambleParser()).
                    put("text", new TextParser()).
                    put("estimate", new EstimateParser()).
                    put("multiplechoice", new MultipleChoiceParser()).
                    put("list", new ListParser()).
                    build();
        //@formatter:on
    }

    private static interface ConfigParser {
        String getName();
        QuestionInterface parse(MemorySection section, final QukkizSettings settings);
    }

    private static class ScrambleParser implements ConfigParser {
        @Override
        public String getName() {
            return "Scramble";
        }

        @Override
        public QuestionInterface parse(final MemorySection section, final QukkizSettings settings) {
            String word = section.getString("answer");
            return new ScrambleQuestion(word, settings);
        }
    }

    private static class TextParser implements ConfigParser {
        @Override
        public String getName() {
            return "Text";
        }

        @Override
        public QuestionInterface parse(final MemorySection section, final QukkizSettings settings) {
            final String question = section.getString("question");
            if (question != null) {
                Set<String> answers = Sets.newHashSet();
                for (Object answerObj : section.getList("answers")) {
                    answers.add(answerObj.toString().toLowerCase());
                }
                Set<String> alternatives = Sets.newHashSet();
                for (Object alternativeObj : section.getList("aliases")) {
                    final String alternative = alternativeObj.toString().toLowerCase();
                    if (!answers.contains(alternative)) {
                        alternatives.add(alternative);
                    }
                }
                return new TextQuestion(question, settings, answers.toArray(new String[0]), alternatives.toArray(new String[0]));
            } else {
                return null;
            }
        }
    }

    private static class EstimateParser implements ConfigParser {
        @Override
        public String getName() {
            return "Estimate";
        }

        @Override
        public QuestionInterface parse(final MemorySection section, final QukkizSettings settings) {
            final String question = section.getString("question");
            if (section.isInt("answer") || section.isLong("answer") || section.isDouble("answer")) {
                final double answer = section.getDouble("answer");
                final DecimalFormat format;
                if (section.isSet("format")) {
                    format = new DecimalFormat(section.getString("format"));
                } else if (section.isSet("minimum") || section.isSet("maximum")) {
                    final int minimum = section.getInt("minimum", 1);
                    final int maximum = section.getInt("maximum", 3);
                    format = MinecraftUtil.getFormatWithMinimumDecimals(minimum, maximum);
                } else if (section.isDouble("answer")) {
                    format = MinecraftUtil.getFormatWithMinimumDecimals(1, 3);
                } else {
                    format = MinecraftUtil.getFormatWithMinimumDecimals(0, 0);
                }
                return new EstimateQuestion(question, settings, answer, format);
            } else {
                return null;
            }
        }
    }

    private static class MultipleChoiceParser implements ConfigParser {
        @Override
        public String getName() {
            return "Multiple choice";
        }

        @Override
        public QuestionInterface parse(final MemorySection section, final QukkizSettings settings) {
            final String question = section.getString("question");
            if (question != null) {
                final AliasedAnswer[] wrongAnswers = getAliasedAnswers(section, "wronganswers");
                final List<String> visibleAnswers = getStringList(section, "answers");
                final List<String> aliases = getStringList(section, "aliases");
                final AliasedAnswer correctAnswer = new AliasedAnswer(visibleAnswers.toArray(new String[0]), aliases.toArray(new String[0]));
                return new MultipleChoiceQuestion(question, settings, MinecraftUtil.concat(correctAnswer, wrongAnswers));
            } else {
                return null;
            }
        }
    }

    private static class ListParser implements ConfigParser {
        @Override
        public String getName() {
            return "List";
        }

        @Override
        public QuestionInterface parse(final MemorySection section, final QukkizSettings settings) {
            String question = section.getString("question");
            if (question != null) {
                AliasedAnswer[] aliasedAnswers = getAliasedAnswers(section, "answers");
                return new ListQuestion(question, settings, aliasedAnswers);
            } else {
                return null;
            }
        }
    }

    private static List<String> getStringList(final ConfigurationSection section, final String path) {
        if (section.isList(path)) {
            return section.getStringList(path);
        } else {
            List<String> list = new ArrayList<String>(1);
            if (section.isString(path)) {
                list.add(section.getString(path));
            }
            return list;
        }
    }

    private static AliasedAnswer[] getAliasedAnswers(final MemorySection section, final String path) {
        final List<? extends MemorySection> answerSections = MemorySectionFromMap.getSectionList(section, path);
        final List<AliasedAnswer> answers = new ArrayList<AliasedAnswer>(answerSections.size());
        for (int i = 0; i < answerSections.size(); i++) {
            MemorySection s = answerSections.get(i);
            if (s.isString("answer")) {
                final List<String> visibleAnswers = getStringList(s, "answers");
                final List<String> aliases = getStringList(s, "aliases");
                answers.add(new AliasedAnswer(visibleAnswers.toArray(new String[0]), aliases.toArray(new String[0])));
            }
        }
        return answers.toArray(new AliasedAnswer[0]);
    }

    private final QukkizSettings settings;
    private final XLogger logger;

    public ConfigurationParser(final QukkizSettings settings, final XLogger logger) {
        this.settings = settings;
        this.logger = logger;
    }

    @Override
    public List<QuestionInterface> getQuestions(File file) {

        FileConfiguration config = new YamlConfiguration();
        boolean valid = false;
        try {
            config.load(file);
            valid = true;
        } catch (FileNotFoundException e) {
            this.logger.warning("Questions file not found '" + file.getAbsolutePath() + "'!", e);
        } catch (IOException e) {
            this.logger.warning("Unable to read questions file '" + file.getAbsolutePath() + "'!", e);
        } catch (InvalidConfigurationException e) {
            this.logger.warning("Invalid configuration in questions file '" + file.getAbsolutePath() + "'!", e);
        }

        List<QuestionInterface> questions = new ArrayList<QuestionInterface>();
        if (valid) {
            ConfigurationSection questionRoot = config.getConfigurationSection("questions");
            for (Entry<String, ConfigParser> parser : PARSERS.entrySet()) {
                List<Map<String, Object>> l = questionRoot.getMapList(parser.getKey());
                for (Map<String, Object> map : l) {
                    QuestionInterface question = null;
                    boolean reported = false;
                    try {
                        question = parser.getValue().parse(new MemorySectionFromMap(map), this.settings);
                    } catch (Exception e) {
                        this.logger.warning("Exception in parsing question of type '" + parser.getValue().getName() + "'!", e);
                        reported = true;
                    }
                    if (question == null) {
                        if (!reported) {
                            this.logger.warning("Get no question of type '" + parser.getValue().getName() + "'!");
                        }
                    } else {
                        questions.add(question);
                    }
                }
            }
        }

        return questions;
    }

}
