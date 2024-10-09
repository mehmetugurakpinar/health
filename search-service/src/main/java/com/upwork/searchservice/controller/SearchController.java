package com.upwork.searchservice.controller;

import com.upwork.searchservice.dto.PatientRequest;
import com.upwork.searchservice.dto.PatientResponse;
import com.upwork.searchservice.service.PatientSearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private PatientSearchService patientSearchService;

	@GetMapping
	public ResponseEntity<?> searchPatient(@RequestParam(name = "q") String query) {
		try {
			SearchResponse searchResponse = patientSearchService.searchPatient(query);

			SearchHit[] searchHits = searchResponse.getHits().getHits();

			List<PatientResponse> patientResponses = new ArrayList<>();
			for (SearchHit hit : searchHits) {
				PatientResponse patientResponse = getPatientResponseRecord(hit);
				patientResponses.add(patientResponse);
			}

			return new ResponseEntity<>(patientResponses, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<PatientResponse> getPatientById(@PathVariable String id) throws IOException {
		PatientResponse patient = patientSearchService.getPatientById(id);

		if (patient != null) {
			return ResponseEntity.ok(patient);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/internal/patients")
	public void createPatient(@RequestBody PatientRequest patientRequest) throws IOException {
		patientSearchService.indexPatient(patientRequest);
	}

	private static PatientResponse getPatientResponseRecord(SearchHit hit) {
		Map<String, Object> sourceAsMap = hit.getSourceAsMap();

		String name = (String) sourceAsMap.get("given");
		String surname = (String) sourceAsMap.get("family");
		String birthDate = (String) sourceAsMap.get("birthDate");
		String gender = (String) sourceAsMap.get("gender");
		String phone = (String) sourceAsMap.get("phone");
		String id = (String) sourceAsMap.get("id");

		return new PatientResponse(
				name,
				surname,
				birthDate,
				gender,
				phone,
				id);
	}
}

