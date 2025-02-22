import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Layer{

    //SETTINGS
    final int noOfLayerDependencies = 5;

    //CLASS ATTRIBUTES
    private static int layerNum = -1;

    double[][][] inputActivationMatrix = null;
    double[][][] outputActivationMatrix = null;

    double[][][][] filters = null;
    double[] biases = null;

    public Layer(){
        layerNum++;

        initDependencies();

        this.outputActivationMatrix = new double[this.filters.length][][];
        System.out.println("       |- -> Assigned outputActivationMatrix depth successfully");

        System.out.println("    |- ! Initialisation of " + layerNum + " completed successfully\n");
    }

    private void initDependencies(){
        System.out.println("    INITIALISING LAYER " + layerNum);
        int[] controlValues = new int[noOfLayerDependencies];
        File dependenciesFile = NetworkFileHandler.loadFile("resources/LayerDependencies/LayerControls/Controls_Layer" + layerNum);
        if(dependenciesFile == null){
            System.out.println("  ! FATAL ERROR: Layer Control info from resources/LayerDependencies/LayerControls/Controls_Layer " + layerNum + " cannot be located.");
            System.exit(1);
        }else{
            try{
                Scanner scanner = new Scanner(dependenciesFile);
                int i = 0;
                while(scanner.hasNext()){
                    controlValues[i] = scanner.nextInt();
                    i++;
                }
                scanner.close();
            }catch(Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        System.out.print("    |- ! Displaying Control info loaded for Layer " + layerNum + ": ");
        System.out.print(Arrays.toString(controlValues));
        System.out.println();

        final int filterNo = controlValues[0];
        final int filterDepth = controlValues[1];
        final int filterHeight = controlValues[2];
        final int filterWidth = controlValues[3];
        final int biasDepth = controlValues[4];

        try{
            initFilters(filterNo, filterDepth, filterHeight, filterWidth);
            initBiases(biasDepth);
        }catch(Exception e){
            System.out.println("  ! FATAL ERROR: Error occurred during initialisation of Layer " + layerNum + " dependencies");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initFilters(final int filterNo, final int filterDepth, final int filterHeight, final int filterWidth) throws IOException {
        this.filters = new double[filterNo][filterDepth][filterHeight][filterWidth];

        File file = NetworkFileHandler.loadFile("resources/LayerDependencies/LayerFilters/Filters_Layer" + layerNum);
        if(file ==null){
            System.out.println("    ! Filters for Layer " + layerNum + " presumed lost, constructing untrained filter to replace...");
            for(int no = 0; no < filterNo; no++){
                for(int depth = 0; depth < filterDepth; depth++){
                    for(int row = 0; row < filterHeight; row++){
                        for(int col = 0; col < filterWidth; col++){
                            this.filters[no][depth][row][col] = 1;
                        }
                    }
                }
            }
            System.out.println("       |- -> Saving untrained filters for Layer " + layerNum);
            NetworkFileHandler.writeFile(this.filters, "resources/LayerDependencies/LayerFilters/Filters_Layer" + layerNum);
        }else{
            System.out.println("       |- -> Loading located Filters for Layer " + layerNum);
            int nCount = 0;
            int aCount = 0;
            int row = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while((line = bufferedReader.readLine()) != null){
                if(line.equals("n")) {
                    nCount++;
                    row = 0;
                }else if(line.equals("a")){
                    aCount++;
                    row = 0;
                    nCount = 0;
                }else{
                    String[] values = line.split("\\s+");
                    double[] matrixRow = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
                    this.filters[aCount][nCount][row] = matrixRow;
                    row++;
                }
            }
        }
    }

    private void initBiases(final int biasDepth) throws FileNotFoundException {
        this.biases = new double[biasDepth];
        File file = NetworkFileHandler.loadFile("resources/LayerDependencies/LayerBiases/Biases_Layer" + layerNum);
        if(file ==null){
            System.out.println("    ! Biases for Layer " + layerNum + " presumed lost, constructing unadjusted biases to replace...");
            for(int row = 0; row < biases.length; row++){
                this.biases[row] = 0;
            }
            System.out.println("       |- -> Saving unadjusted biases for Layer " + layerNum);
            NetworkFileHandler.writeFile(this.biases, "resources/LayerDependencies/LayerBiases/Biases_Layer" + layerNum);
        }else{
            System.out.println("       |- -> Loading located Biases for Layer " + layerNum);
            Scanner scanner = new Scanner(file);
            int i = 0;
            while(scanner.hasNextLine() && i < biasDepth){
                String line = scanner.nextLine();
                this.biases[i] = Double.parseDouble(line);
                i++;
            }
            scanner.close();
        }
    }

    //UPDATE TO USE CONVOLUTION AND POOL -------------------------------------------------------------------------------
    public void beginComputation(final int loopStep){
        System.out.println("  ? Layer " + loopStep + " beginning computation");
        for(int number = 0; number < filters.length; number++){
            for(int depth = 0; depth < inputActivationMatrix.length; depth++){
                if(loopStep == 3 || loopStep == 6 || loopStep == 8 || loopStep == 10){
                    System.out.println("  |- -> Pool  Input Depth " + depth + " computing for Layer " + loopStep);
                    outputActivationMatrix[number] = Pool.regionalMaxPool(this.filters[number][depth], inputActivationMatrix[depth]);
                }else{
                    System.out.println("  |- -> Convolution Input Depth " + depth + " computing for Layer " + loopStep);
                    outputActivationMatrix[number] = Convolution.convolute(this.filters[number][depth], inputActivationMatrix[depth]);
                }

            }
        }

    }

    public NetworkFileHandler.Request updateLayerFilters(final double[][][][] newFilters){
        System.out.println("            ? Checking for update in filters of Layer " + layerNum);
        boolean updated = false;

        int filterNo = 0;
        while(filterNo < this.filters.length && !updated){
            int depth = 0;
            while(depth < this.filters[filterNo].length && !updated){
                int row = 0;
                while(row < this.filters[filterNo][depth].length && !updated){
                    int col = 0;
                    while(col < this.filters[filterNo][depth][row].length && !updated){
                        if(this.filters[filterNo][depth][row][col] != newFilters[filterNo][depth][row][col]){
                            System.out.println("            ! Updating Layer " + layerNum + " filters, adding new filters to fileQueue");
                            this.filters = newFilters;
                            updated = true;
                        }
                        col++;
                    }
                    row++;
                }
                depth++;
            }
            filterNo++;
        }
        if(updated){
            return new NetworkFileHandler.Request("resources/LayerDependencies/LayerFilters/Filters_Layer" + layerNum, newFilters);
        }else{
            System.out.println("            X No update in filters of Layer " + layerNum + " filters");
            return null;
        }
    }

    public NetworkFileHandler.Request updateLayerBiases(final double[] newBiases){
        System.out.println("            ? Checking for update in biases of Layer " + layerNum);
        boolean updated = false;
        int i = 0;
        while(i < this.biases.length && !updated){
            if(this.biases[i] != newBiases[i]){
                System.out.println("            ! Updating Layer " + layerNum + " biases, adding new biases to fileQueue");
                this.biases = newBiases;
                updated = true;
            }
            i++;
        }

        if(updated){
            return new NetworkFileHandler.Request("resources/WeightsAndConnections/connections_Layer" + layerNum, newBiases);
        }else{
            System.out.println("            X No update in biases of Layer " + layerNum + " biases");
            return null;
        }
    }

    public void setInputActivationMatrix(final double[][][] inputActivationMatrix){
        this.inputActivationMatrix = inputActivationMatrix;
    }

    public double[][][] getInputActivationMatrix(){
        return inputActivationMatrix;
    }

    public double[][] getSubInputActivationMatrix(final int depth){
        return this.inputActivationMatrix[depth];
    }

    public int getInputActivationDepth(){
        return this.inputActivationMatrix.length;
    }

    public int getInputActivationHeight(final int depth){
        return this.inputActivationMatrix[depth].length;
    }

    public int getInputActivationWidth(final int depth, final int row){
        return this.inputActivationMatrix[depth][row].length;
    }

    public int getOutputActivationDepth(){
        return this.outputActivationMatrix.length;
    }

    public int getOutputActivationHeight(final int depth){
        return this.outputActivationMatrix[depth].length;
    }

    public int getOutputActivationWidth(final int depth, final int row){
        return this.outputActivationMatrix[depth][row].length;
    }

    public void setOutputActivationMatrix(final double[][][] outputActivationMatrix){
        this.outputActivationMatrix = outputActivationMatrix;
    }

    public double[][][] getOutputActivationMatrix(){
        return outputActivationMatrix;
    }

    public double[][] getSubOutputActivationMatrix(int depth){
        return this.outputActivationMatrix[depth];
    }

    public void setFilters(final double[][][][] filters){
        this.filters = filters;
    }

    public int getFiltersSize(){
        return filters.length;
    }

    public int getFiltersDepthSize(final int filterNo){
        return filters[filterNo].length;
    }

    public int getFiltersHeightSize(final int filterNo, final int depth){
        return this.filters[filterNo][depth].length;
    }

    public int getFiltersWidthSize(final int filterNo, final int depth, final int row){
        return this.filters[filterNo][depth][row].length;
    }

    public double[][][][] getFilters(){
        return filters;
    }

    public double[][][] getFilters(final int index){
        return filters[index];
    }

    public void setBiases(final double[] biases){
        this.biases = biases;
    }

    public int getBiasesSize(){
        return biases.length;
    }

    public double[] getBiases(){
        return biases;
    }
}


class Convolution{
    public static double[][] convolute(double[][] filter, double[][] input){
        double[][] output = new double[input.length - filter.length + 1][input[0].length - filter[0].length + 1];
        for(int row = 0; row < input.length - filter.length + 1; row++){
            for(int col = 0; col < input[0].length - filter[0].length + 1; col++){
                output[row][col] = getWeightedSums(createSubArray(input, filter.length, row, col), filter);
            }
        }
        return applyActivationFunction(output);
    }

    public static double[][] createSubArray(double[][] input, final int filterLength, final int row, final int col){
        double[][] subArray = new double[filterLength][filterLength];
        for(int x = 0; x < subArray.length; x++){
            for(int y = 0; y < subArray[0].length; y++){
                subArray[x][y] = input[x + row][y + col];
            }
        }
        return subArray;
    }

    private static double getWeightedSums(double[][] subArray, double[][] filter){
        double weightedSum = 0;
        for(int row = 0; row < subArray.length; row++){
            for(int col = 0; col < subArray[row].length; col++){
                weightedSum += filter[row][col] * subArray[row][col];
            }
        }
        return weightedSum;
    }

    private static double[][] applyActivationFunction(double[][] array){
        for(int row = 0; row < array.length; row++){
            for(int col = 0; col < array[row].length; col++){
                array[row][col] = NetworkMathHandler.TANH_Activation(array[row][col]);
            }
        }
        return array;
    }

    public void backPropagation(double[] dLdO){

    }
}

//NEED TO IMPLEMENT ----------------------------------------------------------------------------------------------------
class Pool{
    public double[][][] poolActivationMatrix(double[][][] activationMatrix){
        return null;
    }

    public static double[][] regionalMaxPool(double[][] poolFilter, double[][] input){
        double[][] newOutput = new double[input.length / poolFilter.length][input[0].length / poolFilter[0].length];
        int newOutputRow = 0;
        int newOutputCol = 0;
        for(int row = 0; row < input.length; row+= 2){
            for(int col = 0; col < input.length; col+= 2){
                double[][] subArray = Convolution.createSubArray(input, poolFilter.length, row, col);
                double greatest = subArray[0][0];
                for(int x = 0; x < subArray.length; x++){
                    for(int y = 0; y < subArray[0].length; y++){
                        if(greatest < subArray[x][y]){
                            greatest = subArray[x][y];
                        }
                    }
                }
                newOutput[newOutputRow][newOutputCol] = greatest;
                newOutputCol++;
                if(newOutputCol == input[0].length){
                    newOutputRow++;
                    newOutputCol = 0;
                }
            }
        }
        return newOutput;
    }

    public static double[][][] globalMaxPool(double[][][] array, final int filterLength){
        return null;
    }
}
