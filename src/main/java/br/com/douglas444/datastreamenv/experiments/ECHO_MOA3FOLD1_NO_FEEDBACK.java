package br.com.douglas444.datastreamenv.experiments;


import br.com.douglas444.datastreamenv.Util;
import br.com.douglas444.dsframework.DSClassifierExecutor;
import br.com.douglas444.dsframework.DSFileReader;
import br.com.douglas444.echo.ECHOBuilder;
import br.com.douglas444.echo.ECHOController;
import br.com.douglas444.mltk.datastructure.DynamicConfusionMatrix;

import java.io.IOException;

public class ECHO_MOA3FOLD1_NO_FEEDBACK {

    private static final int Q = 400;
    private static final int K = 50;
    private static final double GAMMA = 0.5;
    private static final double SENSITIVITY = 0.001;
    private static final double CONFIDENCE_THRESHOLD = 0.6;
    private static final double ACTIVE_LEARNING_THRESHOLD = 0.5;
    private static final int FILTERED_OUTLIER_BUFFER_MAX_SIZE = 2000;
    private static final int CONFIDENCE_WINDOW_MAX_SIZE = 1000;
    private static final int ENSEMBLE_SIZE = 5;
    private static final int RANDOM_GENERATOR_SEED = 0;
    private static final int CHUNK_SIZE = 2000;

    public static void main(String[] args) throws IOException {

        final ECHOBuilder echoBuilder = new ECHOBuilder(
                Q,
                K,
                GAMMA,
                SENSITIVITY,
                CONFIDENCE_THRESHOLD,
                ACTIVE_LEARNING_THRESHOLD,
                FILTERED_OUTLIER_BUFFER_MAX_SIZE,
                CONFIDENCE_WINDOW_MAX_SIZE,
                ENSEMBLE_SIZE,
                RANDOM_GENERATOR_SEED,
                CHUNK_SIZE,
                null);

        final ECHOController echoController = echoBuilder.build();


        DSFileReader dsFileReader = new DSFileReader(",", Util.getFileReader("MOA3_fold1_ini"));
        DSClassifierExecutor.start(echoController, dsFileReader, true, 1000);

        dsFileReader = new DSFileReader(",", Util.getFileReader("MOA3_fold1_onl"));
        DSClassifierExecutor.start(echoController, dsFileReader, true, 1000);

        DynamicConfusionMatrix dcm = echoController.getDynamicConfusionMatrix();
        System.out.println("\n" + dcm.toString());

    }
}
