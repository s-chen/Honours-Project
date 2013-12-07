package com.basewarp.basewarp.services;

import java.util.HashMap;

public interface ServiceListener {
	
	public enum StatusCode{
	
		CONNECTION_TIMEOUT(101),
		ADDRESS_CONNECTION_TIMEOUT(102),
		AUTHENTICATION_FAILED(103),
		CONNECTION_FAILED(104),

		SUCCESS(201),
		UNKNOWN(-1),

		USER_DATA_OK(202),
		USER_DATA_FAILED(206),

		IMAGE_DATA_OK(203),
		ADD_BUBBLE_OK(204),
		BUBBLE_DATA_OK(205),

		LOGIN_VALID(301),
		LOGIN_INVALID_PASSWORD(302),
		LOGIN_INVALID_USERNAME(303),
		LOGIN_STARTED(304),

		ACCOUNT_MERGE_FAILED(908),
		ACCOUNT_MERGE_INACTIVE(907),
		ACCOUNT_CREATED_FACEBOOK(906),
		ACCOUNT_MERGED(905),
		ACCOUNT_CREATED_INACTIVE(904),
		ACCOUNT_CREATE_FAILED(903),
		ACCOUNT_UNKNOWN(902),
		ACCOUNT_INACTIVE(901),

		ADDRESS_OK(401),
		ADDRESS_LIST_OK(402),
		ADDRESS_TIMEZONE_OK(403),
		ADDRESS_TIMEZONE_FAILED(404),

		RATING_OK(501),
		RATING_FAILED(502),

		PW_RESET_EMAIL_SENT(601),
		PW_RESET_FAILED(602),
		PW_RESET_INVALID_EMAIL(603),
		UPDATE_INFO_OK(603),
		UPDATE_INFO_FAILED(604),
		UPDATE_INFO_PW_INCORRECT(605),

		ADD_BUBBLE_IMAGE_FAILED(702),
		ADD_BUBBLE_FAILED(701),
		BUBBLES_EMPTY(801)
		;
		
		private int codeNumber;
		private StatusCode(int codeNum) {
			this.codeNumber = codeNum;
		}
		
		public int getCodeAsInt() {
			return codeNumber;
		}
		
		public static StatusCode getEnumByCode(int key) {
			for(StatusCode c : values()) {
				if(c.getCodeAsInt() == key) return c;
			}
			return null;
		}
		
	}

	void beforeStart();
	void onComplete(Object data, StatusCode rc);
}
