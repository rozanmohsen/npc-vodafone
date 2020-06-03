package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.TimeSlotModel;

public class TimeSlotDAO {
	private static final Logger logger = LoggerFactory.getLogger(TimeSlotDAO.class.getName());
	private TimeSlotDAO() {
		
	}


	public static TimeSlotModel getTimeSlotValue(Connection conn, TimeSlotModel timeSlotModel) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			selectStmt = "SELECT TIME_SLOT_NAME, TIME_SLOT.IS_BUSINESS, TIME_UNIT.TIME_UNIT_ID, TIME_UNIT.TIME_UNIT_NAME, TIME_SLOT.TIME_VALUE FROM TIME_SLOT, TIME_UNIT WHERE TIME_SLOT.TIME_UNIT_ID = TIME_UNIT.TIME_UNIT_ID AND TIME_SLOT.TIME_SLOT_NAME= '"
					+ timeSlotModel.getTimeSlotName() + "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectStmt);
			if (!rs.next()) {
				return null;
			}
			timeSlotModel.setTimeUnitID(rs.getShort("TIME_UNIT_ID"));
			timeSlotModel.setTimeSlotName(rs.getString("TIME_SLOT_NAME"));
			timeSlotModel.setTimeValue(rs.getInt("TIME_VALUE"));
			timeSlotModel.setTimeUnitName(rs.getString("TIME_UNIT_NAME"));
			timeSlotModel.setBusiness(rs.getBoolean("IS_BUSINESS"));
			return timeSlotModel;
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
