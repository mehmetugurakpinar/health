package com.upwork.dataservice.controller;

import com.upwork.dataservice.dto.PatientRequest;
import com.upwork.dataservice.dto.PatientResponse;
import com.upwork.dataservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@Validated
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@PostMapping
	public ResponseEntity<PatientResponse> upsertPatient(@Valid @RequestBody PatientRequest patientRequest) {
		PatientResponse patientResponse = patientService.upsertPatient(patientRequest);
		if (patientResponse != null) {
			return ResponseEntity.ok(patientResponse);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
