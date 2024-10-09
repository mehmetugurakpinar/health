package com.upwork.dataservice.dto;

public record PatientInternal(
	String id,
	String given,
	String family,
	String birthDate,
	String gender,
	String phoneNo
) {}

