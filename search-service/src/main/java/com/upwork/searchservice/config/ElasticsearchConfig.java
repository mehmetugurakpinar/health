package com.upwork.searchservice.config;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

	@Bean
	public RestClient elasticsearchRestClient() {
		return RestClient.builder(HttpHost.create("localhost:9200"))
				.setHttpClientConfigCallback(httpClientBuilder -> {
					httpClientBuilder.addInterceptorLast((HttpResponseInterceptor) (response, context) -> {
						response.addHeader("X-Elastic-Product", "Elasticsearch");
					});
					return httpClientBuilder;
				})
				.setDefaultHeaders(new org.apache.http.Header[]{
						new org.apache.http.message.BasicHeader("Content-Type", "application/json")
				})
				.build();
	}

	@Bean
	public RestHighLevelClient openSearchClient() {
		RestClientBuilder builder = RestClient.builder(
				new HttpHost("localhost", 9200, "http"));
		return new RestHighLevelClient(builder);
	}
}