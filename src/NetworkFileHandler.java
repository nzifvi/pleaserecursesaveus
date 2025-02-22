import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NetworkFileHandler {
    ArrayList<Request> fileQueue = new ArrayList<>();


    public static class Request {
        final   private      String filePath;
        private double[]     array1 = null;
        private double[][][][] array2 = null;

        public Request(String filePath, double[] array){
            this.filePath = filePath;
            this.array1 = array;
        }

        public Request(String filePath, double[][][][] array){
            this.filePath = filePath;
            this.array2 = array;
        }

        public double getElement(final int index){
            return array1[index];
        }

        public double getElement(final int filterNo, final int depth, final int row, final int col){
            return array2[filterNo][depth][row][col];
        }

        public double[] getArray1(){
            return array1;
        }

        public double[][][] getArray2HeightXWidth(int index){
            return array2[index];
        }

        public double[][][][] getArray2DepthXHeightXWidth(){
            return array2;
        }

        public String getFilePath(){
            return filePath;
        }
    }

    public void enqueue(Request request){
        int i = 0;
        boolean existsInQueue = false;

        while(i < fileQueue.size() && !existsInQueue){
            if(fileQueue.get(i).getFilePath().equals(request.getFilePath())){
                existsInQueue = true;
                fileQueue.set(i, request);
            }
        }
        if(!existsInQueue){
            fileQueue.add(request);
        }
    }

    public static File loadFile(final String filePath){
        try{
            File file = new File(filePath);
            if(file.exists()){
                return file;
            }else{
                return null;
            }
        }catch(Exception e){
            System.out.println("  ! Fatal error when attempting to read " + filePath);
            System.exit(1);
        }
        return null;
    }

    public static double[][][] loadInput(final File file, final int channelNo, final int standardHeight, final int standardWidth) throws IOException {
        double[][][] inputArray = new double[channelNo][standardHeight][standardWidth];

        if(file == null){
            System.out.println("  ! FATAL ERROR: Input file is null. This may potentially be due to a failure to read");
            System.exit(1);
        }else{
            int nCount = 0;
            int row = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null && nCount < channelNo){
                if(line.equals("n")){
                    nCount++;
                    row = 0;
                }else{
                    String[] values = line.split("\\s+");
                    double[] matrixRow = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
                    inputArray[nCount][row] = matrixRow;
                    row++;
                }
            }
        }
        return inputArray;
    }

    public static double[][][][] loadInput(final File file, final int filterNo,  final int filterDepth, final int standardHeight, final int standardWidth) throws IOException {
        double[][][][] inputArray = new double[filterNo][filterDepth][standardHeight][standardWidth];

        if (file == null) {
            System.out.println("  ! FATAL ERROR: Input file is null. This may potentially be due to a failure to read");
            System.exit(1);
        } else {
            int nCount = 0;
            int aCount = 0;
            int row = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null && nCount < filterNo) {
                if (line.equals("n")) {
                    nCount++;
                    row = 0;
                } else if (line.equals("a")) {
                    aCount++;
                    row = 0;
                } else {
                    String[] values = line.split("\\s+");
                    double[] matrixRow = Arrays.stream(values).mapToDouble(Double::parseDouble).toArray();
                    inputArray[aCount][nCount][row] = matrixRow;
                    row++;
                }
            }
        }
        return inputArray;
    }

    public static void writeFile(final double[][][][] array, final String filePath){
        try{
            FileWriter writeFile = new FileWriter(filePath);

            for(int no = 0; no < array.length; no++){
                for(int depth = 0; depth < array[no].length; depth++){
                    for(int row = 0; row < array[no][depth].length; row++){
                        for(int col = 0; col < array[no][depth][row].length; col++){
                            writeFile.write(array[no][depth][row][col] + " ");
                        }
                        writeFile.write("\n");
                    }
                    writeFile.write("n\n");
                }
                writeFile.write("a\n");
            }

            writeFile.close();
        }catch(Exception e){
            System.out.println("  ! FATAL ERROR: An error occurred when attempting to write to " + filePath);
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void writeFile(final double[] array, final String filePath){
        try{
            FileWriter writeFile = new FileWriter(filePath);
            for(int row = 0; row < array.length; row++){
                writeFile.write(array[row] + "\n");
            }
            writeFile.close();
        }catch(Exception e){
            System.out.println("  ! FATAL ERROR: An error occurred when attempting to write to " + filePath);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void processQueue(){
        for(int i = 0; i < fileQueue.size(); i++){
            try{
                Request requestToWrite = fileQueue.removeFirst();
                FileWriter fw = new FileWriter(requestToWrite.getFilePath());
                if(requestToWrite.getArray2DepthXHeightXWidth() == null){ //Write bias request

                    double[] arrayToWrite = requestToWrite.getArray1();
                    for(int row = 0; row < arrayToWrite.length; row++){
                        fw.write(arrayToWrite[row] + "\n");
                    }
                    fw.close();

                }else if(requestToWrite.getArray1() == null){//Write layer weight set request

                    double[][][][] arrayToWrite = requestToWrite.getArray2DepthXHeightXWidth();
                    for(int depth = 0; depth < arrayToWrite.length; depth++){
                        for(int row = 0; row < arrayToWrite[depth].length; row++){
                            for(int col = 0; col < arrayToWrite[depth][row].length; col++){
                                fw.write(arrayToWrite[depth][row][col] + " ");
                            }
                            fw.write("\n");
                        }
                        fw.write("n\n");
                    }
                }
            }catch(Exception e){
                System.out.println("  ! ERROR: Fatal error occurred when attempting to process fileQueue");
                System.exit(1);

            }
        }
    }
}
