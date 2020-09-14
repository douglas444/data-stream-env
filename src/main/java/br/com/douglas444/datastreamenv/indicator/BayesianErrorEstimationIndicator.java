package br.com.douglas444.datastreamenv.indicator;

import br.com.douglas444.mltk.datastructure.Sample;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BayesianErrorEstimationIndicator {

    public static ConceptClassification.Type calculateIndicatorGuess(final double bayesianErrorEstimation,
                                                                     final double lowerLimit,
                                                                     final double upperLimit) {

        if (bayesianErrorEstimation > upperLimit) {
            return ConceptClassification.Type.NOVELTY;
        } else if (bayesianErrorEstimation < lowerLimit) {
            return ConceptClassification.Type.KNOWN;
        } else {
            return ConceptClassification.Type.NULL;
        }

    }

    public static double estimateBayesError(final Sample target, final List<Sample> targetConcepts) {

        if (targetConcepts.isEmpty()) {
            return 1;
        }

        final HashMap<Integer, List<Sample>> centroidsByLabel = new HashMap<>();

        targetConcepts.forEach(centroid -> {
            centroidsByLabel.putIfAbsent(centroid.getY(), new ArrayList<>());
            centroidsByLabel.get(centroid.getY()).add(centroid);
        });

        final HashMap<Integer, Sample> closestCentroidByLabel = new HashMap<>();

        centroidsByLabel.forEach((label, centroids) -> {

            final Sample closestCentroid = centroids
                    .stream()
                    .min(Comparator.comparing((Sample sample) -> sample.distance(target)))
                    .orElse(centroids.get(0));

            closestCentroidByLabel.put(label, closestCentroid);
        });

        final double n = 1.0 / closestCentroidByLabel
                .values()
                .stream()
                .map(centroid -> centroid.distance(target))
                .min(Double::compare)
                .orElse(0.0);

        if (Double.isInfinite(n)) {
            return 0;
        }

        if (centroidsByLabel.size() == 1) {
            return 1;
        }

        final double d = closestCentroidByLabel
                .values()
                .stream()
                .map(centroid -> 1.0 / (centroid.distance(target)))
                .reduce(0.0, Double::sum);

        double max = 1 - 1 / (double) closestCentroidByLabel.keySet().size();

        final double result = (1 - n / d) / max;

        if (Double.isNaN(result)) {
            throw new IllegalStateException("Result of estimateBayesError is not a number");
        }

        return result;
    }

}
