package com.jeltechnologies.screenmusic.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TestRecord( @JsonProperty(value="windows_batch_file") String field, List<String> moreFields) {

}
