package co.edu.unicauca.api_rest_mcs_registro.capaAccesoADatos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
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

            // 1. Inserción en la tabla padre (formatos)
            String sqlPadre = "INSERT INTO formatos (tipo_formato, fecha_creacion, titulo, director_trabajo, objetivo_general) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtPadre = conn.prepareStatement(sqlPadre, Statement.RETURN_GENERATED_KEYS);
            
            String tipo = (formato instanceof FormatoAEntity_TI) ? "TI" : "PP";
            pstmtPadre.setString(1, tipo);
            pstmtPadre.setDate(2, new java.sql.Date(formato.getFechaCreacion().getTime()));
            pstmtPadre.setString(3, formato.getTitulo());
            pstmtPadre.setString(4, formato.getDirectorTrabajo());
            pstmtPadre.setString(5, formato.getObjetivoGeneral());
            
            pstmtPadre.executeUpdate();

            // Recuperar el ID generado en la tabla padre
            ResultSet rs = pstmtPadre.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
                formato.setId(idGenerado);
            }
            pstmtPadre.close();

            // 2. Inserción en la tabla hija correspondiente
            if (formato instanceof FormatoAEntity_TI) {
                FormatoAEntity_TI ti = (FormatoAEntity_TI) formato;
                String sqlHijo = "INSERT INTO formatos_ti (formato_id, nombre_estudiante1, nombre_estudiante2, codigo_estudiante1, codigo_estudiante2) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmtHijo = conn.prepareStatement(sqlHijo);
                pstmtHijo.setInt(1, idGenerado);
                pstmtHijo.setString(2, ti.getNombreEstudiante1());
                pstmtHijo.setString(3, ti.getNombreEstudiante2());
                pstmtHijo.setObject(4, ti.getCodigoEstudiante1(), Types.INTEGER); 
                pstmtHijo.setObject(5, ti.getCodigoEstudiante2(), Types.INTEGER);
                pstmtHijo.executeUpdate();
                pstmtHijo.close();

            } else if (formato instanceof FormatoAEntity_PP) {
                FormatoAEntity_PP pp = (FormatoAEntity_PP) formato;
                String sqlHijo = "INSERT INTO formatos_pp (formato_id, nombre_estudiante, codigo_estudiante, asesor_organizacion, tiene_carta_aceptacion) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmtHijo = conn.prepareStatement(sqlHijo);
                pstmtHijo.setInt(1, idGenerado);
                pstmtHijo.setString(2, pp.getNombreEstudiante());
                pstmtHijo.setObject(3, pp.getCodigoEstudiante(), Types.INTEGER);
                pstmtHijo.setString(4, pp.getAsesorOrganizacion());
                pstmtHijo.setBoolean(5, pp.isTieneCartaAceptacion());
                pstmtHijo.executeUpdate();
                pstmtHijo.close();
            }

            // 3. Guardar los objetivos específicos en su tabla
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
            conexionABaseDeDatos.desconectar();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Si algo falla en cualquiera de los inserts, se reversa todo
                if (conexionABaseDeDatos.getConnection() != null) {
                    conexionABaseDeDatos.getConnection().rollback();
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
            
            // 1. Consultar con LEFT JOIN para traer los datos del padre y ambas posibles hijas
            String sql = "SELECT f.*, " +
                         "ti.nombre_estudiante1, ti.nombre_estudiante2, ti.codigo_estudiante1, ti.codigo_estudiante2, " +
                         "pp.nombre_estudiante, pp.codigo_estudiante, pp.asesor_organizacion, pp.tiene_carta_aceptacion " +
                         "FROM formatos f " +
                         "LEFT JOIN formatos_ti ti ON f.id = ti.formato_id " +
                         "LEFT JOIN formatos_pp pp ON f.id = pp.formato_id " +
                         "WHERE f.id = ?";
                         
            PreparedStatement pstmt = conexionABaseDeDatos.getConnection().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String tipo = rs.getString("tipo_formato");
                
                // Reconstruir el objeto hijo correcto dependiendo del discriminador
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

                // Cargar datos comunes del padre
                formato.setId(rs.getInt("id"));
                formato.setFechaCreacion(rs.getDate("fecha_creacion"));
                formato.setTitulo(rs.getString("titulo"));
                formato.setDirectorTrabajo(rs.getString("director_trabajo"));
                formato.setObjetivoGeneral(rs.getString("objetivo_general"));
                formato.setEstadoActual(rs.getString("estado_actual"));

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

    public List<FormatoAEntity> findAll() {
        List<FormatoAEntity> formatos = new ArrayList<>();
        try {
            conexionABaseDeDatos.conectar();
            Connection conn = conexionABaseDeDatos.getConnection();

            String sql = "SELECT f.*, " +
                    "ti.nombre_estudiante1, ti.nombre_estudiante2, ti.codigo_estudiante1, ti.codigo_estudiante2, " +
                    "pp.nombre_estudiante, pp.codigo_estudiante, pp.asesor_organizacion, pp.tiene_carta_aceptacion " +
                    "FROM formatos f " +
                    "LEFT JOIN formatos_ti ti ON f.id = ti.formato_id " +
                    "LEFT JOIN formatos_pp pp ON f.id = pp.formato_id";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                FormatoAEntity formato;
                String tipoFormato = rs.getString("tipo_formato");

                if ("TI".equals(tipoFormato)) {
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

                formato.setId(rs.getInt("id"));
                formato.setFechaCreacion(rs.getDate("fecha_creacion"));
                formato.setTitulo(rs.getString("titulo"));
                formato.setDirectorTrabajo(rs.getString("director_trabajo"));
                formato.setObjetivoGeneral(rs.getString("objetivo_general"));
                formato.setEstadoActual(rs.getString("estado_actual"));

                String sqlObj = "SELECT objetivo FROM objetivos WHERE formato_id = ?";
                PreparedStatement pstmtObj = conn.prepareStatement(sqlObj);
                pstmtObj.setInt(1, formato.getId());
                ResultSet rsObj = pstmtObj.executeQuery();

                List<String> objetivos = new ArrayList<>();
                while (rsObj.next()) {
                    objetivos.add(rsObj.getString("objetivo"));
                }
                formato.setObjetivosEspecificos(objetivos);
                pstmtObj.close();

                formatos.add(formato);
            }
            pstmt.close();
            conexionABaseDeDatos.desconectar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formatos;
    }

    public boolean actualizarEstado(Integer idFormato, String nuevoEstado) {
        boolean actualizado = false;
        try {
            conexionABaseDeDatos.conectar();
            Connection conn = conexionABaseDeDatos.getConnection();
            String sql = "UPDATE formatos SET estado_actual = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idFormato);
            actualizado = pstmt.executeUpdate() > 0;
            pstmt.close();
            conexionABaseDeDatos.desconectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actualizado;
    }

    public List<FormatoAEntity> findByRangoFechas(Date fechaInicio, Date fechaFin) {
        List<FormatoAEntity> formatos = new ArrayList<>();
        try {
            conexionABaseDeDatos.conectar();
            Connection conn = conexionABaseDeDatos.getConnection();

            String sql = "SELECT f.*, " +
                    "ti.nombre_estudiante1, ti.nombre_estudiante2, ti.codigo_estudiante1, ti.codigo_estudiante2, " +
                    "pp.nombre_estudiante, pp.codigo_estudiante, pp.asesor_organizacion, pp.tiene_carta_aceptacion " +
                    "FROM formatos f " +
                    "LEFT JOIN formatos_ti ti ON f.id = ti.formato_id " +
                    "LEFT JOIN formatos_pp pp ON f.id = pp.formato_id " +
                    "WHERE f.fecha_creacion BETWEEN ? AND ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            pstmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                FormatoAEntity formato;
                String tipoFormato = rs.getString("tipo_formato");

                if ("TI".equals(tipoFormato)) {
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

                formato.setId(rs.getInt("id"));
                formato.setFechaCreacion(rs.getDate("fecha_creacion"));
                formato.setTitulo(rs.getString("titulo"));
                formato.setDirectorTrabajo(rs.getString("director_trabajo"));
                formato.setObjetivoGeneral(rs.getString("objetivo_general"));
                formato.setEstadoActual(rs.getString("estado_actual"));

                String sqlObj = "SELECT objetivo FROM objetivos WHERE formato_id = ?";
                PreparedStatement pstmtObj = conn.prepareStatement(sqlObj);
                pstmtObj.setInt(1, formato.getId());
                ResultSet rsObj = pstmtObj.executeQuery();

                List<String> objetivos = new ArrayList<>();
                while (rsObj.next()) {
                    objetivos.add(rsObj.getString("objetivo"));
                }
                formato.setObjetivosEspecificos(objetivos);
                pstmtObj.close();

                formatos.add(formato);
            }
            pstmt.close();
            conexionABaseDeDatos.desconectar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formatos;
    }
}