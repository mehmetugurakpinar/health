package com.upwork.dataservice.dto;

public record PatientRequest(
	String id,
	String given,
	String family,
	String birthDate,
	String gender,
	String phoneNo
) {}

