package com.techstageone.techstageone;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
	private static String URL = "https://jsonmock.hackerrank.com/api/article_users/search?page=";
	private static int Total_pages;
	public static JSONArray cache;


	public static String getJSONCache(final String urlToRead) throws Exception {
		final StringBuilder result = new StringBuilder();
		final URL url = new URL(urlToRead);
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}

	public static String returnURLFromPage(int page) {
		StringBuilder pageURL = new StringBuilder(URL);  
		pageURL.append(page); 

		return pageURL.toString();
	}

	//Algorithms

	public static List<String> getUsernames(int threshold) {
	 	// This function would retrieve the names of the most active authors(using submission_count as the criteria) according to a set threshold.
		List<String> userList = new ArrayList<String>();
		for( Object jobj : cache) {
			JSONObject jsonObj = (JSONObject) jobj;
			if ( (int)jsonObj.get("submission_count") >= threshold)
				userList.add((String)jsonObj.get("username"));
		}
		return userList;
	}
	public static String getUsernameWithHighestCommentCount(){
		// This function would retrieve the name of the author with the highest comment count.
		String user = "";
		int comments = 0;
		for( Object jobj : cache) {
			JSONObject jsonObj = (JSONObject) jobj;
			if ( (int)jsonObj.get("comment_count") >= comments){
				user = (String)jsonObj.get("username");
				comments = (int)jsonObj.get("comment_count");
			}
		}
		return user;
	}
	
	public static List<String> getUsernamesSortedByRecordDate(int threshold) {
		// The list of the authors sorted by when their record was created according to a set threshold.
		List<String> userList = new ArrayList<String>();
		for( Object jobj : cache) {
			JSONObject jsonObj = (JSONObject) jobj;
			if ( (int)jsonObj.get("created_at") >= threshold)
				userList.add((String)jsonObj.get("username"));
		}
		Collections.sort(userList, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		});
		return userList;
	}

	public static void main(final String[] args) {
		// get the json and save it in cache
		System.out.println("Begin pulling data \n");
		try{
			//begin with first page
			int page = 1;
			String json = getJSONCache(returnURLFromPage(page));
			JSONObject root = new JSONObject(json);
			
			//Get the total number of pages and total pages/data 
			//This is important for looping
			Total_pages = root.getInt("total_pages");
			
			//cache the current data
			cache = root.getJSONArray("data");

			if (Total_pages > 1){
				while (page < Total_pages){
					++page;
					json = getJSONCache(returnURLFromPage(page));
					JSONArray jsonArray = new JSONObject(json).getJSONArray("data");

					for( Object jobj : jsonArray) {
						cache.put((JSONObject) jobj);
					}
				}
			}

			System.out.println("Success! All data is cached \n");
			System.out.println("Begining test: \n");

			System.out.println("List of users with the top submissions:");
			System.out.println(getUsernames(20));
			System.out.println("\n");
			System.out.println("User with the most comments:");
			System.out.println(getUsernameWithHighestCommentCount());
			System.out.println("\n");
			System.out.println("Sorted list of users that joined at a time:");
			System.out.println(getUsernamesSortedByRecordDate(1305392958));
			System.out.println("\n");

		} catch(final Exception e) {
            System.out.println("ERROR: " + e);
        }

	}

}
