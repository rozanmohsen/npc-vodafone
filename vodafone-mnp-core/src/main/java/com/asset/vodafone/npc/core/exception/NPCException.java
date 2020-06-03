package com.asset.vodafone.npc.core.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class NPCException extends Exception {

	private static final long serialVersionUID = 2808843997391063087L;

	

	public NPCException(Throwable cause) {
		super(cause);
		
	}

	public NPCException(String message) {
		super(message);

	
	}

	public NPCException(String code, String message) {
		super(message);
		this.code = code;

		

	}

	public NPCException(Throwable cause, String code, String message) {
		super(message, cause);
		this.code = code;
		
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return getMessage() != null ? getMessage() : "";

	}

	@Override
	public String getMessage() {
		return "[" + code + "]: " + super.getMessage();
	}

	public String getErrorStackTrace() {
		PrintStream printStream = null;
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			printStream = new PrintStream(outputStream);
			printStackTrace(printStream);
			return outputStream.toString();

		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
				if (printStream != null)
					printStream.close();
			} catch (Exception ex) {
				ex.getStackTrace();
				
			}
		}
	}

	public static final String GENERAL_ERROR_CODE = "NPC_GENERAL_ERROR";
	public static final String FATAL_ERROR_CODE = "NPC_FATAL_ERROR";
	public static final String NPC_WEB_SERVICE_ERROR_CODE = "NPC_WEB_SERVICE_ERROR_01";
	public static final String DATABASE_CODE_PREFIX = "NPC_DATABASE";
	public static final String DATABASE_CONNECTION_ESTABLISHING_ERROR_CODE = "NPC_DATABASE_ERROR_01";
	public static final String DATABASE_CONNECTION_NAMING_ERROR_CODE = "NPC_DATABASE_ERROR_02";
	public static final String DATABASE_CONNECTION_DRIVER_ERROR_CODE = "NPC_DATABASE_ERROR_03";
	public static final String DATABASE_SQL_SELECT_ERROR_CODE = "NPC_DATABASE_ERROR_04";
	public static final String DATABASE_SQL_INSERT_ERROR_CODE = "NPC_DATABASE_ERROR_05";
	public static final String DATABASE_SQL_UPDATE_ERROR_CODE = "NPC_DATABASE_ERROR_06";
	public static final String DATABASE_SQL_DELETE_ERROR_CODE = "NPC_DATABASE_ERROR_07";
	public static final String DATABASE_DATE_PARSING_ERROR_CODE = "NPC_DATABASE_ERROR_08";
	public static final String JAXB_CODE_PREFIX = "NPC_JAXB";
	public static final String JAXB_CREATE_OBJECT_ERROR_CODE = "NPC_JAXB_ERROR_01";
	public static final String JAXB_VALIDATION_ERROR_CODE = "NPC_JAXB_ERROR_02";
	public static final String PROCESSING_MESSAGE_CODE_NOT_FOUND_ERROR_CODE = "NPC_PROCESSING_ERROR_01";
	public static final String PROCESSING_PARTICIPANT_NOT_FOUND_ERROR_CODE = "NPC_PROCESSING_ERROR_02";
	public static final String PROCESSING_DEPENDENT_MESSAGE_CODE_NOT_FOUND_ERROR_CODE = "NPC_PROCESSING_ERROR_03";
	public static final String PROCESSING_PORT_DATA_NOT_FOUND_ERROR_CODE = "NPC_PROCESSING_ERROR_04";
	public static final String PROCESSING_FIELD_NOT_FOUND_ERROR_CODE = "NPC_PROCESSING_ERROR_05";
	public static final String PROCESSING_SYNC_FILE_NOT_FOUND_ERROR_CODE = "NPC_PROCESSING_ERROR_06";
	public static final String FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE = "Cannot load NPC properties file";
	public static final String FATAL_ERROR_LOADING_CONFIG_FILE_ERROR_MESSAGE = "Cannot load NPC process config file";
	public static final String FATAL_ERROR_SAVING_CONFIG_FILE_ERROR_MESSAGE = "Cannot save process config file";
	public static final String DATABASE_CONNECTION_ESTABLISHING_ERROR_MESSAGE = "Cannot Establish Database Connection";
	public static final String DATABASE_CONNECTION_NAMING_ERROR_MESSAGE = "Error in Connection EJB Lookup";
	public static final String DATABASE_CONNECTION_DRIVER_ERROR_MESSAGE = "Invalid or Wrong JDBC Driver";
	public static final String DATABASE_SQL_SELECT_ERROR_MESSAGE = "Error in Select statement";
	public static final String DATABASE_SQL_INSERT_ERROR_MESSAGE = "Error in Insert statement";
	public static final String DATABASE_SQL_UPDATE_ERROR_MESSAGE = "Error in Update statement";
	public static final String DATABASE_SQL_DELETE_ERROR_MESSAGE = "Error in Delete Statement";
	public static final String DATABASE_DATE_PARSING_ERROR_MESSAGE = "Error in parsing date";
	public static final String JAXB_CREATE_OBJECT_ERROR_MESSAGE = "Cannot Create JAXB Object";
	public static final String JAXB_VALIDATION_ERROR_MESSAGE = "JAXB Validation Error";
	public static final String PROCESSING_MESSAGE_CODE_NOT_FOUND_ERROR_MESSAGE = "Message Code \"{0}\" not found in current Participant message codes";
	public static final String PROCESSING_PARTICIPANT_NOT_FOUND_ERROR_MESSAGE = "Participant \"{0}\" not found  please contact your Database administrator or check the resource file";
	public static final String PROCESSING_DEPENDENT_MESSAGE_CODE_NOT_FOUND_ERROR_MESSAGE = "Dependent message code not found for NPC message \"{0}\" with code \"{1}\"";
	public static final String PROCESSING_PORT_DATA_NOT_FOUND_ERROR_MESSAGE = "No port data found for Message ID \"{0)\"";
	public static final String PROCESSING_FIELD_NOT_FOUND_ERROR_MESSAGE = "The field \"{0}\" is not found in Port Message Table please check the name of this field in the properties file";
	public static final String PROCESSING_SYNC_FILE_NOT_FOUND_ERROR_MESSAGE = "Sync File is not found";
	public static final String NPC_WEB_SERVICE_ERROR_MESSAGE = "Error in sending npc message";
	public static final String GENERAL_ERROR_MESSAGE = "Unknown Error";
	String code;
}
