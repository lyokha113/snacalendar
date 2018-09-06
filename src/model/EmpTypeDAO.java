/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.EmpTypeDTO;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DbUtil;

import javax.naming.NamingException;

/**
 *
 * @author yuu
 */
public class EmpTypeDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public EmpTypeDAO() {
    }
    
    
        public List<EmpTypeDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblEmpType";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<EmpTypeDTO> result = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("TypeID");
                String name = rs.getString("TypeName");

                EmpTypeDTO dto = new EmpTypeDTO(id, name);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

}
