package com.upwork.dataservice.dto;

public record PatientResponse(
	String given,
	String family,
	String birthDate,
	String gender,
	String phoneNo,
	String id
) {}

