package com.asset.vodafone.npc.core.dao;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.SubscriberDataModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;
import com.asset.vodafone.npc.webservice.xsd.portmessage.SubscriberDataType;

public class SubscriberDataDAO {
	private static final Logger logger = LoggerFactory.getLogger(SubscriberDataDAO.class.getName());

	private SubscriberDataDAO() {

	}

	public static void insertSubscriberData(Connection conn, SubscriberDataModel subscriberDataModel)
			throws SQLException {

		Statement stmt = null;
		String insertStmt = "";
		SubscriberDataType subscriberDataType = subscriberDataModel.getSubscriberDataType();
		String corporationName = subscriberDataType.getCorporationName();
		String name = subscriberDataType.getName();

		if (corporationName != null && !"".equals(corporationName.trim()))

			subscriberDataType.setCorporationName(
					new String(corporationName.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

		subscriberDataType.setCorporationName(corporationName);

		if (name != null && !"".equals(name.trim()))

			subscriberDataType.setName(new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

		try {

			stmt = conn.createStatement();
			insertStmt = "INSERT INTO SUBSCRIBER_DATA(NPC_MESSAGE_ID, COMPANYFLAG, CORPORATIONNAME, NAME, ACCOUNTNUM, SIMCARDNUM, NIC, CNIC, PASSPORTNUM, OTHERID, DATEOFBIRTH, CONTACTPHONE, FAX, CITY, STREET, LOCALITY, POSTCODE, GROUP_ID, AUTHORIZED_PERSON_NAME, CORPORATION_ADDRESS, COMMERCIAL_REGISTRATION_NUM, TAX_REGISTRATION_NUM)"
					+ "VALUES (" + DBTypeConverter.toSQLNumber(subscriberDataModel.getNPCMessageID()) + ",";
			String comapnyFlag = subscriberDataType.getCompanyFlag();
			if (comapnyFlag != null && (comapnyFlag == "true" || comapnyFlag.equals("true")))
				insertStmt = insertStmt + DBTypeConverter.toSQLVARCHAR2("1") + ",";
			else
				insertStmt = insertStmt + DBTypeConverter.toSQLVARCHAR2("0") + ",";
			insertStmt = insertStmt + DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getCorporationName()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getName()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getAccountNum()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getSIMCardNum()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getNIC()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getCNIC()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getPassportNum()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getOtherId()) + ","
					+ DBTypeConverter.toSQLNumber(subscriberDataType.getDateOfBirth()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getContactPhone()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getFax()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getCity()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getStreet()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getLocality()) + ","
					+ DBTypeConverter.toSQLNumber(subscriberDataType.getPostCode()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getGroupID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getAuthorizedPersonName()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getCorporationAddress()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getCommercialRegistrationNum()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(subscriberDataType.getTaxRegistrationNum()) + ")";
			stmt.execute(insertStmt);

		} catch (SQLException ex) {
			String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + insertStmt + "]");
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public static SubscriberDataModel getSubscriberData(Connection conn, SubscriberDataModel subscriberDataModel)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT NPC_MESSAGE_ID, COMPANYFLAG, CORPORATIONNAME, NAME, ACCOUNTNUM, SIMCARDNUM, NIC, CNIC, PASSPORTNUM, OTHERID, DATEOFBIRTH, CONTACTPHONE, FAX, CITY, STREET, SUBSCRIBER_NUMBER, LOCALITY, POSTCODE, GROUP_ID, AUTHORIZED_PERSON_NAME, CORPORATION_ADDRESS, COMMERCIAL_REGISTRATION_NUM, TAX_REGISTRATION_NUM FROM SUBSCRIBER_DATA WHERE NPC_MESSAGE_ID = "
					+ subscriberDataModel.getNPCMessageID();
			rs = stmt.executeQuery(selectStmt);
			boolean subscriberDataFound;
			for (subscriberDataFound = false; rs.next(); subscriberDataFound = true) {
				SubscriberDataModel.set(subscriberDataModel, "NPC_MESSAGE_ID", rs.getLong("NPC_MESSAGE_ID"));
				SubscriberDataModel.set(subscriberDataModel, "COMPANYFLAG", rs.getString("COMPANYFLAG"));
				SubscriberDataModel.set(subscriberDataModel, "CORPORATIONNAME", rs.getString("CORPORATIONNAME"));
				SubscriberDataModel.set(subscriberDataModel, "NAME", rs.getString("NAME"));
				SubscriberDataModel.set(subscriberDataModel, "ACCOUNTNUM", rs.getString("ACCOUNTNUM"));
				SubscriberDataModel.set(subscriberDataModel, "SIMCARDNUM", rs.getString("SIMCARDNUM"));
				SubscriberDataModel.set(subscriberDataModel, "NIC", rs.getString("NIC"));
				SubscriberDataModel.set(subscriberDataModel, "CNIC", rs.getString("CNIC"));
				SubscriberDataModel.set(subscriberDataModel, "PASSPORTNUM", rs.getString("PASSPORTNUM"));
				SubscriberDataModel.set(subscriberDataModel, "OTHERID", rs.getString("OTHERID"));
				SubscriberDataModel.set(subscriberDataModel, "DATEOFBIRTH", rs.getString("DATEOFBIRTH"));
				SubscriberDataModel.set(subscriberDataModel, "CONTACTPHONE", rs.getString("CONTACTPHONE"));
				SubscriberDataModel.set(subscriberDataModel, "FAX", rs.getString("FAX"));
				SubscriberDataModel.set(subscriberDataModel, "CITY", rs.getString("CITY"));
				SubscriberDataModel.set(subscriberDataModel, "STREET", rs.getString("STREET"));
				SubscriberDataModel.set(subscriberDataModel, "SUBSCRIBER_NUMBER", rs.getString("SUBSCRIBER_NUMBER"));
				SubscriberDataModel.set(subscriberDataModel, "LOCALITY", rs.getString("LOCALITY"));
				SubscriberDataModel.set(subscriberDataModel, "POSTCODE", rs.getString("POSTCODE"));
				SubscriberDataModel.set(subscriberDataModel, "GROUP_ID", rs.getString("GROUP_ID"));
				SubscriberDataModel.set(subscriberDataModel, "AUTHORIZED_PERSON_NAME",
						rs.getString("AUTHORIZED_PERSON_NAME"));
				SubscriberDataModel.set(subscriberDataModel, "CORPORATION_ADDRESS",
						rs.getString("CORPORATION_ADDRESS"));
				SubscriberDataModel.set(subscriberDataModel, "COMMERCIAL_REGISTRATION_NUM",
						rs.getString("COMMERCIAL_REGISTRATION_NUM"));
				SubscriberDataModel.set(subscriberDataModel, "TAX_REGISTRATION_NUM",
						rs.getString("TAX_REGISTRATION_NUM"));
			}

			if (!subscriberDataFound)
				subscriberDataModel.setSubscriberDataType(null);
		} catch (SQLException ex) {
			String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return subscriberDataModel;
	}
}
