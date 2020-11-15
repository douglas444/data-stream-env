package br.com.douglas444.datastreamenv.strategy1;

import br.com.douglas444.datastreamenv.common.Oracle;
import br.com.douglas444.datastreamenv.common.ConceptCategory;
import br.com.douglas444.datastreamenv.common.ExperimentResult;
import br.com.douglas444.datastreamenv.common.InterceptionResult;
import br.com.douglas444.mltk.datastructure.Sample;
import echo.interceptor.context.ClusteredConceptContext;
import echo.mineClass.Miner;

import java.util.*;
import java.util.stream.Collectors;

public class ECHO {

    private final static double BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT = 0.95;
    private final static double BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT = 0.95;

    private final static List<InterceptionResult> interceptionsResults = new ArrayList<>();

    public static void main(String[] args) {

        //Defines the interceptor for the case of novel cluster detection
        Miner.interceptor.NOVEL_CLASS_EMERGENCE.define((context) -> {
            if (!context.getTargetSamples().isEmpty()) {
                process(context, ConceptCategory.NOVELTY);
            }
            context.getDefaultAction().run();
        });

        //Defines the interceptor for the case of known cluster detection
        Miner.interceptor.CLASSIFIER_UPDATE.define((context) -> {
            if (!context.getTargetSamples().isEmpty()) {
                process(context, ConceptCategory.KNOWN);
            }
            context.getDefaultAction().run();
        });

        //Executes ECHO
        Miner.random = new Random(0);
        Miner.main(args);

        //Print experiment result
        System.out.println(new ExperimentResult(interceptionsResults));

    }

    private static void process(final ClusteredConceptContext context, ConceptCategory frameworkPrediction) {

        //The target samples comes from the context as arrays, so here we convert then to Sample objects
        final List<Sample> targetSamples = context.getTargetSamples().stream().map(attributes -> {
            final double[] x = Arrays.copyOfRange(attributes, 0, attributes.length - 1);
            final Integer y = (int) attributes[attributes.length - 1];
            return new Sample(x, y);
        }).collect(Collectors.toList());

        //Here we get the current labels known by the decision model
        final Set<Integer> knownLabels = context.getKnownLabels()
                .stream()
                .map(Double::intValue)
                .collect(Collectors.toSet());

        //Calculate the novelty purity value of the cluster
        final double noveltyPurity = Oracle.noveltyPurity(targetSamples, knownLabels);

        //The real concept category is given by the novelty purity value of the cluster.
        final ConceptCategory realCategory;
        if (noveltyPurity >= 0) {
            realCategory = ConceptCategory.NOVELTY;
        } else {
            realCategory = ConceptCategory.KNOWN;
        }

        //The centroid of the target cluster comes from the context as a array, so here we convert it to a Sample object
        final Sample targetConcept = new Sample(context.getTargetClusterCentroid(), -1);

        //The centroids of the known concepts comes from the context as arrays, so here we convert then to Sample objects
        final List<Sample> knownConcepts = context.getKnownClustersCentroids().stream()
                .map(attributes -> {
                    final double[] x = Arrays.copyOfRange(attributes, 0, attributes.length - 1);
                    final Integer y = (int) attributes[attributes.length - 1];
                    return new Sample(x, y);
                })
                .collect(Collectors.toList());

        //Calculate the indicator prediction
        final ConceptCategory indicatorPrediction = Strategy1.calculateIndicatorPrediction(
                targetConcept,
                knownConcepts,
                BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT,
                BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT);

        //Store the result
        interceptionsResults.add(new InterceptionResult(realCategory, frameworkPrediction, indicatorPrediction));

    }



}
