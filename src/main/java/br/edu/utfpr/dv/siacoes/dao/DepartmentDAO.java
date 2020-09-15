package br.edu.utfpr.dv.siacoes.dao;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO extends TemplateMethod<Department> {
	public String FindByIdQuery() {
		return ("SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?");
	}

	@Override
	public String ListAllQuery(boolean onlyActive) {
		return ("SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				(onlyActive ? " WHERE department.active=1" : "") +
				" ORDER BY department.name");
	}

	public List<Department> listByCampus(int idCampus, boolean onlyActive)
			throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT department.*, campus.name AS campusName " +
								"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
								"WHERE department.idCampus=" +
								(idCampus) +
								(onlyActive ? " AND department.active=1" : "") +
								" ORDER BY department.name"
				)
		) {
			List<Department> list = new ArrayList<>();

			while (rs.next()) {
				list.add(this.loadObject(rs));
			}

			return list;
		}
	}

	@Override
	protected int insert(int idUser, Department department) throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS
				)
		) {
			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if (department.getLogo() == null) {
				stmt.setNull(3, Types.BINARY);
			} else {
				stmt.setBytes(3, department.getLogo());
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());

			stmt.execute();
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					department.setIdDepartment(rs.getInt(1));
				}
				new UpdateEvent(conn).registerInsert(idUser, department);
				return department.getIdDepartment();
			}
		}
	}

	@Override
	protected int update(int idUser, Department department) throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?"
				)
		) {
			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if (department.getLogo() == null) {
				stmt.setNull(3, Types.BINARY);
			} else {
				stmt.setBytes(3, department.getLogo());
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());
			stmt.setInt(8, department.getIdDepartment());

			stmt.execute();
			new UpdateEvent(conn).registerInsert(idUser, department);
			return department.getIdDepartment();
		}
	}

	@Override
	public int save(int idUser, Department department) throws SQLException {
		boolean insert = (department.getIdDepartment() == 0);

		if (insert) {
			return insert(idUser, department);
		} else {
			return update(idUser, department);
		}
	}

	@Override
	Department loadObject(ResultSet rs) throws SQLException {
		Department department = new Department();

		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));

		return department;
	}
}