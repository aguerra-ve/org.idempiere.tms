package org.idempiere.tms.mapview.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Set;
import java.util.Collections;

import org.compiere.model.MLocation;
import org.compiere.model.MRole;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.tms.mapview.model.AgingBand;
import org.idempiere.tms.mapview.model.DeliveryRulePolicy;
import org.idempiere.tms.mapview.model.HistoryWindow;
import org.idempiere.tms.mapview.model.OrderCandidate;
import org.idempiere.tms.mapview.model.OrderProductLine;
import org.idempiere.tms.mapview.model.TemporaryProposalReviewer;

/** Native, tenant-scoped and read-only access to completed sales orders. */
public final class OrderCandidateRepository implements TemporaryProposalReviewer.Source {
	private static final ZoneId CARACAS = ZoneId.of("America/Caracas");
	private static final String SQL_SELECT = "SELECT o.C_Order_ID, o.DocumentNo, bp.Name,"
			+ " %s,"
			+ " o.DeliveryRule, completed.CompletedAt,"
			+ " SUM(CASE WHEN ol.QtyOrdered>ol.QtyDelivered"
			+ " THEN ol.QtyOrdered-ol.QtyDelivered ELSE 0 END) AS PendingQty,"
			+ " shipped.LastShippedAt,COALESCE(prepared.PreparedLines,0),mw.Name,"
			+ " %s,"
			+ " CASE WHEN EXISTS(SELECT 1 FROM M_RMA r WHERE r.C_Order_ID=o.C_Order_ID)"
			+ " THEN 'Y' ELSE 'N' END"
			+ " FROM C_Order o"
			+ " JOIN C_BPartner bp ON bp.C_BPartner_ID=o.C_BPartner_ID"
			+ " JOIN C_BPartner_Location bpl ON bpl.C_BPartner_Location_ID=o.C_BPartner_Location_ID"
			+ " JOIN C_DocType dt ON dt.C_DocType_ID=o.C_DocTypeTarget_ID"
			+ " JOIN M_Warehouse mw ON mw.M_Warehouse_ID=o.M_Warehouse_ID"
			+ " JOIN C_OrderLine ol ON ol.C_Order_ID=o.C_Order_ID"
			+ " LEFT JOIN (SELECT cl.Record_ID, MIN(cl.Created) AS CompletedAt"
			+ " FROM AD_ChangeLog cl JOIN AD_Column c ON c.AD_Column_ID=cl.AD_Column_ID"
			+ " JOIN AD_Table t ON t.AD_Table_ID=cl.AD_Table_ID"
			+ " WHERE t.TableName='C_Order' AND c.ColumnName='DocStatus' AND cl.NewValue='CO'"
			+ " GROUP BY cl.Record_ID) completed ON completed.Record_ID=o.C_Order_ID"
			+ " LEFT JOIN (SELECT ol2.C_Order_ID,MAX(done.CompletedAt) AS LastShippedAt"
			+ " FROM M_InOutLine il2 JOIN C_OrderLine ol2 ON ol2.C_OrderLine_ID=il2.C_OrderLine_ID"
			+ " JOIN M_InOut io2 ON io2.M_InOut_ID=il2.M_InOut_ID"
			+ " JOIN (SELECT cl2.Record_ID,MIN(cl2.Created) AS CompletedAt"
			+ " FROM AD_ChangeLog cl2 JOIN AD_Column c2 ON c2.AD_Column_ID=cl2.AD_Column_ID"
			+ " JOIN AD_Table t2 ON t2.AD_Table_ID=c2.AD_Table_ID"
			+ " WHERE t2.TableName='M_InOut' AND c2.ColumnName='DocStatus' AND cl2.NewValue='CO'"
			+ " GROUP BY cl2.Record_ID) done ON done.Record_ID=io2.M_InOut_ID"
			+ " WHERE io2.IsSOTrx='Y' AND io2.MovementType='C-' AND il2.MovementQty>0"
			+ " GROUP BY ol2.C_Order_ID) shipped ON shipped.C_Order_ID=o.C_Order_ID"
			+ " LEFT JOIN (SELECT ol3.C_Order_ID,COUNT(*) AS PreparedLines"
			+ " FROM M_InOutLine il3 JOIN C_OrderLine ol3 ON ol3.C_OrderLine_ID=il3.C_OrderLine_ID"
			+ " JOIN M_InOut io3 ON io3.M_InOut_ID=il3.M_InOut_ID"
			+ " WHERE io3.IsSOTrx='Y' AND io3.MovementType='C-' AND io3.DocStatus IN ('DR','IP')"
			+ " GROUP BY ol3.C_Order_ID) prepared ON prepared.C_Order_ID=o.C_Order_ID"
			+ " WHERE o.IsActive='Y' AND o.IsSOTrx='Y' AND o.DocStatus='CO' AND dt.DocSubTypeSO='SO'"
			+ " AND ol.IsActive='Y' AND ol.M_Product_ID IS NOT NULL";
	private static final String SQL_GROUP = " GROUP BY o.C_Order_ID,o.DocumentNo,bp.Name,"
			+ " %s,bpl.C_Location_ID,o.DeliveryRule,completed.CompletedAt,"
			+ " shipped.LastShippedAt,prepared.PreparedLines,mw.Name"
			+ " HAVING SUM(CASE WHEN ol.QtyOrdered>ol.QtyDelivered"
			+ " THEN ol.QtyOrdered-ol.QtyDelivered ELSE 0 END)>0"
			+ " OR (shipped.LastShippedAt>=? AND shipped.LastShippedAt<=?)"
			+ " ORDER BY completed.CompletedAt,o.DocumentNo";

