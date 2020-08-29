package br.com.douglas444.datastreamenv.experiment.echo;

import br.com.douglas444.datastreamenv.Oracle;
import br.com.douglas444.datastreamenv.indicator.BayesianErrorEstimationIndicator;
import br.com.douglas444.datastreamenv.indicator.ConceptClassification;
import br.com.douglas444.mltk.datastructure.Sample;
import interceptor.context.ClusteredConceptContext;
import mineClass.Miner;

import java.util.*;
import java.util.stream.Collectors;

public class ECHOBayesianErrorEstimationExperiment {

    private final static double BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT = 0.95;
    private final static double BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT = 0.95;

    private final static List<ConceptClassification> results = new ArrayList<>();

    public static void main(String[] args) {

        Miner.interceptor.NOVEL_CLASS_EMERGENCE.define((context) -> {
            if (!context.getTargetSamples().isEmpty()) {
                results.add(generateResult(context, ConceptClassification.Type.NOVELTY));
            }
            context.getDefaultAction().run();
        });

        Miner.interceptor.CLASSIFIER_UPDATE.define((context) -> {
            if (!context.getTargetSamples().isEmpty()) {
                results.add(generateResult(context, ConceptClassification.Type.KNOWN));
            }
            context.getDefaultAction().run();
        });

        Miner.random = new Random(0);
        Miner.main(args);

        ConceptClassification.printStatistics(results);

    }

    private static ConceptClassification generateResult(final ClusteredConceptContext context,
                                                        ConceptClassification.Type algorithmGuess) {

        final double noveltyPurity = Oracle.noveltyPurity(context);
        final ConceptClassification.Type correctAnswer;

        if (noveltyPurity >= 0) {
            correctAnswer = ConceptClassification.Type.NOVELTY;
        } else {
            correctAnswer = ConceptClassification.Type.KNOWN;
        }

        final double bayesianErrorEstimation = estimateBayesError(context.getTargetClusterCentroid(),
                context.getKnownClustersCentroids());

        final ConceptClassification.Type indicatorGuess = BayesianErrorEstimationIndicator.calculateIndicatorGuess(
                bayesianErrorEstimation,
                BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT,
                BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT);

        return new ConceptClassification(correctAnswer, algorithmGuess, indicatorGuess,
                bayesianErrorEstimation);

    }

    private static double estimateBayesError(final double[] targetCentroid,
                                             final List<double[]> knownClustersCentroids) {

        final Sample targetConcept = new Sample(targetCentroid, -1);

        final List<Sample> knownConcepts = knownClustersCentroids.stream()
                .map(attributes -> {
                    final double[] x = Arrays.copyOfRange(attributes, 0, attributes.length - 1);
                    final Integer y = (int) attributes[attributes.length - 1];
                    return new Sample(x, y);
                })
                .collect(Collectors.toList());

        if (knownConcepts.isEmpty()) {
            return 1;
        }

        return BayesianErrorEstimationIndicator.estimateBayesError(targetConcept, knownConcepts);
    }


}
