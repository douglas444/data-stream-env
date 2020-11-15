package br.com.douglas444.datastreamenv.common;

import br.com.douglas444.mltk.datastructure.Sample;

import java.util.List;
import java.util.Set;

public class Oracle {

    public static Integer getLabelOf(final Sample sample) {
        return sample.getY();
    }

    public static boolean isNovelty(final Sample sample, final Set<Integer> knownLabels) {
        final Integer label = getLabelOf(sample);
        return !knownLabels.contains(label);
    }

    public static double noveltyPurity(final List<Sample> samples, final Set<Integer> knownLabels) {

        if (samples.isEmpty()) {
            throw new IllegalArgumentException();
        }

        final int sentence = samples.stream()
                .map(sample -> isNovelty(sample, knownLabels))
                .map(isNovel -> isNovel ? 1 : -1)
                .reduce(0, Integer::sum);

        return sentence / (double) samples.size();

    }

}