	private final Properties context;
	private final Clock clock;

	public OrderCandidateRepository(Properties context) {
		this(context, Clock.system(CARACAS));
	}

	OrderCandidateRepository(Properties context, Clock clock) {
		this.context = context;
		this.clock = clock;
	}

	public List<OrderCandidate> findCandidates() throws SQLException {
		List<OrderCandidate> result = new ArrayList<>();
		boolean specialLocation = hasColumn("C_Order", "C_Location_ID");
		List<Integer> warehouses = warehousesForRole(hasColumn("M_Warehouse", "WarehouseManager"));
		if (warehouses.isEmpty()) return List.of();
		String ids = warehouses.stream().map(String::valueOf)
				.collect(java.util.stream.Collectors.joining(","));
		String location = specialLocation
				? "COALESCE(NULLIF(o.C_Location_ID,0),bpl.C_Location_ID)" : "bpl.C_Location_ID";
		String special = specialLocation
				? "CASE WHEN o.C_Location_ID>0 AND o.C_Location_ID<>bpl.C_Location_ID THEN 'Y' ELSE 'N' END"
				: "'N'";
		String groupLocation = specialLocation ? "o.C_Location_ID" : "bpl.C_Location_ID";
		String accessControlledSql = MRole.getDefault(context, false)
				.addAccessSQL(SQL_SELECT.formatted(location, special), "o", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO)
				+ " AND o.M_Warehouse_ID IN (" + ids + ")" + SQL_GROUP.formatted(groupLocation);
		Instant now = clock.instant();
		Instant firstDay = HistoryWindow.startInclusive(clock);
		try (PreparedStatement statement = DB.prepareStatement(accessControlledSql, null)) {
			Calendar caracas = Calendar.getInstance(TimeZone.getTimeZone(CARACAS));
			statement.setTimestamp(1, Timestamp.from(firstDay), caracas);
			statement.setTimestamp(2, Timestamp.from(now), caracas);
			try (ResultSet rows = statement.executeQuery()) {
				while (rows.next()) {
					result.add(map(rows, now));
				}
			}
		}
		return List.copyOf(result);
	}

