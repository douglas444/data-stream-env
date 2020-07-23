package br.com.douglas444.datastreamenv.experiments;


import br.com.douglas444.datastreamenv.Feedback;
import br.com.douglas444.datastreamenv.Util;
import br.com.douglas444.dsframework.DSClassifierExecutor;
import br.com.douglas444.dsframework.DSFileReader;
import br.com.douglas444.minas.ClassificationResult;
import br.com.douglas444.minas.MINASBuilder;
import br.com.douglas444.minas.MINASController;
import br.com.douglas444.minas.MicroCluster;
import br.com.douglas444.minas.interceptor.MINASInterceptor;
import br.com.douglas444.mltk.datastructure.DynamicConfusionMatrix;

import java.io.IOException;

public class MINAS_SYNEDC_NO_FEEDBACK {

    private static final int TEMPORARY_MEMORY_MAX_SIZE = 2000;
    private static final int MINIMUM_CLUSTER_SIZE = 20;
    private static final int WINDOW_SIZE = 4000;
    private static final int MICRO_CLUSTER_LIFESPAN = 4000;
    private static final int SAMPLE_LIFESPAN = 4000;
    private static final int HEATER_CAPACITY = 10000;
    private static final boolean INCREMENTALLY_UPDATE_DECISION_MODEL = false;
    private static final int HEATER_INITIAL_BUFFER_SIZE = 1000;
    private static final int HEATER_NUMBER_OF_CLUSTERS_PER_LABEL = 100;
    private static final int NOVELTY_DETECTION_NUMBER_OF_CLUSTERS = 100;
    private static final long RANDOM_GENERATOR_SEED = 0;

    private static final MINASInterceptor INTERCEPTOR_COLLECTION = new MINASInterceptor();

    public static void main(String[] args) throws IOException {

        INTERCEPTOR_COLLECTION.MICRO_CLUSTER_CLASSIFIER_INTERCEPTOR.define((context) -> {

            if (context.getDecisionModel().isEmpty()) {
                return new ClassificationResult(null, false);
            }

            final MicroCluster closestMicroCluster = context.getMicroClusterTarget()
                    .calculateClosestMicroCluster(context.getDecisionModel());

            final double distance = context.getMicroClusterTarget().distance(closestMicroCluster);

            if (distance <= closestMicroCluster.calculateStandardDeviation() * 1.1) {
                return new ClassificationResult(closestMicroCluster, true);
            }

            return new ClassificationResult(closestMicroCluster, false);

        });

        final MINASBuilder minasBuilder = new MINASBuilder(
                TEMPORARY_MEMORY_MAX_SIZE,
                MINIMUM_CLUSTER_SIZE,
                WINDOW_SIZE,
                MICRO_CLUSTER_LIFESPAN,
                SAMPLE_LIFESPAN,
                HEATER_CAPACITY,
                INCREMENTALLY_UPDATE_DECISION_MODEL,
                HEATER_INITIAL_BUFFER_SIZE,
                HEATER_NUMBER_OF_CLUSTERS_PER_LABEL,
                NOVELTY_DETECTION_NUMBER_OF_CLUSTERS,
                RANDOM_GENERATOR_SEED,
                INTERCEPTOR_COLLECTION);

        final MINASController minasController = minasBuilder.build();

        DSFileReader dsFileReader = new DSFileReader(",", Util.getFileReader("SynEDC20D40Norm_fold1_ini"));
        DSClassifierExecutor.start(minasController, dsFileReader, true, 1000);

        dsFileReader = new DSFileReader(",", Util.getFileReader("SynEDC20D40Norm_fold1_onl"));
        DSClassifierExecutor.start(minasController, dsFileReader, true, 1000);

        DynamicConfusionMatrix dcm = minasController.getDynamicConfusionMatrixString();
        System.out.println("\n" + dcm.toString());

    }


}