package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.NumbersToPortModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NumberDataType;

public class NumbersToPortDAO {

	static final Logger logger = LoggerFactory.getLogger(NumbersToPortDAO.class.getName());

	public static void insertNumberData(Connection conn, NumbersToPortModel numbersToPortModel) throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			
			NumberDataType numberDataType = numbersToPortModel.getNumberDataType();
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO NUMBERSTOPORT(NPC_MESSAGE_ID, NUMBERFROM, NUMBERTO, DATANUMBER, FAXNUMBER)"
					+ " VALUES(" + numbersToPortModel.getNPCMessageID() + ","
					+ DBTypeConverter.toSQLVARCHAR2(numberDataType.getNumberFrom()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(numberDataType.getNumberTo()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(numberDataType.getDataNumber()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(numberDataType.getFaxNumber()) + ")";
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

	public static List<NumbersToPortModel> getNumbersToPortList(Connection conn,
			NumbersToPortModel numbersToPortModel) throws SQLException, JAXBException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		ArrayList<NumbersToPortModel> numbersToPortList = new ArrayList<>();
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT NPC_MESSAGE_ID, NUMBERFROM, NUMBERTO, DATANUMBER, FAXNUMBER FROM NUMBERSTOPORT WHERE NPC_MESSAGE_ID = "
					+ numbersToPortModel.getNPCMessageID();
			rs = stmt.executeQuery(selectStmt);
			NumbersToPortModel newRow = null;
			while (rs.next()) {
				newRow = NumbersToPortModel.createNumbersToPort();
				NumbersToPortModel.set(newRow, "NPC_MESSAGE_ID", (Object) rs.getLong("NPC_MESSAGE_ID"));
				NumbersToPortModel.set(newRow, "NUMBERFROM", (Object) rs.getString("NUMBERFROM"));
				NumbersToPortModel.set(newRow, "NUMBERTO", (Object) rs.getString("NUMBERTO"));
				NumbersToPortModel.set(newRow, "DATANUMBER", (Object) rs.getString("DATANUMBER"));
				NumbersToPortModel.set(newRow, "FAXNUMBER", (Object) rs.getString("FAXNUMBER"));
				numbersToPortList.add(newRow);
			}
		}

		catch (SQLException ex) {
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
		return numbersToPortList;
	}
}
