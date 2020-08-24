package br.com.douglas444.datastreamenv;

import br.com.douglas444.minas.MicroCluster;
import br.com.douglas444.minas.MicroClusterCategory;
import br.com.douglas444.minas.interceptor.context.NoveltyDetectionContext;
import br.com.douglas444.mltk.datastructure.Sample;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Oracle {

    public static Integer getLabelOf(final Sample sample) {
        return sample.getY();
    }

    public static boolean isNovelty(final Sample sample, final Set<Integer> knownLabels) {
        final Integer label = getLabelOf(sample);
        return !knownLabels.contains(label);
    }

    public static double noveltyPurity(NoveltyDetectionContext context) {

        final List<Sample> samples = context.getTargetSamples();

        if (samples.isEmpty()) {
            throw new IllegalArgumentException();
        }

        final Set<Integer> knownLabels = context.getDecisionModelMicroClusters()
                .stream()
                .filter(microCluster -> microCluster.getMicroClusterCategory() != MicroClusterCategory.NOVELTY)
                .map(MicroCluster::getLabel)
                .collect(Collectors.toSet());

        final int sentence = samples.stream()
                .map(sample -> isNovelty(sample, knownLabels))
                .map(isNovel -> isNovel ? 1 : -1)
                .reduce(0, Integer::sum);

        return sentence / (double) samples.size();

    }
}
