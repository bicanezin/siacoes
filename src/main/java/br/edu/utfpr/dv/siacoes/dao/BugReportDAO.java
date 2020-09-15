package br.edu.utfpr.dv.siacoes.dao;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


public class BugReportDAO extends TemplateMethod<BugReport>{
	public String FindByIdQuery() {
		return ("SELECT bugreport.*, \"user\".name " +
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?");
	}

	public String ListAllQuery(boolean onlyActive) {
		return ("SELECT bugreport.*, \"user\".name " +
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"ORDER BY status, reportdate");
	}

	@Override
	protected int insert(int id, BugReport bug) throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS
				)
		) {
			stmt.setInt(1, bug.getUser().getIdUser());
			stmt.setInt(2, bug.getModule().getValue());
			stmt.setString(3, bug.getTitle());
			stmt.setString(4, bug.getDescription());
			stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			stmt.setInt(6, bug.getType().getValue());
			stmt.setInt(7, bug.getStatus().getValue());
			if (bug.getStatus() == BugStatus.REPORTED) {
				stmt.setNull(8, Types.DATE);
			} else {
				stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}
			stmt.setString(9, bug.getStatusDescription());
			stmt.execute();

			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					bug.setIdBugReport(rs.getInt(1));
				}
				return bug.getIdBugReport();
			}
		}
	}

	@Override
	protected int update(int id, BugReport bug) throws SQLException {
		try (
				Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?"
				)
		) {
			stmt.setInt(1, bug.getUser().getIdUser());
			stmt.setInt(2, bug.getModule().getValue());
			stmt.setString(3, bug.getTitle());
			stmt.setString(4, bug.getDescription());
			stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			stmt.setInt(6, bug.getType().getValue());
			stmt.setInt(7, bug.getStatus().getValue());

			if (bug.getStatus() == BugStatus.REPORTED) {
				stmt.setNull(8, Types.DATE);
			} else {
				stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}

			stmt.setString(9, bug.getStatusDescription());
			stmt.setInt(10, bug.getIdBugReport());
			stmt.execute();

			return bug.getIdBugReport();
		}
	}

	@Override
	public int save(int id, BugReport bug) throws SQLException {
		boolean insert = (bug.getIdBugReport() == 0);

		if (insert) {
			return insert(id, bug);
		} else {
			return update(id, bug);
		}
	}

	@Override
	BugReport loadObject(ResultSet rs) throws SQLException {
		BugReport bug = new BugReport();

		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));

		return bug;
	}
}