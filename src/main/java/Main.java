import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {
        //CSV-JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameWrite = "data.json";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, fileNameWrite);

        //XML-JSON парсер
        String fileNameWrite1 = "data2.json";
        String xmlFile = "data.xml";
        List<Employee> list1 = parseXML(xmlFile);
        String json1 = listToJson(list1);
        writeString(json1, fileNameWrite1);

        //JSON-Java парсер
        String json2 = readString(fileNameWrite);
        List<Employee> list2 = jsonToList(json2);
        for (Employee employee : list2) {
            System.out.println(employee);
        }
    }

    static List<Employee> parseCSV(String[] arr, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(arr);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        List<Employee> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                list.add(new Employee(
                        Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())));
            }
        }
        return list;
    }

    static String readString(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int ch;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = reader.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        return stringBuilder.toString();
    }

    static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> list = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json);

        for (Object jsonElement : jsonArray) {
            Employee employee = gson.fromJson(jsonElement.toString(), Employee.class);
            list.add(employee);
        }
        return list;
    }
}