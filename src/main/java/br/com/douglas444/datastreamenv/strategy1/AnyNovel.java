package br.com.douglas444.datastreamenv.strategy1;

import anynovel.interceptor.context.ClusteredConceptContext;
import br.com.douglas444.datastreamenv.common.Oracle;
import br.com.douglas444.datastreamenv.common.ConceptCategory;
import br.com.douglas444.datastreamenv.common.ExperimentResult;
import br.com.douglas444.datastreamenv.common.InterceptionResult;
import br.com.douglas444.mltk.datastructure.Sample;
import anynovel.conceptEvolution.AnyNovelLauncher;
import anynovel.conceptEvolution.ExpLauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnyNovel {

    private final static double BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT = 0.95;
    private final static double BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT = 0.95;
    private final static List<InterceptionResult> interceptionsResults = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        //Defines the interceptor for the case of novel segment detection
        AnyNovelLauncher.interceptor.NOVELTY_SEGMENT.define((context) -> {
            if (!context.getTargetSamples().isEmpty()) {
                process(context, ConceptCategory.NOVELTY);
            }
            context.getDefaultAction().run();
        });

        //Defines the interceptor for the case of known segment detection
        AnyNovelLauncher.interceptor.KNOWN_SEGMENT.define((context) -> {
            if (!context.getTargetSamples().isEmpty()) {
                process(context, ConceptCategory.KNOWN);
            }
            context.getDefaultAction().run();
        });

        //Executes anyNovel
        ExpLauncher.main(args);

        //Print experiment result
        System.out.println(new ExperimentResult(interceptionsResults));

    }

    private static void process(final ClusteredConceptContext context, final ConceptCategory frameworkPrediction) {

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

        //Calculate the novelty purity value of the segment
        final double noveltyPurity = Oracle.noveltyPurity(targetSamples, knownLabels);

        //The real concept category is given by the novelty purity value of the segment.
        final ConceptCategory realCategory;
        if (noveltyPurity >= 0) {
            realCategory = ConceptCategory.NOVELTY;
        } else {
            realCategory = ConceptCategory.KNOWN;
        }

        //The centroid of the target segment comes from the context as a array, so here we convert it to a Sample object
        final Sample targetConceptCentroid = new Sample(context.getTargetClusterCentroid(), -1);

        //The centroids of the known concepts comes from the context as arrays, so here we convert then to Sample objects
        final List<Sample> knownConceptsCentroids = context.getKnownClustersCentroids().stream()
                .map(attributes -> {
                    final double[] x = Arrays.copyOfRange(attributes, 0, attributes.length - 1);
                    final Integer y = (int) attributes[attributes.length - 1];
                    return new Sample(x, y);
                })
                .collect(Collectors.toList());

        //Calculate the indicator prediction
        final ConceptCategory indicatorPrediction = Strategy1.calculateIndicatorPrediction(
                targetConceptCentroid,
                knownConceptsCentroids,
                BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT,
                BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT);

        //Store the result
        interceptionsResults.add(new InterceptionResult(realCategory, frameworkPrediction, indicatorPrediction));

    }

}
