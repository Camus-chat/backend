package com.camus.backend.filter.domain.Response;

import com.camus.backend.filter.util.type.FilteredType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FilteredMessage {
	Long id;
	FilteredType filteredType;
}
