package com.upwork.dataservice.dto;

import java.util.List;

public record Name(
	String family,
	List<String> given
) {}
