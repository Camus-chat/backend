package com.camus.backend.manage.controller;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.manage.domain.dto.RoomListDto;
import com.camus.backend.manage.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/room")
public class RoomController {

	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@Operation(
		summary = "방 리스트 조회",
		description = "전체 채팅방 리스트를 조회하는 api"
	)
	@GetMapping("/list")
	public ResponseEntity<RoomListDto> getRoomList() {
		// TODO : ROOM 받아오는 로직 구현 -> Redis
		return ResponseEntity.ok(RoomListDto.builder().roomList(new ArrayList<>()).build());
	}

}
