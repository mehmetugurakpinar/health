package com.upwork.searchservice.dto;

public record PatientRequest(
	String id,
	String given,
	String family,
	String birthDate,
	String gender,
	String phoneNo
) {}

