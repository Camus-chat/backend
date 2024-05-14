package com.camus.backend.global.util;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.mongodb.client.result.UpdateResult;

public class DBOperationCheckUtil {

	private DBOperationCheckUtil() {

	}

	public static void checkDBOperation(UpdateResult result) {
		// DB 자체 접근에 성공했는지 여부에 대한 반환
		// query 오류 등
		if (!result.wasAcknowledged()) {
			throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}
	}
}
