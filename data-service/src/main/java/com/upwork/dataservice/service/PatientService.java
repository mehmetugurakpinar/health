package com.upwork.dataservice.service;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import com.upwork.dataservice.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PatientService {

	private final RestTemplate restTemplate;

	@Value("${aidbox.url}")
	private String aidboxUrl;

	@Value("${aidbox.username}")
	private String username;

	@Value("${aidbox.password}")
	private String password;

	public PatientService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public PatientResponse upsertPatient(PatientRequest patientRequest) {
		Patient patient = upsertPatientAidbox(patientRequest);

		return upsertPatientElastic(patient.id(), patientRequest);
	}

	private PatientResponse upsertPatientElastic(String id, PatientRequest patientRequest) {
		String internalUrl = "http://localhost:8081/search/internal/patients";
		PatientResponse patientResponse = new PatientResponse(
				patientRequest.given(),
				patientRequest.family(),
				patientRequest.birthDate(),
				patientRequest.gender(),
				patientRequest.phoneNo(),
				id
		);

		PatientInternal patientInternalRecord = new PatientInternal(
				id,
				patientRequest.given(),
				patientRequest.family(),
				patientRequest.birthDate(),
				patientRequest.gender(),
				patientRequest.phoneNo()
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<PatientInternal> request = new HttpEntity<>(patientInternalRecord, headers);

		ResponseEntity<String> responseInternal = restTemplate.postForEntity(internalUrl, request, String.class);

		if (responseInternal.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException("Failed to upsert patient in ElasticSearch");
		}

		return patientResponse;
	}

	private Patient upsertPatientAidbox(PatientRequest patientRequest) {
		String patientResourceUrl = aidboxUrl + "/Patient";

		List<Name> names = new ArrayList<>();
		Name name = new Name(patientRequest.family(), List.of(patientRequest.given()));
		names.add(name);

		List<Contact> contacts = new ArrayList<>();
		Contact contact = new Contact(patientRequest.phoneNo(), ContactPointSystemEnum.PHONE.getCode());
		contacts.add(contact);

		Patient patient = new Patient(
				patientRequest.id() == null ? UUID.randomUUID().toString() : patientRequest.id(),
				names,
				patientRequest.birthDate(),
				AdministrativeGenderEnum.MALE.getCode(),
				contacts
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(username, password);

		HttpEntity<Patient> request = new HttpEntity<>(patient, headers);
		ResponseEntity<String> response;

		if (patientRequest.id() == null) {
			response = restTemplate.exchange(patientResourceUrl, HttpMethod.POST, request, String.class);
		} else {
			patientResourceUrl += "/" + patientRequest.id();
			response = restTemplate.exchange(patientResourceUrl, HttpMethod.PUT, request, String.class);
		}

		if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
			throw new RuntimeException("Failed to upsert patient in Aidbox");
		}

		return patient;
	}
}
