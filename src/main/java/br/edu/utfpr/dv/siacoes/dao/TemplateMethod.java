package br.edu.utfpr.dv.siacoes.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class TemplateMethod<T> {
    abstract public String FindByIdQuery();
    abstract public String ListAllQuery(boolean onlyActive);
    public T findById(int id) throws SQLException{
        try (
                Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(FindByIdQuery())
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return
                        rs.next() ? this.loadObject(rs): null;
            }
        }
    }

   public List<T> listAll(boolean onlyActive) throws SQLException {
       try (
               Connection conn = ConnectionDAO.getInstance().getConnection();
               Statement stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery(ListAllQuery(onlyActive))
       ) {
           List<T> list = new ArrayList<>();

           while (rs.next()) {
               list.add(this.loadObject(rs));
           }

           return list;
       }
   }

    //public abstract List<Department> listAll(boolean onlyActive) throws SQLException;

    protected abstract int insert(int idUser, T unit) throws SQLException;
    protected abstract int update(int idUser, T unit) throws SQLException;
    public abstract int save(int idUser, T unit) throws SQLException;
    abstract T loadObject(ResultSet rs) throws SQLException;
}