	@Override
	public List<OrderProductLine> findPendingProductLines(
			Set<Integer> selectedOrderIds) throws SQLException {
		if (selectedOrderIds.isEmpty()) return List.of();
		List<Integer> ids = selectedOrderIds.stream().filter(id -> id != null && id > 0).sorted().toList();
		if (ids.size() != selectedOrderIds.size()) throw new SQLException("Invalid order selection");
		List<Integer> warehouses = warehousesForRole(hasColumn("M_Warehouse", "WarehouseManager"));
		if (warehouses.isEmpty()) return List.of();
		String allowed = warehouses.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
		String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
		String sql = "SELECT o.AD_Client_ID,o.C_Order_ID,ol.C_OrderLine_ID,p.M_Product_ID,"
				+ " COALESCE(NULLIF(ol.M_Warehouse_ID,0),o.M_Warehouse_ID),p.Classification,"
				+ " ol.QtyOrdered-ol.QtyDelivered"
				+ " FROM C_Order o JOIN C_OrderLine ol ON ol.C_Order_ID=o.C_Order_ID AND ol.AD_Client_ID=o.AD_Client_ID"
				+ " JOIN M_Product p ON p.M_Product_ID=ol.M_Product_ID AND p.AD_Client_ID=o.AD_Client_ID"
				+ " JOIN C_DocType dt ON dt.C_DocType_ID=o.C_DocTypeTarget_ID"
				+ " WHERE o.AD_Client_ID=? AND o.IsActive='Y' AND o.IsSOTrx='Y' AND o.DocStatus='CO'"
				+ " AND dt.DocSubTypeSO='SO' AND ol.IsActive='Y' AND ol.QtyOrdered>ol.QtyDelivered"
				+ " AND o.C_Order_ID IN (" + placeholders + ")"
				+ " AND o.M_Warehouse_ID IN (" + allowed + ")"
				+ " AND COALESCE(NULLIF(ol.M_Warehouse_ID,0),o.M_Warehouse_ID) IN (" + allowed + ")";
		sql = MRole.getDefault(context, false).addAccessSQL(sql, "o", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		List<OrderProductLine> lines = new ArrayList<>();
		try (PreparedStatement statement = DB.prepareStatement(sql, null)) {
			statement.setInt(1, Env.getAD_Client_ID(context));
			for (int index = 0; index < ids.size(); index++) statement.setInt(index + 2, ids.get(index));
			try (ResultSet rows = statement.executeQuery()) {
				while (rows.next()) lines.add(new OrderProductLine(
						rows.getLong(1), rows.getInt(2), rows.getInt(3), rows.getInt(4), rows.getInt(5),
						rows.getString(6), rows.getBigDecimal(7)));
			}
		}
		return List.copyOf(lines);
	}

	private List<Integer> warehousesForRole(boolean hasManagers) throws SQLException {
		List<Integer> allowed = new ArrayList<>();
		String roleId = Integer.toString(Env.getAD_Role_ID(context));
		String select = "SELECT mw.M_Warehouse_ID" + (hasManagers ? ",mw.WarehouseManager" : "")
				+ " FROM M_Warehouse mw WHERE mw.IsActive='Y' AND mw.AD_Client_ID=?";
		String sql = MRole.getDefault(context, false)
				.addAccessSQL(select, "mw", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		try (PreparedStatement statement = DB.prepareStatement(
				sql, null)) {
			statement.setInt(1, Env.getAD_Client_ID(context));
			try (ResultSet rows = statement.executeQuery()) {
				while (rows.next()) {
					if (!hasManagers) {
						allowed.add(rows.getInt(1));
						continue;
					}
					String managers = rows.getString(2);
					if (managers == null) continue;
					for (String token : managers.split(",")) {
						if (roleId.equals(token.trim())) {
							allowed.add(rows.getInt(1));
							break;
						}
					}
				}
			}
		}
		return allowed;
	}

	private boolean hasColumn(String table, String column) throws SQLException {
		String sql = "SELECT 1 FROM information_schema.columns"
				+ " WHERE table_schema=ANY(current_schemas(false))"
				+ " AND table_name=? AND column_name=?";
		try (PreparedStatement statement = DB.prepareStatement(sql, null)) {
			statement.setString(1, table.toLowerCase(java.util.Locale.ROOT));
			statement.setString(2, column.toLowerCase(java.util.Locale.ROOT));
			try (ResultSet rows = statement.executeQuery()) {
				return rows.next();
			}
		}
	}

	private OrderCandidate map(ResultSet row, Instant now) throws SQLException {
		int locationId = row.getInt(4);
		MLocation location = new MLocation(context, locationId, null);
		Calendar caracas = Calendar.getInstance(TimeZone.getTimeZone(CARACAS));
		Instant completedAt = timestamp(row.getTimestamp(6, caracas));
		Instant dispatchedAt = timestamp(row.getTimestamp(8, caracas));
		BigDecimal pending = decimal(row.getBigDecimal(7));
		DeliveryRulePolicy.Result rule = DeliveryRulePolicy.explain(row.getString(5));
		return new OrderCandidate(row.getInt(1), row.getString(2), row.getString(3), locationId,
				location.getAddress1(), value(location.getRegionName(true)), municipality(location),
				parish(location), completedAt,
				AgingBand.at(completedAt, now), row.getString(5), rule.label(),
				rule.explanation(), pending, coordinate(location, "Longitude"),
				coordinate(location, "Latitude"), dispatchedAt, pending.signum() == 0,
				row.getInt(9) > 0, row.getString(10), "Y".equals(row.getString(11)),
				"Y".equals(row.getString(12)));
	}

	private Double coordinate(MLocation location, String columnName) {
		if (location.get_ColumnIndex(columnName) < 0) {
			return null;
		}
		Object raw = location.get_Value(columnName);
		if (raw == null || raw.toString().isBlank()) {
			return null;
		}
		try {
			return Double.valueOf(raw.toString().trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String municipality(MLocation location) {
		String municipality = optionalReference(location, "C_Municipality_ID", "C_Municipality");
		return municipality == null ? value(location.getCity()) : municipality;
	}

	private String parish(MLocation location) {
		String parish = optionalReference(location, "C_Parish_ID", "C_Parish");
		if (parish != null) return parish;
		String address2 = value(location.getAddress2());
		String prefix = "Parroquia:";
		return address2 != null && address2.regionMatches(true, 0, prefix, 0, prefix.length())
				? value(address2.substring(prefix.length())) : null;
	}

	private String optionalReference(MLocation location, String columnName, String tableName) {
		if (location.get_ColumnIndex(columnName) < 0) {
			return null;
		}
		int id = location.get_ValueAsInt(columnName);
		return id <= 0 ? null : value(DB.getSQLValueString(null,
				"SELECT Name FROM " + tableName + " WHERE " + columnName + "=?", id));
	}

	private static Instant timestamp(Timestamp value) {
		return value == null ? null : value.toInstant();
	}

	private static BigDecimal decimal(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private static String value(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
