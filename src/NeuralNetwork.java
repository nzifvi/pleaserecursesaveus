import java.io.IOException;
import java.util.Arrays;

public class NeuralNetwork {
    private Layer[] layers;
    private boolean isDataLoaded = true;
    private NetworkTrainer trainer = new NetworkTrainer();
    NetworkFileHandler networkFileHandler = new NetworkFileHandler();
    NetworkDataVisualiser networkDataVisualiser = new NetworkDataVisualiser();

    public NeuralNetwork(final int layerAmount, final String targetInput) throws IOException {
        double[][][] targetInputArray = NetworkFileHandler.loadInput(
                NetworkFileHandler.loadFile("resources/Inputs/" + targetInput),
                3,
                500,
                500
        );

        layers = new Layer[layerAmount];
        for(int i = 0; i < layers.length; i++){
            if(i == 0){ //Input layer

                layers[i] = new Layer();
                layers[i].setInputActivationMatrix(targetInputArray); //Load input into first layer

            }else{
                layers[i] = new Layer();
            }
        }
    }

    public void run(){
        if(!isDataLoaded){
            System.out.println("    ! Cannot run network without data loaded");
        }else{
            for(int i = 0; i < layers.length; i++){
                System.out.println("\n --> ITERATION: " + i + "\n");
                if(i > 0){
                    layers[i].setInputActivationMatrix(layers[i - 1].getOutputActivationMatrix());
                }
                layers[i].beginComputation(i);
            }
            performBackPropagation();
        }
    }

    private void performBackPropagation(){
        System.out.println("        |- Performing Back propagation for Layers");
        /*
        * ONCE BACK PROP SET UP, REPLACE INPUTS OF UPDATES WITH THE CALCULATED ARRAYS. CURRENT INPUTS ARE TO ALLOW
        * COMPILATION FOR TESTING
        */
        for(int i = 0; i < layers.length; i++){
            NetworkFileHandler.Request req;

            req = layers[i].updateLayerFilters(this.layers[i].getFilters());
            if(req != null){
                networkFileHandler.enqueue(req);
            }
            req = layers[i].updateLayerBiases(this.layers[i].getBiases());
            if(req != null){
                networkFileHandler.enqueue(req);
            }
        }

        networkFileHandler.processQueue();
    }

    public void displayLayer(final int index){
        if(index < 0 || index >= this.layers.length){
            throw new ArrayIndexOutOfBoundsException("No such layer at index " + index + " exists");
        }else{
            String layerTitle;
            if(index == 0){
                layerTitle = "INPUT LAYER";
            }else if(index == this.layers.length - 1){
                layerTitle = "OUTPUT LAYER";
            }else{
                layerTitle = "LAYER " + index;
            }

            System.out.println("\n" + layerTitle + " DETAILS:");
            System.out.println("  BIASES:");
            System.out.println("  |- -> Biases Depth: " + this.layers[index].getBiasesSize());

            System.out.println("\n  FILTERS:");
            System.out.println("  |- -> Number of Filters: " + this.layers[index].getFiltersSize());
            System.out.println("  |- -> Filters Depth: " + this.layers[index].getFiltersDepthSize(0));
            System.out.println("  |- -> Filters Height: " + this.layers[index].getFiltersHeightSize(0, 0));
            System.out.println("  |- -> Filters Width: " + this.layers[index].getFiltersWidthSize(0,0,0));

            System.out.println("\n  INPUT & OUTPUT:");
            System.out.println("  |- -> Input Depth = " + this.layers[index].getInputActivationDepth() + ", Output Depth = " + this.layers[index].getOutputActivationDepth());
            System.out.println("  |- -> Input Height = " + this.layers[index].getInputActivationHeight(0) + ", Output Height = " + this.layers[index].getOutputActivationHeight(0));
            System.out.println("  |- -> Input Width = " + this.layers[index].getInputActivationWidth(0, 0) + ", Output Width = " + this.layers[index].getOutputActivationWidth(0, 0));
            System.out.println("LAYER FILTERS: ");
            for(int depth = 0; depth < this.layers[index].getFiltersSize(); depth++){
                System.out.println(Arrays.deepToString(this.layers[index].getFilters(depth)));
            }
            System.out.println("LAYER BIASES: ");
            System.out.println(Arrays.toString(this.layers[index].getBiases()));

        }
    }
}
