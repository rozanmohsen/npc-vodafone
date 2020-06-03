package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.PortDataModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;

public class PortDataDAO {

	static final Logger logger = LoggerFactory.getLogger(PortDataDAO.class.getName());
	
	public static void insertPortData(Connection conn, PortDataModel portDataModel) throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO PORT_DATA(PORT_DATA_ID,PORTID,DONORID,RECIPIENTID,PORTINGRQUESTFORMID,PORT_STATUS,SR,TRANSFERFEE,AVERAGEINVOICEFEE,NPR_NPC_MESSAGE_ID,LAST_NPC_MESSAGE_ID,OLD_SIM_NUMBER,ACCOUNT_NUMBER,ERROR_MESSAGE_RECEIVED,NEWROUTE,PAYMENTDUE,INTERNAL_PORT_ID,TRANSACTION_STATUS,COMMENTS)  VALUES ("
					+ DBTypeConverter.toSQLNumber(portDataModel.getPortDataID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getDonorID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getRecipientID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortingRequestFormID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortStatus()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getSr()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getTransferFee()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getAverageInvoiceFee()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getNprNPCMessageID()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getLastNPCMessageID()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getOldSimNumber()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getAccountNumber()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getErrorMessageReceived()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getNewRoute()) + ","
					+ DBTypeConverter.toSQLNumber(portDataModel.getPaymentDue()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getInternalPortID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getTransactionStatus()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getComments()) + ")";
			stmt.execute(insertStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + insertStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static void updatePortData(Connection conn, PortDataModel portDataModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE PORT_DATA SET PORTID = " + DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortID())
					+ "," + "PORT_STATUS" + " = " + DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortStatus()) + ","
					+ "LAST_NPC_MESSAGE_ID" + " = " + DBTypeConverter.toSQLNumber(portDataModel.getLastNPCMessageID())
					+ "," + "DONORID" + " = " + DBTypeConverter.toSQLVARCHAR2(portDataModel.getDonorID()) + ","
					+ "RECIPIENTID" + " = " + DBTypeConverter.toSQLVARCHAR2(portDataModel.getRecipientID()) + ","
					+ "PORTINGRQUESTFORMID" + " = "
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortingRequestFormID()) + "," + "NEWROUTE" + " = "
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getNewRoute()) + "," + "TRANSFERFEE" + " = "
					+ DBTypeConverter.toSQLNumber(portDataModel.getTransferFee()) + "," + "AVERAGEINVOICEFEE" + " = "
					+ DBTypeConverter.toSQLNumber(portDataModel.getAverageInvoiceFee()) + "," + "PAYMENTDUE" + " = "
					+ DBTypeConverter.toSQLNumber(portDataModel.getPaymentDue()) + "," + "TRANSACTION_STATUS" + " = "
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getTransactionStatus()) + ","
					+ "ERROR_MESSAGE_RECEIVED" + " = "
					+ DBTypeConverter.toSQLNumber(portDataModel.getErrorMessageReceived()) + "," + "COMMENTS" + " = "
					+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getComments()) + " WHERE " + "PORT_DATA_ID" + " = "
					+ DBTypeConverter.toSQLNumber(portDataModel.getPortDataID());
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static void updatePortStatus(Connection conn, PortDataModel portDataModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE PORT_DATA SET PORT_STATUS = '" + portDataModel.getPortStatus() + "'" + " WHERE "
					+ "PORT_DATA_ID" + " = " + portDataModel.getPortDataID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static void updateLastNPCMessageID(Connection conn, PortDataModel portDataModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE PORT_DATA SET LAST_NPC_MESSAGE_ID = " + portDataModel.getLastNPCMessageID() + " WHERE "
					+ "PORT_DATA_ID" + " = " + portDataModel.getPortDataID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static PortDataModel getPortDataModel(Connection conn, PortDataModel portDataModel) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT PORT_DATA_ID,INTERNAL_PORT_ID,PORTID,DONORID,RECIPIENTID,PORTINGRQUESTFORMID,PORT_STATUS,ERROR_MESSAGE_RECEIVED,SR,TRANSFERFEE,AVERAGEINVOICEFEE,LAST_NPC_MESSAGE_ID,NPR_NPC_MESSAGE_ID,COMMENTS,NEWROUTE,PAYMENTDUE,ERROR_MESSAGE_RECEIVED,INTERNAL_PORT_ID FROM PORT_DATA WHERE ";
			String portQuery = "";
			if (portDataModel.getPortID() != null && !"".equals(portDataModel.getPortID()))
				portQuery = "PORT_DATA.PORTID = " + DBTypeConverter.toSQLVARCHAR2(portDataModel.getPortID());
			else
				portQuery = "PORT_DATA.INTERNAL_PORT_ID="
						+ DBTypeConverter.toSQLVARCHAR2(portDataModel.getInternalPortID());
			selectStmt = selectStmt + portQuery + " ORDER BY " + "CREATE_DATE" + " DESC ";
			rs = stmt.executeQuery(selectStmt);
			PortDataModel newPortDataRow;
			newPortDataRow = null;
			if (rs.next()) {
				newPortDataRow = PortDataModel.getInstance();
				newPortDataRow.setPortDataID(rs.getLong("PORT_DATA_ID"));
				newPortDataRow.setPortID(rs.getString("PORTID"));
				newPortDataRow.setDonorID(rs.getString("DONORID"));
				newPortDataRow.setRecipientID(rs.getString("RECIPIENTID"));
				newPortDataRow.setPortingRequestFormID(rs.getString("PORTINGRQUESTFORMID"));
				newPortDataRow.setPortStatus(rs.getString("PORT_STATUS"));
				newPortDataRow.setErrorMessageReceived(rs.getShort("ERROR_MESSAGE_RECEIVED"));
				newPortDataRow.setSr(rs.getString("SR"));
				newPortDataRow.setTransferFee(rs.getLong("TRANSFERFEE"));
				newPortDataRow.setAverageInvoiceFee(rs.getLong("AVERAGEINVOICEFEE"));
				newPortDataRow.setNprNPCMessageID(rs.getLong("NPR_NPC_MESSAGE_ID"));
				newPortDataRow.setLastNPCMessageID(rs.getLong("LAST_NPC_MESSAGE_ID"));
				newPortDataRow.setComments(rs.getString("COMMENTS"));
				newPortDataRow.setNewRoute(rs.getString("NEWROUTE"));
				newPortDataRow.setPaymentDue(rs.getLong("PAYMENTDUE"));
				newPortDataRow.setErrorMessageReceived(rs.getShort("ERROR_MESSAGE_RECEIVED"));
				newPortDataRow.setInternalPortID(rs.getString("INTERNAL_PORT_ID"));
			}
			return newPortDataRow;
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
