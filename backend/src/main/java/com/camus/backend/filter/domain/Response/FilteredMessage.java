package com.camus.backend.filter.domain.Response;

import java.time.LocalDateTime;

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
	String type = "FilteredMessage";
	Long id;
	FilteredType filteredType;
	LocalDateTime createdDate;

	public FilteredMessage(Long id, FilteredType filteredType, LocalDateTime createdDate) {
		this.type = "FilteredMessage";
		this.id = id;
		this.filteredType = filteredType;
		this.createdDate = createdDate;
	}
}
