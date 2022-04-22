import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCsv = parseCSV(columnMapping, fileName);

        String json = listToJson(listCsv);

        writeString(json, "dataJ.json");

        List<Employee> listXml = parseXML("data.xml");
        System.out.println(listXml);

        String json2 = listToJson(listXml);

        writeString(json2, "dataJ2.json");

        String dataJson = readString("dataJ2.json");
        System.out.println(dataJson);

        List<Employee> listJs = jsonToList(dataJson);
        for (Employee oneEmployee : listJs) {
            System.out.println(oneEmployee);
        }
    }


    public static List<Employee> jsonToList(String strJson) {
        List<Employee> jsList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(strJson);
            JSONArray jsonArray = (JSONArray) obj;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object obAr : jsonArray) {
                String strJs = obAr.toString();
                Employee employee = gson.fromJson(strJs, Employee.class);
                jsList.add(employee);
            }

        } catch (ParseException exception) {
            exception.printStackTrace();
        }
        return jsList;
    }

    private static String readString(String path) {
        StringBuilder str = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String s;
            while ((s = br.readLine()) != null) {
                str.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(str);
    }

    public static List<Employee> parseXML(String strPath) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(strPath));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (Node.ELEMENT_NODE == node_.getNodeType()) {
                    Element element = (Element) node_;
                    NodeList listEl = element.getChildNodes();

                    Employee employee = new Employee();
                    for (int a = 0; a < listEl.getLength(); a++) {
                        Node current = listEl.item(a);
                        String attrName = current.getNodeName();
                        String attrValue = current.getTextContent();

                        if (attrName.equals("id")) {
                            employee.id = Long.parseLong(attrValue);
                        }
                        if (attrName.equals("firstName")) {
                            employee.firstName = attrValue;
                        }
                        if (attrName.equals("lastName")) {
                            employee.lastName = attrValue;
                        }
                        if (attrName.equals("country")) {
                            employee.country = attrValue;
                        }
                        if (attrName.equals("age")) {
                            employee.age = Integer.parseInt(attrValue);
                        }
                    }
                    employees.add(employee);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static <T> String listToJson(List<Employee> list) {
        String json = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        json = gson.toJson(list, listType);

        return json;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> listEmployee = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            listEmployee = csv.parse();
            listEmployee.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listEmployee;
    }

    public static void writeString(String string, String name) {
        try (FileWriter file = new FileWriter(name)) {
            file.write(string);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
