package FightPredictor.util;

import FightPredictor.FightPredictor;
import com.badlogic.gdx.Gdx;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private static final String TF_MODEL_JAR_RESOURCE_ROOT_DIRECTORY_NAME = "FightPredictorResources/ml/saved_model";
    private static final String FILE_DELIM = "/";

    private static final String TF_MODEL_ROOT_DIRECTORY_NAME = "STSFightPredictor";
    private static final String TF_MODEL_ASSETS_DIRECTORY_NAME = "assets";
    private static final String TF_MODEL_VARIABLES_DIRECTORY_NAME = "variables";

    private static final String TF_MODEL_VAR_DATA_FILE_NAME = "variables.data-00000-of-00001";
    private static final String TF_MODEL_VAR_INDEX_FILE_NAME = "variables.index";
    private static final String TF_MODEL_SAVED_MODEL_FILE_NAME = "saved_model.pb";

    public static Path createTempModelResourceDir() throws IOException {
        Path modelTempRoot = Files.createTempDirectory(TF_MODEL_ROOT_DIRECTORY_NAME).toAbsolutePath();
        FightPredictor.logger.info("Root of model temp direcotry: " + modelTempRoot.toString());

        // Make sub directories for model files
        Path modelAssetsDir = modelTempRoot.resolve(TF_MODEL_ASSETS_DIRECTORY_NAME);
        Files.createDirectory(modelAssetsDir);
        Path modelVarDir = modelTempRoot.resolve(TF_MODEL_VARIABLES_DIRECTORY_NAME);
        Files.createDirectory(modelVarDir);

        modelTempRoot.toFile().deleteOnExit();
        modelAssetsDir.toFile().deleteOnExit();
        modelVarDir.toFile().deleteOnExit();

        // Make temp files and copy data
        String savedModel = TF_MODEL_JAR_RESOURCE_ROOT_DIRECTORY_NAME + FILE_DELIM + TF_MODEL_SAVED_MODEL_FILE_NAME;
        createTempModelFile(savedModel, modelTempRoot.resolve(TF_MODEL_SAVED_MODEL_FILE_NAME).toString());

        String varData = TF_MODEL_JAR_RESOURCE_ROOT_DIRECTORY_NAME + FILE_DELIM + TF_MODEL_VARIABLES_DIRECTORY_NAME + FILE_DELIM + TF_MODEL_VAR_DATA_FILE_NAME;
        createTempModelFile(varData, modelVarDir.resolve(TF_MODEL_VAR_DATA_FILE_NAME).toString());

        String varIndex = TF_MODEL_JAR_RESOURCE_ROOT_DIRECTORY_NAME + FILE_DELIM + TF_MODEL_VARIABLES_DIRECTORY_NAME + FILE_DELIM + TF_MODEL_VAR_INDEX_FILE_NAME;
        createTempModelFile(varIndex, modelVarDir.resolve(TF_MODEL_VAR_INDEX_FILE_NAME).toString());

        return modelTempRoot;
    }

    private static void createTempModelFile(String resource, String filePath) {
        File file = null;

        try {
            InputStream input = Gdx.files.internal(resource).read();
            file = new File(filePath);
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file != null && !file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
    }
}
