package br.com.douglas444.datastreamenv.strategy1;


import br.com.douglas444.datastreamenv.common.ConceptCategory;
import br.com.douglas444.datastreamenv.common.ExperimentResult;
import br.com.douglas444.datastreamenv.common.InterceptionResult;
import br.com.douglas444.datastreamenv.common.Oracle;
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
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MINAS {

    private final static double BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT = 0.95;
    private final static double BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT = 0.95;

    private final static List<InterceptionResult> interceptionsResults = new ArrayList<>();

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

        //Sets interceptors
        final MINASInterceptor interceptor = new MINASInterceptor();
        interceptor.MICRO_CLUSTER_EXPLAINED.define(consumerForExplainedCase);
        interceptor.MICRO_CLUSTER_EXPLAINED_BY_ASLEEP.define(consumerForExplainedCase);
        interceptor.MICRO_CLUSTER_UNEXPLAINED.define(consumerForUnexplainedCase);

        //Instantiates MINAS and starts DSClassifierExecutor
        final MINASBuilder minasBuilder = new MINASBuilder(configurationFile, interceptor);
        final MINASController minasController = minasBuilder.build();
        DSClassifierExecutor.start(minasController, true, 1000, fileReaders);

        //Print MINAS result
        minasController.getDynamicConfusionMatrix().print();

        //Print experiment results
        System.out.println(new ExperimentResult(interceptionsResults));

    }

    final static Consumer<NoveltyDetectionContext> consumerForExplainedCase = (context) -> {

        final ConceptCategory frameworkPrediction;
        if (context.getClosestMicroCluster().getMicroClusterCategory() == MicroClusterCategory.NOVELTY) {
            frameworkPrediction = ConceptCategory.NOVELTY;
        } else {
            frameworkPrediction = ConceptCategory.KNOWN;
        }

        process(context, frameworkPrediction);
        context.getDefaultAction().run();

    };

    final static Consumer<NoveltyDetectionContext> consumerForUnexplainedCase = (context) -> {
        process(context, ConceptCategory.NOVELTY);
        context.getDefaultAction().run();
    };

    private static void process(final NoveltyDetectionContext context, ConceptCategory frameworkPrediction) {

        //Here we get the current labels known by the decision model
        final Set<Integer> knownLabels = context.getDecisionModelMicroClusters()
                .stream()
                .filter(microCluster -> microCluster.getMicroClusterCategory() != MicroClusterCategory.NOVELTY)
                .map(MicroCluster::getLabel)
                .collect(Collectors.toSet());

        //Calculate the novelty purity value of the cluster
        final double noveltyPurity = Oracle.noveltyPurity(context.getTargetSamples(), knownLabels);

        //The real concept category is given by the novelty purity value of the segment.
        final ConceptCategory realCategory;
        if (noveltyPurity >= 0) {
            realCategory = ConceptCategory.NOVELTY;
        } else {
            realCategory = ConceptCategory.KNOWN;
        }

        //Calculate the target concept centroid
        final Sample targetConceptCentroid = context.getTargetMicroCluster().calculateCentroid();

        //Calculate the centroid of the concepts known by the decision model
        final List<Sample> knownConceptsCentroids = context.getDecisionModelMicroClusters().stream()
                .filter(microCluster -> microCluster.getMicroClusterCategory() == MicroClusterCategory.KNOWN)
                .map(MicroCluster::calculateCentroid)
                .collect(Collectors.toList());

        //Calculate the indicator prediction
        final ConceptCategory indicatorPrediction = Strategy1.calculateIndicatorPrediction(
                targetConceptCentroid,
                knownConceptsCentroids,
                BAYESIAN_ERROR_ESTIMATION_LOWER_LIMIT,
                BAYESIAN_ERROR_ESTIMATION_UPPER_LIMIT);

        //Store the result
        final InterceptionResult result = new InterceptionResult(realCategory, frameworkPrediction, indicatorPrediction);
        interceptionsResults.add(result);

    }

}