package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models.FormatoAEntity;
import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models.FormatoAEntity_PP;
import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.models.FormatoAEntity_TI;
import co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.repositories.conexion.ConexionBD;

@Repository
public class FormatoARepository {
    
    private final ConexionBD conexionABaseDeDatos;

    public FormatoARepository() {
        conexionABaseDeDatos = new ConexionBD();
    }

    public FormatoAEntity save(FormatoAEntity formato) {
        System.out.println("Registrando formato en la base de datos...");
        int idGenerado = -1;

        try {
            conexionABaseDeDatos.conectar();
            Connection conn = conexionABaseDeDatos.getConnection();
            conn.setAutoCommit(false); // Transacción manual para asegurar todo o nada

            // 1. Inserción principal en tabla formatos
            String sql = "INSERT INTO formatos (tipo_formato, fecha_creacion, titulo, director_trabajo, objetivo_general, " +
                         "nombre_estudiante1, nombre_estudiante2, codigo_estudiante1, codigo_estudiante2, " +
                         "nombre_estudiante, codigo_estudiante, asesor_organizacion, tiene_carta_aceptacion) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                         
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Atributos comunes
            pstmt.setDate(2, new java.sql.Date(formato.getFechaCreacion().getTime()));
            pstmt.setString(3, formato.getTitulo());
            pstmt.setString(4, formato.getDirectorTrabajo());
            pstmt.setString(5, formato.getObjetivoGeneral());

            // Polimorfismo: Llenamos los nulos dependiendo del tipo
            if (formato instanceof FormatoAEntity_TI) {
                FormatoAEntity_TI ti = (FormatoAEntity_TI) formato;
                pstmt.setString(1, "TI");
                
                // Setear campos TI
                pstmt.setString(6, ti.getNombreEstudiante1());
                pstmt.setString(7, ti.getNombreEstudiante2());
                pstmt.setObject(8, ti.getCodigoEstudiante1(), Types.INTEGER); // Permite nulos si Integer
                pstmt.setObject(9, ti.getCodigoEstudiante2(), Types.INTEGER);
                
                // Anular campos PP
                pstmt.setNull(10, Types.VARCHAR);
                pstmt.setNull(11, Types.INTEGER);
                pstmt.setNull(12, Types.VARCHAR);
                pstmt.setNull(13, Types.BOOLEAN);

            } else if (formato instanceof FormatoAEntity_PP) {
                FormatoAEntity_PP pp = (FormatoAEntity_PP) formato;
                pstmt.setString(1, "PP");
                
                // Anular campos TI
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.INTEGER);
                pstmt.setNull(9, Types.INTEGER);

                // Setear campos PP
                pstmt.setString(10, pp.getNombreEstudiante());
                pstmt.setObject(11, pp.getCodigoEstudiante(), Types.INTEGER);
                pstmt.setString(12, pp.getAsesorOrganizacion());
                pstmt.setBoolean(13, pp.isTieneCartaAceptacion());
            }

            pstmt.executeUpdate();

            // Recuperar el ID generado
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
                formato.setId(idGenerado);
            }

            // 2. Guardar los objetivos específicos en su tabla
            if (formato.getObjetivosEspecificos() != null) {
                String sqlObj = "INSERT INTO objetivos (formato_id, objetivo) VALUES (?, ?)";
                PreparedStatement pstmtObj = conn.prepareStatement(sqlObj);
                for (String obj : formato.getObjetivosEspecificos()) {
                    pstmtObj.setInt(1, idGenerado);
                    pstmtObj.setString(2, obj);
                    pstmtObj.executeUpdate();
                }
                pstmtObj.close();
            }

            conn.commit(); // Confirmar transacción
            pstmt.close();
            conexionABaseDeDatos.desconectar();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conexionABaseDeDatos.getConnection() != null) {
                    conexionABaseDeDatos.getConnection().rollback(); // Si falla, reversar
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return idGenerado != -1 ? this.findById(idGenerado).orElse(null) : null;
    }

    public Optional<FormatoAEntity> findById(Integer id) {
        FormatoAEntity formato = null;
        try {
            conexionABaseDeDatos.conectar();
            
            // 1. Consultar el formato
            String sql = "SELECT * FROM formatos WHERE id = ?";
            PreparedStatement pstmt = conexionABaseDeDatos.getConnection().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String tipo = rs.getString("tipo_formato");
                
                // Reconstruir el objeto dependiendo de la BD
                if ("TI".equals(tipo)) {
                    FormatoAEntity_TI ti = new FormatoAEntity_TI();
                    ti.setNombreEstudiante1(rs.getString("nombre_estudiante1"));
                    ti.setNombreEstudiante2(rs.getString("nombre_estudiante2"));
                    ti.setCodigoEstudiante1(rs.getObject("codigo_estudiante1", Integer.class));
                    ti.setCodigoEstudiante2(rs.getObject("codigo_estudiante2", Integer.class));
                    formato = ti;
                } else {
                    FormatoAEntity_PP pp = new FormatoAEntity_PP();
                    pp.setNombreEstudiante(rs.getString("nombre_estudiante"));
                    pp.setCodigoEstudiante(rs.getObject("codigo_estudiante", Integer.class));
                    pp.setAsesorOrganizacion(rs.getString("asesor_organizacion"));
                    pp.setTieneCartaAceptacion(rs.getBoolean("tiene_carta_aceptacion"));
                    formato = pp;
                }

                // Cargar datos comunes
                formato.setId(rs.getInt("id"));
                formato.setFechaCreacion(rs.getDate("fecha_creacion"));
                formato.setTitulo(rs.getString("titulo"));
                formato.setDirectorTrabajo(rs.getString("director_trabajo"));
                formato.setObjetivoGeneral(rs.getString("objetivo_general"));

                // 2. Consultar y cargar los objetivos específicos
                String sqlObj = "SELECT objetivo FROM objetivos WHERE formato_id = ?";
                PreparedStatement pstmtObj = conexionABaseDeDatos.getConnection().prepareStatement(sqlObj);
                pstmtObj.setInt(1, id);
                ResultSet rsObj = pstmtObj.executeQuery();
                
                List<String> objetivos = new ArrayList<>();
                while(rsObj.next()){
                    objetivos.add(rsObj.getString("objetivo"));
                }
                formato.setObjetivosEspecificos(objetivos);
                
                pstmtObj.close();
            }
            pstmt.close();
            conexionABaseDeDatos.desconectar();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(formato);
    }
}