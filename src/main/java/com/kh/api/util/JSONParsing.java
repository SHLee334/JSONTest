package com.kh.api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kh.api.model.vo.Festival;

public class JSONParsing {

	public static void main(String[] args) {

		String url = "http://openapi.seoul.go.kr:8088/63795059616d677838387250726649/json/culturalEventInfo/1/10/";

		try {
			Reader r = Resources.getResourceAsReader("mybatis-config.xml");
			SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(r);
			SqlSession session = factory.openSession();

			URL requestUrl = new URL(url);
			HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();
			urlConnection.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line = null;

			StringBuffer responseBuffer = new StringBuffer();

			while ((line = br.readLine()) != null) {
				responseBuffer.append(line);
			}

			br.close();
			urlConnection.disconnect();

			String responseData = responseBuffer.toString();
			JSONObject jsonResponse = new JSONObject(responseData); // pom.xml에 json관련 dependency 추가 했음

			JSONObject culturalEventInfo = jsonResponse.getJSONObject("culturalEventInfo");

			JSONArray row = culturalEventInfo.getJSONArray("row");

			for (int i = 0; i < row.length(); i++) {
				JSONObject result = row.getJSONObject(i);
				// 축제코드 자바코드 (auto_increment)
				String title = result.getString("TITLE");
				String startDate = result.getString("STRTDATE");
				String endDate = result.getString("END_DATE");
				// 축제상태 자바코드 (오늘날짜 / END_DATE 비교)
				String place = result.getString("PLACE");
				String lat = result.getString("LAT");
				String lot = result.getString("LOT");
				String feeStatus = result.getString("IS_FEE");
				String fee = result.getString("USE_FEE");
				String age = result.getString("USE_TRGT");
				String enrollDate = result.getString("RGSTDATE");
				String operator = result.getString("ORG_NAME");
				String hmpgUrl = result.getString("HMPG_ADDR");
				String poster = result.getString("MAIN_IMG");
				// 뷰카운트 자바코드
				// 시즌코드 자바코드
				String category = result.getString("CODENAME");

				System.out.println("title : " + title);
				System.out.println("startDate : " + startDate);
				System.out.println("STRTDATE : " + startDate.split(" ")[0]);
				System.out.println("END_DATE : " + endDate.split(" ")[0]);
				System.out.println();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				Festival fes = new Festival();
				// 축제 코드
				fes.setName(title);
				fes.setStartDate(sdf.parse(startDate.split(" ")[0]));
				fes.setEndDate(sdf.parse(endDate.split(" ")[0]));
				// 축제상태
				fes.setPlace(place);
				fes.setLat(lat);
				fes.setLot(lot);
				fes.setFeeStatus(feeStatus);
				fes.setFee(fee);
				fes.setAge(age);
				fes.setEnrollDate(sdf.parse(enrollDate.split(" ")[0]));
				fes.setOperator(operator);
				fes.setHmpgUrl(hmpgUrl);
				fes.setPoster(poster);
				// 뷰카운트
				// 시즌코드
				fes.setCategory(category);

				session.insert("festivalMapper", fes);
				session.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
