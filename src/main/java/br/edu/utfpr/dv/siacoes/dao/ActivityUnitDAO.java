package br.edu.utfpr.dv.siacoes.dao;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ActivityUnitDAO extends TemplateMethod<ActivityUnit> {
	public String FindByIdQuery() {
		return ("SELECT * FROM activityunit WHERE idActivityUnit=?");
	}

	public String ListAllQuery(boolean onlyActive) {
		return ("SELECT * FROM activityunit ORDER BY description");
	}

	@Override
	protected int insert(int idUser, ActivityUnit unit) throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS
				)
		) {
			stmt.setString(1, unit.getDescription());
			stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			stmt.setString(3, unit.getAmountDescription());
			stmt.execute();

			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					unit.setIdActivityUnit(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, unit);

				return unit.getIdActivityUnit();
			}
		}
	}

	@Override
	protected int update(int idUser, ActivityUnit unit) throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?"
				)
		) {
			stmt.setString(1, unit.getDescription());
			stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			stmt.setString(3, unit.getAmountDescription());
			stmt.setInt(4, unit.getIdActivityUnit());
			stmt.execute();

			new UpdateEvent(conn).registerInsert(idUser, unit);

			return unit.getIdActivityUnit();
		}
	}

	@Override
	public int save(int idUser, ActivityUnit unit) throws SQLException {
		boolean insert = (unit.getIdActivityUnit() == 0);

		if (insert) {
			return insert(idUser, unit);
		} else {
			return update(idUser, unit);
		}
	}

	@Override
	ActivityUnit loadObject(ResultSet rs) throws SQLException {
		ActivityUnit unit = new ActivityUnit();

		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));

		return unit;
	}
}