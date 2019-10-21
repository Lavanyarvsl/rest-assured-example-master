package io.sample.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;



public class Util {

	static final Logger logger = Logger.getLogger(GenericRestLibrary.class);
	Properties prop = new Properties();
	
	
	/**
	 * @param propertyFileName
	 * @return this method is for reading property file from src/config folder by
	 *         passing the property file name
	 */
	public Map<String, String> readProperty(String propertyFileName) {
		try {
			prop.load(new FileInputStream("src/config/test.properties"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> mapProp = prop.entrySet().stream()
				.collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
		return mapProp;

	}
	
	public JSONObject readJsonData(String path) {

		// File fileToRead = new File(getResourcePath()+path);
		 File fileToRead = new File(path);
		
		InputStream isOfFile = null;
		try {
			isOfFile = new FileInputStream(fileToRead);
		} catch (FileNotFoundException e1) {
	
		}
		JSONObject jsonData = null;
		try {
			jsonData = (JSONObject) new JSONParser().parse(new InputStreamReader(isOfFile, "UTF-8"));
		} catch (IOException | ParseException | NullPointerException e) {
		
		}
		return jsonData;
	}
	
	public String getResourcePath() {
		return "src/test/resources";
	}
	
	
	/**
	 * this function compare the request and response json and return the boolean
	 * value
	 * 
	 * @param expectedResponseBody
	 * @param actualResponseBody
	 * @return boolean value
	 */
	public boolean jsonComparison(JSONObject expectedResponseBody, Object actualResponseBody) {
		JSONObject reqObj =  expectedResponseBody;
		JSONObject resObj =  (JSONObject) actualResponseBody;
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode requestJson = mapper.readTree(reqObj.toString());
			JsonNode responseJson = mapper.readTree(resObj.toString());
			JsonNode diffJson = JsonDiff.asJson(requestJson, responseJson);

			
			if (diffJson.toString().equals("[]")) {
				
				return true;
			}

			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					
					return false;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;

	}
	
	
	/**
	 * @param folderName
	 * @param testType
	 * @param requestjsonName
	 * @return this method works as data provider and reads the test case folders and returns the respective output object.
	 * @throws IOException
	 * @throws ParseException
	 */
	public Object[][] readTestCases(String folderName, String testType){

		List<String> listOfFolders = getFoldersFilesNameList(folderName, true);
		
		ArrayList<String> testCaseNames = new ArrayList<>();
		
		for (int j = 0; j < listOfFolders.size(); j++) {
			String[] arr = listOfFolders.get(j).split("/");
			switch (testType) {
			case "smoke":
				if (arr[arr.length - 1].toString().contains("smoke"))
					testCaseNames.add(arr[arr.length - 1].toString());
				break;
			case "regression":
				if (arr[arr.length - 1].toString().contains("invalid"))
					testCaseNames.add(arr[arr.length - 1].toString());
				break;

			default:
				testCaseNames.add(arr[arr.length - 1].toString());
				break;
			}
		}

		Object[][] testParam = new Object[testCaseNames.size()][];
		int k = 0;
		for (String testcaseName : testCaseNames) {
			
			testParam[k] = new Object[] {testcaseName};
			k++;
		}
		return testParam;

	}

	
	/**
	 * @param folderRelativePath
	 * @param isfolder(it should be true if u want to get list of folders and false for list of files)
	 * @return this method is for returning the list of relative path of each folder or files in a given path
	 */
	public List<String> getFoldersFilesNameList(String folderRelativePath, boolean isfolder){
		String configPath = folderRelativePath;
		List<String> listFoldersFiles = new ArrayList<>();
		
		
					final File file = new File(getResourcePath()+folderRelativePath);
					
					for (File f : file.listFiles()) {
						if (f.isDirectory()==isfolder)
						listFoldersFiles.add(configPath + "/" + f.getName());
						
					}
					//System.out.println("======listFoldersFiles=="+listFoldersFiles);
		return listFoldersFiles;
	}
	
	
	/**
	 * @param modulename
	 * @param apiname
	 * @param testcaseName
	 * @return this method is for reading request and response object form the given testcase folder and returns in array.
	 */
	public JSONObject[] readRequestResponseJson(String modulename, String apiname, String testcaseName){
		String configPath = "/"+modulename + "/" + apiname + "/" + testcaseName;
		String path="src/test/resources"+configPath;
		List<String> listofFiles =  getFoldersFilesNameList(configPath, false);
		System.out.println("list of values:"+listofFiles);
		JSONObject[] objectData = new JSONObject[2];
		for (int k = 0; k < listofFiles.size(); k++) {
			String[] arr = listofFiles.get(k).split("/");
				if (arr[arr.length - 1].toLowerCase().contains("request")) 
					objectData[0] =  readJsonData(path+"/"+arr[arr.length - 1]);
					
				 else if (arr[arr.length - 1].toLowerCase().contains("response")) 
					objectData[1] =  readJsonData(path+"/"+arr[arr.length - 1]);
		 
				
		}
		
		return objectData;
	}
	
	
	/**
	 * The method to remove the json element from the json file
	 * 
	 * @return String
	 * @throws ParseException 
	 */
	
	public JSONObject removeJsonElement(String yourActualJSONString,ArrayList<String> eleToRemove) throws ParseException {
		String myOutput = null;
		JSONObject jsnObject = null;
		String val = null;
		
		//String yourActualJSONString = new String(Files.readAllBytes(Paths.get(readFilePath)), StandardCharsets.UTF_8);
		DocumentContext jsonContext = JsonPath.parse(yourActualJSONString);
		
		for (int i = 0; i < eleToRemove.size(); i++) 
		{
			val=eleToRemove.get(i);
			jsonContext.delete(val);
			myOutput = jsonContext.jsonString();
			JSONParser parser = new JSONParser(); 
		    jsnObject = (JSONObject) parser.parse(myOutput);
			
		}
		return jsnObject;
		
	}
	
	/*
	 * Generic method to compare Values
	 * 
	 */
	public void compareValues(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
			logger.info("values are equal");
		} catch (Exception e) {
			logger.info("values are not equal");
		}
	}
	
	
}
