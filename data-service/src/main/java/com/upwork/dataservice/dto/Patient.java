package com.upwork.dataservice.dto;

import java.util.List;

public record Patient(
		String id,
		List<Name> name,
		String birthDate,
		String gender,
		List<Contact> telecom
) {}
