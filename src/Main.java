import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) throws IOException {
        NeuralNetwork neuralNetwork1 = new NeuralNetwork(12 , "Input1");


        neuralNetwork1.run();

        neuralNetwork1.displayLayer(0);
        neuralNetwork1.displayLayer(1);

        //NetworkTrainer trainer = new NetworkTrainer();
        //Sample req = trainer.getSample(0);
        //Sample.displayArray(req.getArray("r"));
    }
}
