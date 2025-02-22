public class NetworkErrorCalculator {
    Layer layer;
    Neuron[] neurons;
    double[] inputs;
    double[] outputs;
    double[][] Connections;

    public NetworkErrorCalculator(Layer layer1){
        //layer = layer1;
        //inputs = layer.getInputs();
        //outputs = layer.getOutputs();
        //neurons = layer.getNeurons();
        //Connections = layer.getLayerConnectionSet();

    }

    /*
    public double[] weightedSum(){
        double[] sum = new double[neurons.length-1];
        for(int i = 0; i <= neurons.length-1; i++){
            for(int j = 0; j <= layer.getInputsSize(); j++){
                sum[i] += Connections[i][j] *inputs[j];
            }
            sum[i] += neurons[i].getBias();
        }
        return sum;
    }
     */

    /*
    public double[] BweightedSum(){
        double[] Bsum = new double[neurons.length-1];
        for(int i = 0; i <= neurons.length-1; i++){
            for(int j = 0; j <= layer.getInputsSize(); j++){
                Bsum[i] += Connections[i][j] *inputs[j];
            }
            Bsum[i] += neurons[i].getBias();
        }
        return Bsum;
    }
     */

    /*
    public double[] DFactivationFunction(){
        double[] activations = new double[neurons.length-1];
        for(int i = 0; i <= neurons.length-1; i++){
            activations[i] = NetworkMathHandler.TANH_Derivative(weightedSum()[i]);
        }
        return activations;
    }
     */

    /*
    public double[] DFweightedSum(){
        return inputs;
    }

    public double[] costFunction(){
        double[] costs = new double[neurons.length-1];
        for(int i = 0; i <= neurons.length-1; i++){
            for(int j = 0; j <= layer.getOutputsSize(); j++){
                //costs[i] = (outputs[j] - desiredOutput[j])^2;
            }
        }
        return costs;
    }
     */

    /*
    public double[] DFcostFunction(){
        double[] DFcost = new double[neurons.length-1];
        for(int i = 0; i <= neurons.length-1; i++){
            //DFcost[i] = weightedSum()[i]*(outputs[i] - desiredoutput[i]);
        }
        return DFcost;
    }
     */

    /*
    public double[] changeInWeights(){
        double[] change = new double[neurons.length-1];
        for(int i = 0; i <= neurons.length-1; i++){
            change[i] = DFcostFunction()[i] * DFactivationFunction()[i] * DFweightedSum()[i];
        }
        return change;
    }f
     */

    /*public double[] changeInBias(){

    }*/
}
