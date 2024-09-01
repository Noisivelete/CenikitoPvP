/*
 * Copyright (c) 2024-2025 Francisco Miguel P.G. - Noisivelet
 * Unless stated otherwise, modification, distribution or comertialitation of this software is prohibited by law.
 */
package net.noisivelet.cenikito.cenikitopvp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.mariadb.jdbc.MariaDbDataSource;

/**
 * 
 * @author Francis
 */
public class SQLDatabase {
    public static final String FILENAME="sql.discordbot";
    private SQLDatabase() {}
    
    public static void start() throws SQLException, FileNotFoundException, IOException{
        try{
            MariaDbDataSource dataSource = getDataSource();
            DATASOURCE = dataSource;
            querySelect("SELECT 1");
        } catch (FileNotFoundException ex){
            File file = new File(FILENAME);
            file.createNewFile();
            throw new FileNotFoundException("Archivo 'sql.discordbot' generado. Introduce los datos de la conexión antes de continuar.");
        }
    }
    
    private static MariaDbDataSource DATASOURCE = null;
    
    private static MariaDbDataSource getDataSource() throws FileNotFoundException, IllegalStateException, SQLException {
        MariaDbDataSource ds=new MariaDbDataSource();
        String[] params=new String[3];
        int i=0;
        File file = new File(FILENAME);
        if(!file.exists()) throw new FileNotFoundException(FILENAME);
        try(Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                params[i++]=sc.nextLine();
            }
        }
        
        if(i!=3)
            throw new IllegalStateException("El archivo '"+FILENAME+"' no tiene estructura válida.");
        
        
        ds.setUser(params[0]);
        ds.setPassword(params[1]);
        ds.setUrl("jdbc:"+params[2]);
        return ds;
    }
    
    /**
     * Ejecuta una consulta de tipo SELECT a la base de datos.
     * @param statement Consulta que se realizará. Puede contener wildcards '?'.
     * @param params Lista de parámetros que reemplazarán a los wildcards '?' presentes en la consulta.
     * @return Una lista de mapas, donde cada elemento de la lista es una línea de la base de datos. Cada elemento del HashMap será la columna del dato accedido, y su valor será el valor de dicha columna para la línea.
     * @throws SQLException Si no se puede llevar a cabo la consulta en la base de datos.
     */
    public static ArrayList<HashMap<String, String>> querySelect(String statement, String... params) throws SQLException{
        if(DATASOURCE == null) throw new IllegalStateException("La base de datos no está iniciada.");
        try(
                Connection cn=DATASOURCE.getConnection();
                PreparedStatement stmt=cn.prepareStatement(statement);
                ){
                    for(int i=1;i<=params.length;i++){
                        stmt.setString(i, params[i-1]);
                    }
                    ResultSet rs=stmt.executeQuery();
                    ResultSetMetaData rsmd=rs.getMetaData();
                    ArrayList<HashMap<String,String>> al=new ArrayList<>();
                    HashMap<String, String> hm;
                    while(rs.next()){
                        hm=new HashMap<>();
                        for(int i=1;i<=rsmd.getColumnCount();i++){
                            hm.put(rsmd.getColumnLabel(i), rs.getString(i));
                        }
                        al.add(hm);
                    }
                    return al;
        }
    }
    
    /**
     * Ejecuta una consulta en la base de datos, de tipo INSERT.
     * @param statement La consulta a realizar. Permite wildcards de tipo '?'
     * @param params Parámetros que reemplazarán los wildcards '?' en la consulta. 
     * @return La ID de la nueva fila insertada en la DB.
     * @throws SQLException Si existe un error a la hora de ejecutar la consulta en la base de datos o si no se ha afectado ninguna fila a la hora de ejecutar la consulta.
     */
    public static long queryInsert(String statement, String... params) throws SQLException{
        if(DATASOURCE == null) throw new IllegalStateException("La base de datos no está iniciada.");
        try(
                Connection cn=DATASOURCE.getConnection();
                PreparedStatement stmt=cn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
                )
        {
            for(int i=1;i<=params.length;i++){
                stmt.setString(i, params[i-1]);
            }
            int numRows=stmt.executeUpdate();
            if(numRows==0) throw new SQLException("Error: Ninguna fila afectada.");
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (generatedKeys.getLong(1));
                }
                else {
                    return -1;
                }
            }
            
        }
    }
    
    
    /**
     * Ejecuta una orden en la DB, de tipo DML.
     * @param statement Orden a realizar. Admite wildcards de tipo '?'
     * @param params Parámetros que reemplazarán cada wildcard '?' insertados en la orden SQL.
     * @return El número de filas afectadas por la orden SQL ejecutada.
     * @throws SQLException Si ocurre un error a la hora de ejecutar la orden SQL.
     */
    public static int queryDML(String statement, String... params) throws SQLException{
        if(DATASOURCE == null) throw new IllegalStateException("La base de datos no está iniciada.");
        try (
                Connection cn=DATASOURCE.getConnection();
                PreparedStatement stmt=cn.prepareStatement(statement);
                
            )
        {
            for (int i=1;i<=params.length;i++) {
                stmt.setString(i, params[i-1]);
            }
            return stmt.executeUpdate();         
        }
    }
}
