package br.com.douglas444.datastreamenv.experiment;


import br.com.douglas444.datastreamenv.*;
import br.com.douglas444.datastreamenv.indicator.BayesianErrorEstimationIndicator;
import br.com.douglas444.datastreamenv.indicator.ConceptClassification;
import br.com.douglas444.datastreamenv.util.FileUtil;
import br.com.douglas444.dsframework.DSClassifierExecutor;
import br.com.douglas444.dsframework.DSFileReader;
import br.com.douglas444.minas.MINASBuilder;
import br.com.douglas444.minas.MINASController;
import br.com.douglas444.minas.MicroCluster;
import br.com.douglas444.minas.MicroClusterCategory;
import br.com.douglas444.minas.interceptor.MINASInterceptor;
import br.com.douglas444.minas.interceptor.context.NoveltyDetectionContext;
import br.com.douglas444.mltk.datastructure.Sample;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MINASBayesianErrorEstimationExperiment {

    private final static double BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT = 0.95;
    private final static double BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT = 0.95;

    private final static List<ConceptClassification> results = new ArrayList<>();

    final static Consumer<NoveltyDetectionContext> consumerForExplainedCase = (context) -> {

        final ConceptClassification.Type correctAnswer;
        final ConceptClassification.Type algorithmGuess;

        final double noveltyPurity = Oracle.noveltyPurity(context);

        if (noveltyPurity >= 0) {
            correctAnswer = ConceptClassification.Type.NOVELTY;
        } else {
            correctAnswer = ConceptClassification.Type.KNOWN;
        }

        if (context.getClosestMicroCluster().getMicroClusterCategory() == MicroClusterCategory.NOVELTY) {
            algorithmGuess = ConceptClassification.Type.NOVELTY;
        } else {
            algorithmGuess = ConceptClassification.Type.KNOWN;
        }

        final double bayesianErrorEstimation = estimateBayesError(context.getTargetMicroCluster(),
                context.getDecisionModelMicroClusters());

        final ConceptClassification.Type indicatorGuess = BayesianErrorEstimationIndicator.calculateIndicatorGuess(
                bayesianErrorEstimation,
                BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT,
                BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT);

        final ConceptClassification result = new ConceptClassification(correctAnswer, algorithmGuess,
                indicatorGuess, bayesianErrorEstimation);

        results.add(result);

        context.getDefaultAction().run();

    };

    final static Consumer<NoveltyDetectionContext> consumerForUnexplainedCase = (context) -> {

        final ConceptClassification.Type correctAnswer;

        final double noveltyPurity = Oracle.noveltyPurity(context);

        if (noveltyPurity >= 0) {
            correctAnswer = ConceptClassification.Type.NOVELTY;
        } else {
            correctAnswer = ConceptClassification.Type.KNOWN;
        }

        final double bayesianErrorEstimation = estimateBayesError(context.getTargetMicroCluster(),
                context.getDecisionModelMicroClusters());

        final ConceptClassification.Type indicatorGuess = BayesianErrorEstimationIndicator.calculateIndicatorGuess(
                bayesianErrorEstimation,
                BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT,
                BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT);

        final ConceptClassification result = new ConceptClassification(correctAnswer,
                ConceptClassification.Type.NOVELTY, indicatorGuess, bayesianErrorEstimation);

        results.add(result);

        context.getDefaultAction().run();

    };

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        if (args.length == 0) {
            System.out.println("Missing parameter MINAS configuration file path");
            System.exit(1);
        }

        if (args.length < 2) {
            System.out.println("Missing parameter stream source files path");
            System.exit(1);
        }

        final String configurationFile = args[0];
        final String[] files = Arrays.copyOfRange(args, 1, args.length);
        final DSFileReader[] fileReaders = new DSFileReader[files.length];

        for (int i = 0; i < files.length; i++) {
            fileReaders[i] = new DSFileReader(",", FileUtil.getFileReader(files[i]));
        }

        final MINASInterceptor interceptor = new MINASInterceptor();
        interceptor.MICRO_CLUSTER_EXPLAINED.define(consumerForExplainedCase);
        interceptor.MICRO_CLUSTER_EXPLAINED_BY_ASLEEP.define(consumerForExplainedCase);
        interceptor.MICRO_CLUSTER_UNEXPLAINED.define(consumerForUnexplainedCase);

        final MINASBuilder minasBuilder = new MINASBuilder(configurationFile, interceptor);
        final MINASController minasController = minasBuilder.build();
        DSClassifierExecutor.start(minasController, true, 1000, fileReaders);

        minasController.getDynamicConfusionMatrix().print();
        ConceptClassification.printStatistics(results);

    }

    private static double estimateBayesError(final MicroCluster targetMicroCluster,
                                             final List<MicroCluster> decisionModelMicroClusters) {

        final Sample targetConcept = targetMicroCluster.calculateCentroid();
        final List<Sample> knownConcepts = decisionModelMicroClusters.stream()
                .filter(microCluster -> microCluster.getMicroClusterCategory() == MicroClusterCategory.KNOWN)
                .map(MicroCluster::calculateCentroid)
                .collect(Collectors.toList());

        if (knownConcepts.isEmpty()) {
            return 1;
        }

        return BayesianErrorEstimationIndicator.estimateBayesError(targetConcept, knownConcepts);
    }

}