package com.upwork.searchservice.model;

public record Patient (
	String id,
	String given,
	String family,
	String birthDate,
	String gender,
	String phone,
	String fullName
) {}

