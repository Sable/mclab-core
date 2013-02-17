package natlab.tame.mc4.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import natlab.toolkits.filehandling.GenericFile;

/**
 * represents a result value as read by the unit test xml
 * TODO - this should be made generic to represent arbitrary matlab values
 */

public class ResultValue {
    ArrayList<Integer> size = null;
    int numdims, numel;
    boolean isReal = true;
    String matlabClass = null;
    
    ArrayList<HashMap<String,ResultValue>> structResult;
    ArrayList<Double> matrixResult,imagMatrixResult;
    String charResult;
    ArrayList<Boolean> logicalResult;
    ArrayList<ResultValue> cellResult;
    RESULT_TYPE resultType = RESULT_TYPE.UNKNOWN;
    public enum RESULT_TYPE{
        STRUCT,
        HANDLE,
        MATRIX,
        LOGICAL,
        CHAR,
        CELL,
        UNKNOWN
    }
    
    public static class XMLResult{
        public boolean success;
        public ResultValue resultValue;
        public String error;
        public String message;
        public String toString() {
            return "success="+success
            +",value={"+resultValue+"}"
            +(success?"":",error="+error);
        }
    }
    
    private static Element readXmlFile(GenericFile file){
        try{
            BufferedReader reader = new BufferedReader(file.getReader());
            StringBuilder sBuilder = new StringBuilder();
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                sBuilder.append(line);
                sBuilder.append("\n");
            }
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();    
            Document doc = dBuilder.parse(new ByteArrayInputStream(sBuilder.toString().getBytes()));
            doc.getDocumentElement().normalize();  
            return doc.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;            
        }
    }
    

    public static XMLResult readResult(GenericFile file){
        Node node = readXmlFile(file);
        XMLResult result = new XMLResult();
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++){
            Node n = list.item(i);
            if (n.getNodeName().equals("success")){
                result.success = string2booleanList(n.getTextContent()).get(0);
            }
            if (n.getNodeName().equals("result")){
                result.resultValue = new ResultValue(n);
            }
            if (n.getNodeName().equals("error")){
                result.resultValue = new ResultValue(n);
                result.error = result.resultValue.structResult.get(0).get("identifier").charResult;
                result.message = result.resultValue.structResult.get(0).get("message").charResult;
            }
        }
        return result;
    }
    
    public static ArrayList<Integer> string2intList(String s){
        ArrayList<Integer> list = new ArrayList<Integer>();
        StringTokenizer tokenizer = (new StringTokenizer(s,","));
        while (tokenizer.hasMoreElements()){
            list.add(Integer.valueOf(tokenizer.nextToken().trim()));
        }
        return list;
    }
    
    public static ArrayList<Double> string2doubleList(String s){
        ArrayList<Double> list = new ArrayList<Double>();
        StringTokenizer tokenizer = (new StringTokenizer(s,","));
        while (tokenizer.hasMoreElements()){
            String f = tokenizer.nextToken().trim();
            if (f.endsWith("Inf")){
                f = f.substring(0, f.length()-3)+"Infinity";
            }
            list.add(Double.valueOf(f));
        }
        return list;
    }
    
    public static ArrayList<Boolean> string2booleanList(String s){
        ArrayList<Boolean> list = new ArrayList<Boolean>();
        StringTokenizer tokenizer = (new StringTokenizer(s,","));
        while (tokenizer.hasMoreElements()){
            String b = tokenizer.nextToken().trim();
            if (b.equals("true")){
                list.add(true);
            } else if (b.equals("false")){
                list.add(false);
            } else {
                throw new UnsupportedOperationException("expected string of comma separated booleans, received "+s);
            }
        }
        return list;
    }
        
    public ResultValue(Node node){
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++){
            Node n = list.item(i);
            if (n.getNodeName().equals("class")){
                matlabClass = n.getTextContent();
            }
            if (n.getNodeName().equals("size")){
                size = string2intList(n.getTextContent());
                numdims = size.size();
                numel = 1;
                for (int d : size){
                    numel *= d;
                }
            }
            if (n.getNodeName().equals("matrix")){
                resultType = RESULT_TYPE.MATRIX;
                matrixResult = string2doubleList(n.getTextContent());
            }
            if (n.getNodeName().equals("imagMatrix")){
                isReal = false;
                resultType = RESULT_TYPE.MATRIX;
                imagMatrixResult = string2doubleList(n.getTextContent());
            }
            if (n.getNodeName().equals("char")){
                resultType = RESULT_TYPE.CHAR;
                charResult = n.getTextContent();
            }
            if (n.getNodeName().equals("logical")){
                resultType = RESULT_TYPE.LOGICAL;
                logicalResult = string2booleanList(n.getTextContent());
            }
            if (n.getNodeName().equals("handle")){
                resultType = RESULT_TYPE.HANDLE;
            }
            if (n.getNodeName().equals("struct")){
                resultType = RESULT_TYPE.STRUCT;
                //create struct list for first occurence of struct
                if (structResult == null){
                    structResult = new ArrayList<HashMap<String,ResultValue>>();
                }
                //build struct
                HashMap<String,ResultValue> struct = new HashMap<String, ResultValue>();
                NodeList children = n.getChildNodes();
                for (int j = 0; j < children.getLength(); j++){
                    Node child = children.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE){
                        struct.put(child.getNodeName(),
                                new ResultValue(child));
                    }
                }
                structResult.add(struct);
            }
            if (n.getNodeName().equals("cell")){
                resultType = RESULT_TYPE.CELL;
            }
        }
    }
    
    
    public String toString() {
        String s = "class="+matlabClass+",size="+size+",";
        s+=resultType;
        switch (resultType) {
        case STRUCT: s+=","+structResult; break;
        case MATRIX: s+=","+matrixResult; break;
        case LOGICAL:s+=","+logicalResult; break;
        case CHAR:   s+=","+charResult; break;
        }
        
        return s;
    }
}




