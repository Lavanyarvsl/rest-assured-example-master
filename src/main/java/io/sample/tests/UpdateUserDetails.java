package io.sample.tests;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;

import com.google.common.base.Verify;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.sample.util.Util;
import io.sample.util.GenericRestLibrary;

public class UpdateUserDetails implements ITest {

	String appURI;
	String usrURI;
	JSONObject jsnObject = null;
	String testCaseName = "";
	Util util = new Util();
	JSONParser parser = new JSONParser();
	GenericRestLibrary lib = new GenericRestLibrary();
	SoftAssert softAssert = new SoftAssert();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	private final String moduleName = "user";
	private final String apiName = "updateUser";
	String propFileName = "src/config/test.properties";
	String tcName = "";
	static final Logger logger = Logger.getLogger(CreateUser.class);
	
	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */

	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName + "_" + apiName + "_" + object.toString();
	}

	/**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 */
	@DataProvider(name = "fetchData")
	public Object[][] readData(ITestContext context) throws ParseException {

		return new Util().readTestCases("/" + moduleName + "/" + apiName, "smokeAndRegression");

	}

	/**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * 
	 */
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void GetUserDetails(String testcaseName) throws ParseException {

		
		tcName=testcaseName;
		logger.info("\n*******Test Case Name*******\n" + testcaseName);
		JSONObject objectDataArray[] = util.readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject ActualReq = objectDataArray[0];
		String usrId = ActualReq.get("id").toString();
		
		
		ActualReq.remove("id");
		System.out.println("ActualReq:" + ActualReq);
		Expectedresponse = objectDataArray[1];
		logger.info("\n*******Expectedresponse*******\n" + Expectedresponse);
		
		Actualresponse = lib.putRequest(appURI + usrURI+"/"+usrId, ActualReq);
		logger.info("\n*******Actualresponse*******\n" + Actualresponse.asString());
		jsnObject = (JSONObject) parser.parse(Actualresponse.asString());
		ArrayList<String> arrActRes = new ArrayList<String>();
		arrActRes.add("$.updatedAt");
		
		JSONObject actReq = util.removeJsonElement(Actualresponse.asString(), arrActRes);
		JSONObject exeRes = util.removeJsonElement(Expectedresponse.toJSONString(), arrActRes);
		System.out.println("My actReq::" + actReq);
		System.out.println("My exeReq::" + exeRes);
		boolean status = util.jsonComparison(exeRes, actReq);
		logger.info("\n*******Result*******\n" + status);
		logger.info("=========================================" + status);
		Verify.verify(status);
		softAssert.assertAll();
		

	}

	public String getTestName() {
		this.testCaseName = tcName;
		return this.testCaseName;
	}

	@BeforeClass
	public void resourceIntialize() {
		appURI = util.readProperty(propFileName).get("ApplURI");
		usrURI = util.readProperty(propFileName).get("UserURI");
	}
	
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
	    try {
	        BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
	        Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
	        f.setAccessible(true);
	        f.set(baseTestMethod, this.testCaseName);
	    } catch (Exception e) {
	       
	        Reporter.log("Exception : " + e.getMessage());
	    }
	}

}
