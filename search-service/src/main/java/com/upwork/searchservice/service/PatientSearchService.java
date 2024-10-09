package com.upwork.searchservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upwork.searchservice.dto.PatientRequest;
import com.upwork.searchservice.model.Patient;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import com.upwork.searchservice.dto.PatientResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class PatientSearchService {

	public static final String PATIENTS = "patients";
	private final RestHighLevelClient elasticsearchClient;

	public PatientSearchService(RestHighLevelClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}

	@Transactional(readOnly = true)
	public SearchResponse searchPatient(String keyword) throws Exception {
		if (keyword == null || keyword.length() < 3) {
			throw new IllegalArgumentException("Search keyword must be at least 3 characters long.");
		}
		var sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("fullName", "*" + keyword.toLowerCase() + "*").fuzziness("AUTO"));

		var searchRequest = new SearchRequest(PATIENTS);
		searchRequest.source(sourceBuilder);

		return elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
	}

	@Transactional(readOnly = true)
	public PatientResponse getPatientById(String id) throws IOException {
		var getRequest = new GetRequest(PATIENTS, id);
		var getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);

		if (!getResponse.isExists()) {
			return null;
		}

		var name = (String) getResponse.getSourceAsMap().get("given");
		var surname = (String) getResponse.getSourceAsMap().get("family");
		var birthDate = (String) getResponse.getSourceAsMap().get("birthDate");
		var gender = (String) getResponse.getSourceAsMap().get("gender");
		var phone = (String) getResponse.getSourceAsMap().get("phone");

		return new PatientResponse(
				name,
				surname,
				birthDate,
				gender,
				phone,
				id);
	}

	public void indexPatient(PatientRequest patientRequest) throws IOException {
		var patient = new Patient(
				patientRequest.id(),
				patientRequest.given(),
				patientRequest.family(),
				patientRequest.birthDate(),
				patientRequest.gender(),
				patientRequest.phoneNo(),
				patientRequest.given() + " " + patientRequest.family());
		var request = new IndexRequest(PATIENTS);
		request.id(patient.id());
		request.source(convertToJson(patient), XContentType.JSON);
		elasticsearchClient.index(request, RequestOptions.DEFAULT);
	}

	private String convertToJson(Patient patient) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(patient);
	}
}
