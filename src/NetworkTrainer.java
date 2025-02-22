import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.File;


public class NetworkTrainer {
    private ArrayList<Sample> sampleList = new ArrayList<>();
    private int totalSamples;
    private int correctSamples;
    private int incorrectSamples;
    NetworkFileHandler fileHandler = new NetworkFileHandler();

    //CONTROL VALUES
    final int standardisedImageWidth = 501;
    final int standardisedImageHeight = 501;

    public NetworkTrainer() throws FileNotFoundException {
        File trainingControlData = NetworkFileHandler.loadFile("resources/TrainingSet/TrainingControlData");
        int trainingSetSize = 0;
        if(trainingControlData == null){
            System.out.println("  ! ERROR: TrainingControlData file not found. TrainingControlData file must be manually recreated");
            System.exit(1);
        }else{
            Scanner scannerObj = new Scanner(trainingControlData);
            trainingSetSize = scannerObj.nextInt();
            scannerObj.close();
        }

        for(int i = 0; i < trainingSetSize; i++){
            File trainingElement = NetworkFileHandler.loadFile("resources/TrainingSet/TrainingElement" + i);
            int elementNo = 0;
            boolean isCorrect = false;
            double[][] rArray = new double[standardisedImageHeight][standardisedImageWidth];
            double[][] gArray = new double[standardisedImageHeight][standardisedImageWidth];
            double[][] bArray = new double[standardisedImageHeight][standardisedImageWidth];

            if(trainingElement != null){
                try{
                    int redRow = 0;
                    int blueRow = 0;
                    int greenRow = 0;
                    int nCount = 0;
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(trainingElement));
                    String line;

                    while((line = bufferedReader.readLine()) != null){
                        String[] values = line.split("\\s+");
                        if(redRow == 0){
                            int[] controlInfo = Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
                            elementNo = controlInfo[0];
                            if(controlInfo[1] == 1){
                                isCorrect = true;
                            }else{
                                isCorrect = false;
                            }
                            redRow++;
                        }else{
                            if(Objects.equals(values[0], "n")){
                                nCount++;
                            }else{
                                double[] matrixRow = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
                                if(nCount == 0){ //Load as rArray
                                    rArray[redRow] = matrixRow;
                                    redRow++;
                                }else if(nCount == 1){
                                    gArray[greenRow] = matrixRow;
                                    greenRow++;
                                }else{
                                    bArray[blueRow] = matrixRow;
                                    blueRow++;
                                }
                            }
                        }
                    }
                    sampleList.add(new Sample(elementNo, isCorrect, rArray, gArray, bArray));

                }catch(Exception e){
                    System.out.println("  ! ERROR: Failure attempting to read trainingElement");
                    System.out.print(e);
                    System.exit(1);
                }
            }
        }
    }


    public void shuffleTrainingSet(final int shuffleTimes){
        Random randomiser = new Random();
        //Recommended: shuffle 3 * 4 (12) times

        for(int i = 0; i < shuffleTimes; i++){
            for(int j = 0; j < totalSamples; j++){
                int oldIndex = randomiser.nextInt(totalSamples);
                int newIndex = randomiser.nextInt(totalSamples);

                //boolean temp = sampleArray[oldIndex].getIsCorrectSample();
                //sampleArray[oldIndex].setIsCorrectSample(sampleArray[newIndex].getIsCorrectSample());
                //sampleArray[newIndex].setIsCorrectSample(temp);
            }
        }
    }

    public Sample getSample(final int pos){
        return sampleList.get(pos);
    }

}

class Sample{
    private int testNo;
    private boolean isCorrectSample;

    private double[][] rArray;
    private double[][] gArray;
    private double[][] bArray;

    public Sample(final int testNo, final boolean isCorrectSample, double[][] rArray, double[][] gArray, double[][] bArray){
        //Update once not testing
        this.testNo = testNo;
        this.isCorrectSample = isCorrectSample;
        this.rArray = rArray;
        this.gArray = gArray;
        this.bArray = bArray;
    }

    public int getTestNo(){
        return this.testNo;
    }

    public void setTestValue(final int testNo){
        this.testNo = testNo;
    }

    public boolean getIsCorrectSample(){
        return isCorrectSample;
    }

    public void setIsCorrectSample(final boolean isCorrectSample){
        this.isCorrectSample = isCorrectSample;
    }

    public double[][] getArray(final String arrayIdentifier){
        if(Objects.equals(arrayIdentifier, "r")){
            return rArray;
        }else if(Objects.equals(arrayIdentifier, "g")){
            return gArray;
        }else if(Objects.equals(arrayIdentifier, "b")){
            return bArray;
        }else{
            System.out.println("  ! ERROR: unidentified RGB array requested");
            System.exit(1);
            return null;
        }
    }

    public static void displayArray(double[][] array){
        System.out.println(Arrays.deepToString(array));
    }
}
