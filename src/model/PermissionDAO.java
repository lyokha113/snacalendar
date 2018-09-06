/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.PermissionDTO;
import utils.DbUtil;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuu
 */
public class PermissionDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public PermissionDAO() {
    }

    public List<PermissionDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblPermission";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<PermissionDTO> result = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("PermissionID");
                String name = rs.getString("PermissionName");

                PermissionDTO dto = new PermissionDTO(id, name);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
