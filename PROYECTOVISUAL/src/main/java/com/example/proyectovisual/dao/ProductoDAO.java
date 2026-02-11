package com.example.proyectovisual.dao;

import com.example.proyectovisual.model.Producto;
import java.sql.SQLException;
import java.util.List;

public interface ProductoDAO {
    void crear(Producto p) throws SQLException;
    List<Producto> listar() throws SQLException;
    void actualizar(Producto p) throws SQLException;
    void eliminar(int id) throws SQLException;
}
