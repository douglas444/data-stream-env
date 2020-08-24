package br.com.douglas444.datastreamenv.indicator;

import java.util.Arrays;
import java.util.List;

public class ConceptClassification {

    public enum Type {NOVELTY, KNOWN, NULL}

    private Type correctAnswer;
    private Type algorithmGuess;
    private Type indicatorGuess;
    private double indicatorValue;

    public ConceptClassification(Type correctAnswer, Type algorithmGuess, Type indicatorGuess, double indicatorValue) {
        this.correctAnswer = correctAnswer;
        this.algorithmGuess = algorithmGuess;
        this.indicatorGuess = indicatorGuess;
        this.indicatorValue = indicatorValue;
    }

    public static void printStatistics(final List<ConceptClassification> results) {

        if (results.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int noveltyCount = 0;

        for (ConceptClassification result : results) {
            if (result.getCorrectAnswer() == ConceptClassification.Type.NOVELTY) {
                ++noveltyCount;
            }
        }

        System.out.println("\n\nActual novelties = " + noveltyCount);
        System.out.println("Actual known = " + (results.size() - noveltyCount) + "\n");

        final int[] noveltyDistribution = new int[100];
        final int[] knownDistribution = new int[100];

        Arrays.fill(noveltyDistribution, 0);
        Arrays.fill(knownDistribution, 0);

        for (ConceptClassification result : results) {
            if (result.getCorrectAnswer() == ConceptClassification.Type.NOVELTY) {
                if (result.getIndicatorValue() == 1) {
                    ++noveltyDistribution[99];
                } else {
                    ++noveltyDistribution[(int) (result.getIndicatorValue() * 100)];
                }
            } else {
                if (result.getIndicatorValue() == 1) {
                    ++knownDistribution[99];
                } else {
                    ++knownDistribution[(int) (result.getIndicatorValue() * 100)];
                }
            }
        }

        System.out.println("Bayesian error estimation distribution:\n");
        for (int i = 0; i < 100; ++i) {

            if (noveltyDistribution[i] + knownDistribution[i] == 0) {
                continue;
            }

            System.out.println("[" + (i / (double) 100) + " - " + (i + 1) / (double) 100 + (i < 99 ? ") ->\n" : "] ->\n") +
                    "    \\" + (100 * noveltyDistribution[i] / safeDenominator(noveltyCount)) + "% of novelties total\n" +
                    "    \\" + (100 * knownDistribution[i]   / safeDenominator(results.size() - noveltyCount)) + "% of known total\n" +
                    "    \\" + (100 * noveltyDistribution[i] / safeDenominator(noveltyDistribution[i] + knownDistribution[i])) + "% are novelties\n" +
                    "    \\" + (100 * knownDistribution[i]   / safeDenominator(noveltyDistribution[i] + knownDistribution[i])) + "% are known\n");
        }

        int goodDisagreement = 0;
        int badDisagreement = 0;

        for (ConceptClassification result : results) {
            if (result.getAlgorithmGuess() != result.getIndicatorGuess()
                    && result.getIndicatorGuess() != ConceptClassification.Type.NULL) {
                if (result.getCorrectAnswer() != result.getAlgorithmGuess()) {
                    ++goodDisagreement;
                } else {
                    ++badDisagreement;
                }
            }
        }

        System.out.println("\nGood disagreement = " + (100 * goodDisagreement / safeDenominator(goodDisagreement + badDisagreement)) + "%");
        System.out.println("Bad disagreement = " + (100 * badDisagreement / safeDenominator(goodDisagreement + badDisagreement)) + "%\n");

        int goodAgreement = 0;
        int badAgreement = 0;

        for (ConceptClassification result : results) {
            if (result.getAlgorithmGuess() == result.getIndicatorGuess()
                    && result.getIndicatorGuess() != ConceptClassification.Type.NULL) {
                if (result.getCorrectAnswer() != result.getAlgorithmGuess()) {
                    ++badAgreement;
                } else {
                    ++goodAgreement;
                }
            }
        }

        System.out.println("Good agreement = " + (100 * goodAgreement / safeDenominator(goodAgreement + badAgreement)) + "%");
        System.out.println("Bad agreement = " + (100 * badAgreement / safeDenominator(goodAgreement + badAgreement)) + "%\n");

        int goodNoOpinion = 0;
        int badNoOpinion = 0;

        for (ConceptClassification result : results) {
            if (result.getIndicatorGuess() == ConceptClassification.Type.NULL) {
                if (result.getCorrectAnswer() != result.getAlgorithmGuess()) {
                    ++badNoOpinion;
                } else {
                    ++goodNoOpinion;
                }
            }
        }

        System.out.println("Good no opinion = " + (100 * goodNoOpinion / safeDenominator(goodNoOpinion + badNoOpinion)) + "%");
        System.out.println("Bad no opinion = " + (100 * badNoOpinion / safeDenominator(goodNoOpinion + badNoOpinion)) + "%");
        System.out.println("No opinion rate = " + (100 * (goodNoOpinion + badNoOpinion) / safeDenominator(results.size())) + "%\n");

        int algorithmHits = 0;
        int indicatorHits = 0;

        for (ConceptClassification result : results) {
            if (result.getCorrectAnswer() == result.getAlgorithmGuess()) {
                ++algorithmHits;
            }
            if (result.getCorrectAnswer() == result.getIndicatorGuess()) {
                ++indicatorHits;
            }
        }

        System.out.println("Algorithm acc = " + (100 * algorithmHits / safeDenominator(results.size())) + "%");
        System.out.println("Indicator acc = " + (100 * indicatorHits / safeDenominator(results.size() - (goodNoOpinion + badNoOpinion))) + "%\n");
        int indicatorTrueNovelty = 0;
        int indicatorFalseNovelty = 0;
        int indicatorTrueKnown = 0;
        int indicatorFalseKnown = 0;
        int indicatorNotClassifiedKnown = 0;
        int indicatorNotClassifiedNovelty = 0;

        int algorithmTrueNovelty = 0;
        int algorithmFalseNovelty = 0;
        int algorithmTrueKnown = 0;
        int algorithmFalseKnown = 0;

        for (ConceptClassification result : results) {

            if (result.getCorrectAnswer() == ConceptClassification.Type.NOVELTY) {

                switch (result.getIndicatorGuess()) {
                    case NOVELTY:
                        ++indicatorTrueNovelty;
                        break;
                    case KNOWN:
                        ++indicatorFalseKnown;
                        break;
                    case NULL:
                        ++indicatorNotClassifiedNovelty;
                        break;
                    default:
                        break;
                }

                switch (result.getAlgorithmGuess()) {
                    case NOVELTY:
                        ++algorithmTrueNovelty;
                        break;
                    case KNOWN:
                        ++algorithmFalseKnown;
                        break;
                    default:
                        break;
                }

            } else {

                switch (result.getIndicatorGuess()) {
                    case NOVELTY:
                        ++indicatorFalseNovelty;
                        break;
                    case KNOWN:
                        ++indicatorTrueKnown;
                        break;
                    case NULL:
                        ++indicatorNotClassifiedKnown;
                        break;
                    default:
                        break;
                }

                switch (result.getAlgorithmGuess()) {
                    case NOVELTY:
                        ++algorithmFalseNovelty;
                        break;
                    case KNOWN:
                        ++algorithmTrueKnown;
                        break;
                    default:
                        break;
                }

            }
        }

        System.out.println("Indicator confusion matrix");
        System.out.println("        |NOVELTY|KNOWN  |?      |");
        System.out.println(String.format("|NOVELTY|%7d|%7d|%7d|", indicatorTrueNovelty, indicatorFalseKnown, indicatorNotClassifiedNovelty));
        System.out.println(String.format("|KNOWN  |%7d|%7d|%7d|", indicatorFalseNovelty, indicatorTrueKnown, indicatorNotClassifiedKnown));

        System.out.println("\nAlgorithm confusion matrix");
        System.out.println("        |NOVELTY|KNOWN  |");
        System.out.println(String.format("|NOVELTY|%7d|%7d|", algorithmTrueNovelty, algorithmFalseKnown));
        System.out.println(String.format("|KNOWN  |%7d|%7d|", algorithmFalseNovelty, algorithmTrueKnown));

    }

    private static double safeDenominator(final double value) {
        if (value == 0) {
            return 1;
        } else {
            return value;
        }
    }

    public Type getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Type correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Type getAlgorithmGuess() {
        return algorithmGuess;
    }

    public void setAlgorithmGuess(Type algorithmGuess) {
        this.algorithmGuess = algorithmGuess;
    }

    public Type getIndicatorGuess() {
        return indicatorGuess;
    }

    public void setIndicatorGuess(Type indicatorGuess) {
        this.indicatorGuess = indicatorGuess;
    }

    public double getIndicatorValue() {
        return indicatorValue;
    }

    public void setIndicatorValue(double indicatorValue) {
        this.indicatorValue = indicatorValue;
    }
}
