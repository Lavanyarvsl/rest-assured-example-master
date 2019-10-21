package io.sample.util;


import io.restassured.response.Response;
import io.sample.tests.CreateUser;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.testng.Assert;


public class GenericRestLibrary {

	static final Logger logger = Logger.getLogger(GenericRestLibrary.class);
	
	/**
	 * @param url
	 * @return this method is for get request 
	 *         without any param
	 */
	public Response getRequestWithoutParameters(String url) {
		logger.info("\n********HTTP URI********\n" +url);
		Response getResponse = given().relaxedHTTPSValidation().log().all().when().get(url)
				.then().log().all().extract().response();
		
		return getResponse;
		
	} // end GET_REQUEST
	
	/**
	 * @param url
	 * @return this method is for post request 
	 *         with request body
	 */
	public Response postRequest(String url, Object body) {
		logger.info("\n********HTTP URI********\n" +url);
		Response postResponse = given().relaxedHTTPSValidation().body(body)
				.contentType( MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).log().all().when().post(url).then().log().all()
				.extract().response();
		
		
		return postResponse;
	} // end POST_REQUEST
	
	/**
	 * @param url
	 * @return this method is for put request 
	 *         with request body
	 */
	public Response putRequest(String url, Object body) {
		logger.info("\n********HTTP URI********\n" +url);
		Response putResponse = given().relaxedHTTPSValidation().body(body)
				.contentType( MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).log().all().when().put(url).then().log().all()
				.extract().response();
		System.out.println("Put::"+putResponse.asString());
		
		return putResponse;
	} // end POST_REQUEST
	
	/**
	 * @param url
	 * @return this method is for delete request 
	 *         with request body
	 */
	public Response deleteRequest(String url, Object body) {
		logger.info("\n********HTTP URI********\n" +url);
		Response putResponse = given().relaxedHTTPSValidation().body(body)
				.contentType( MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).log().all().when().delete(url).then().log().all()
				.extract().response();
		
		
		return putResponse;
	} // end POST_REQUEST
	
	
	/**
	 * @param url
	 * @param body
	 * @param pathParams
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for post request with
	 *         pathParams and jsonData in request body.
	 */
	public Response postWithPathParams(String url, Object body, HashMap<String, String> pathParams,
			String contentHeader, String acceptHeader) {
		logger.info("\n********HTTP URI********\n" +url);
		
		Response postResponse = given().relaxedHTTPSValidation().pathParams(pathParams)
				.body(body).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).log().all().when().post(url).then().log()
				.all().extract().response();
		
		return postResponse;
	}
	
	
}
