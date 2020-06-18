package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.NumbersToPortModel;
import com.asset.vodafone.npc.core.models.ParticipantModel;
import com.asset.vodafone.npc.core.models.PortMessageModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;

public class GenericDAO {
	static final Logger logger = LoggerFactory.getLogger(GenericDAO.class.getName());

	private GenericDAO() {
	}

	/**
	 * Method to get Next value of sequence of column in database table
	 * 
	 * @param conn
	 * @param sequenceName
	 * @return
	 * @throws SQLException
	 */
	public static long getNextValueOfSequence(Connection conn, String sequenceName) throws SQLException {
		Statement stmt = null;
		ResultSet resultSet = null;
		String selectStmt = "";
		try {
			selectStmt = "SELECT " + sequenceName + ".NEXTVAL FROM dual";
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(selectStmt);
			if (!resultSet.next()) {
				return -1L;
			}
			return resultSet.getLong(1);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	/**
	 * Method to get Current date Time
	 * 
	 * @param conn
	 * @param format
	 * @return
	 * @throws SQLException
	 */
	public static String getCurrentDateTime(Connection conn, String format) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		if (format == null) {
			format = "dd/MM/yyyy hh24:mi:ss";
		}
		String sql = "";
		try {
			sql = "SELECT TO_CHAR(SYSDATE,'" + format + "') AS \"SYSDATE\" FROM DUAL";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			String sysDate = null;

			while (rs.next())
				sysDate = rs.getString("SYSDATE");
			return sysDate;
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + sql + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Generic Method to insert into database table it takes DB Schema and table
	 * name and fields(columns)
	 * 
	 * @param conn
	 * @param schemaName
	 * @param tableName
	 * @param fields
	 * @return boolean
	 * @throws SQLException
	 */
	public static boolean insertIntoTable(Connection conn, String schemaName, String tableName,
			Map<String, String> fields) throws SQLException {
		Statement stmt;
		ResultSet rs;
		String insertStmt;
		stmt = null;
		rs = null;
		Object[] insertColNames = fields.keySet().toArray();

		StringBuilder insertFields = new StringBuilder();
		insertFields.append("INSERT INTO ");
		insertFields.append(tableName);
		insertFields.append("(");

		StringBuilder insertValues = new StringBuilder();
		insertValues.append(" VALUES(");
		String fieldValue = "";
		DatabaseMetaData metaData = conn.getMetaData();
		rs = metaData.getColumns(null, schemaName.toUpperCase(), tableName.toUpperCase(), null);
		String columnType = "";
		String columnName = "";
		while (rs.next()) {
			columnName = rs.getString("COLUMN_NAME");
			columnType = rs.getString("TYPE_NAME");
			for (int i = 0; i < insertColNames.length; i++)
				if (columnName.equalsIgnoreCase(String.valueOf(insertColNames[i]))) {
					fieldValue = String.valueOf(fields.get(insertColNames[i]));
					insertFields.append(columnName);
					insertFields.append(",");

					if (columnType.equalsIgnoreCase("Number")) {
						insertValues.append("TO_NUMBER(");
						insertValues.append(fieldValue);
						insertValues.append("),");

					} else if (columnType.equalsIgnoreCase("Varchar2")) {
						insertValues.append("TO_CHAR('");
						insertValues.append(fieldValue);
						insertValues.append("'),");
					} else if (columnType.equalsIgnoreCase("Date")
							|| columnType.toUpperCase().startsWith("TimeStamp".toUpperCase())) {
						insertValues.append("TO_DATE('");
						insertValues.append(fieldValue);
						insertValues.append("','");
						insertValues.append("DD/MM/YYYY HH24:MI:SS");
						insertValues.append("'),");
					}

				}
		}
		insertStmt = insertFields.substring(0, insertFields.length() - 1) + ")"
				+ insertValues.substring(0, insertValues.length() - 1) + ")";
		try {
			stmt = conn.createStatement();
			return stmt.execute(insertStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + insertStmt + "]");
		} finally {

			rs.close();

			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Get Participant model(participant Name , participant ID) from participant
	 * table
	 * 
	 * @param conn
	 * @param participantModel
	 * @return ParticipantModel
	 * @throws SQLException
	 */
	public static ParticipantModel getParticipantModel(Connection conn, ParticipantModel participantModel)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			selectStmt = "SELECT PARTICIPANT_ID,PARTICIPANT_NAME FROM PARTICIPANT WHERE PARTICIPANT_ID = "
					+ DBTypeConverter.toSQLVARCHAR2(participantModel.getParticipantID());
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectStmt);
			ParticipantModel resultParticipantModel = null;
			for (; rs.next(); resultParticipantModel.setParticipantName(rs.getString("PARTICIPANT_NAME"))) {
				resultParticipantModel = new ParticipantModel();
				resultParticipantModel.setParticipantID(rs.getString("PARTICIPANT_ID"));
			}

			return resultParticipantModel;

		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method to check if PORT_ID of XML Message is Exist or not in 'PORT_DATA'
	 * table
	 * 
	 * @param conn
	 * @param portID
	 * @return boolean
	 * @throws SQLException
	 */
	public static boolean isPortExists(Connection conn, String portID) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "SELECT PORT_DATA.PORT_DATA_ID FROM PORT_DATA WHERE PORT_DATA.PORTID="
				+ DBTypeConverter.toSQLVARCHAR2(portID);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectStmt);
			return rs.next();
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method that retrieve Internal_port_id from PORT_DATA table for specific
	 * PortMessageModel
	 * 
	 * @param conn
	 * @param portMessageModel
	 * @return long INTERNAL_PORT_ID
	 * @throws SQLException
	 */
	public static long getInternalPortIDByPortIDOrMSISDN(Connection conn, PortMessageModel portMessageModel)
			throws SQLException {
		Statement stmt;
		ResultSet rs;
		String selectStmt;
		NumbersToPortModel numbersToPortModel = portMessageModel.getNumbersToPortList().get(0);
		stmt = null;
		rs = null;
		selectStmt = "";
		boolean portExists = false;
		if (portMessageModel.getPortMessageType().getPortID() != null
				&& !"".equals(portMessageModel.getPortMessageType().getPortID())) {
			portExists = isPortExists(conn, portMessageModel.getPortMessageType().getPortID());
			if (portExists)
				selectStmt = "SELECT DISTINCT PORT_DATA.INTERNAL_PORT_ID FROM PORT_DATA WHERE PORT_DATA.PORTID = "
						+ DBTypeConverter.toSQLVARCHAR2(portMessageModel.getPortMessageType().getPortID());
		}
		if (!portExists)
			selectStmt = "SELECT DISTINCT PORT_DATA.INTERNAL_PORT_ID, PORT_DATA.CREATE_DATE FROM PORT_DATA WHERE TRANSACTION_STATUS = "
					+ DBTypeConverter.toSQLVARCHAR2("Success")
					+ " AND PORTID IS NULL  AND NPR_NPC_MESSAGE_ID IN (SELECT DISTINCT NUMBERSTOPORT.NPC_MESSAGE_ID FROM NUMBERSTOPORT WHERE (NUMBERSTOPORT.NUMBERFROM = "
					+ DBTypeConverter.toSQLVARCHAR2(numbersToPortModel.getNumberDataType().getNumberFrom())
					+ " AND NUMBERSTOPORT.NUMBERTO = "
					+ DBTypeConverter.toSQLVARCHAR2(numbersToPortModel.getNumberDataType().getNumberTo()) + ")"
					+ " OR( NUMBERSTOPORT.NUMBERFROM = "
					+ DBTypeConverter.toSQLVARCHAR2(numbersToPortModel.getNumberDataType().getNumberFrom())
					+ " AND NUMBERSTOPORT.NUMBERTO IS NULL))" + " ORDER BY " + "CREATE_DATE" + " DESC ";
		portMessageModel.setPortExists(portExists);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectStmt);
			if (!rs.next()) {
				return -1L;
			}
			return rs.getLong("INTERNAL_PORT_ID");
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}
