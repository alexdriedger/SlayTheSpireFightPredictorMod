package FightPredictor.ml;

import FightPredictor.FightPredictor;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.proto.framework.SignatureDef;
import org.tensorflow.proto.framework.TensorInfo;
import org.tensorflow.tools.ndarray.FloatNdArray;
import org.tensorflow.types.TFloat32;
import org.tensorflow.tools.ndarray.StdArrays;

import java.util.List;
import java.util.Map;

public class Model {

    public static final int NUM_FEATURES = 975;

    SavedModelBundle model;
    String inputOp;
    String outputOp;

    /**
     * Load a tensorflow model from a Saved Model Format
     * @param pathToModel absolute path to model root folder
     */
    public Model(String pathToModel) {
        // Load model from path
        this.model = SavedModelBundle.load(pathToModel, "serve");

        // Get input and output operation names
        SignatureDef sig = model.metaGraphDef().getSignatureDefMap().get("serving_default");

        Map<String, TensorInfo> inputMap = sig.getInputsMap();
        Map<String, TensorInfo> outputMap = sig.getOutputsMap();

        if (inputMap.keySet().size() > 1 || outputMap.keySet().size() > 1) {
            String error = "Model has too many inputs or outputs";
            FightPredictor.logger.error(error);
            throw new RuntimeException(error);
        }

        for (String key : inputMap.keySet()) {
            TensorInfo ti = inputMap.get(key);
            this.inputOp = ti.getName();
        }

        for (String key : outputMap.keySet()) {
            TensorInfo ti = outputMap.get(key);
            this.outputOp = ti.getName();
        }

        FightPredictor.logger.info("Initialized tensorflow model");
    }

    public float predict(float[] inputVector) {
        // Convert float[] into a tensor
        float[][] basicMatrix = new float[1][NUM_FEATURES];
        basicMatrix[0] = inputVector;

        FloatNdArray fa = StdArrays.ndCopyOf(basicMatrix);
        Tensor<TFloat32> inputMatrix = TFloat32.tensorOf(fa);

        // Run inference
        try {
            List<Tensor<?>> output = model.session()
                    .runner()
                    .feed(inputOp, inputMatrix)
                    .fetch(outputOp)
                    .run();

            if (output.size() > 1) {
                String error = "Model predicted more than one value";
                FightPredictor.logger.error(error);
                throw new RuntimeException(error);
            }

            // Get prediction
            Tensor<?> t = output.get(0);
            Tensor<TFloat32> tCast = (Tensor<TFloat32>) t;
            return tCast.data().getFloat();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public float[] predict(float[][] inputMatrix) {
        return new float[]{};
    }
}
