/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.far.mer.pla;

import com.far.lib.BaseDatos;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jorge
 */
public class getVendedores extends HttpServlet {
    private String easy_ip = null;
    private int easy_puerto = 1433;
    private String easy_db = null;
    private String easy_usuario = null;
    private String easy_clave = null;

    public void init(ServletConfig config) throws ServletException
    {
        this.easy_ip = config.getServletContext().getInitParameter("EASY_IP");
        this.easy_puerto = Integer.parseInt(config.getServletContext().getInitParameter("EASY_PUERTO"));
        this.easy_db = config.getServletContext().getInitParameter("EASY_DB");
        this.easy_usuario = config.getServletContext().getInitParameter("EASY_USUARIO");
        this.easy_clave = config.getServletContext().getInitParameter("EASY_CLAVE");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String txt = request.getParameter("txt").toLowerCase();
        String obj = request.getParameter("obj");
        String onClick = request.getParameter("onC");
        String msg = "";
        String html = "";
        BaseDatos objDB = new BaseDatos(this.easy_ip, this.easy_puerto, this.easy_db, this.easy_usuario, this.easy_clave);
        try{
            ResultSet rs = objDB.consulta("select top(20) codigo_vendedor, nombre_vendedor from tbl_vendedores with (nolock) where lower(nombre_vendedor) like '%"+txt+"%'");
            msg = objDB.getError();
            String codigo_vendedor = "";
            String nombre_vendedor = "";
            while(rs.next()){
                codigo_vendedor = rs.getString("codigo_vendedor")!=null?rs.getString("codigo_vendedor"):"";
                nombre_vendedor = rs.getString("nombre_vendedor")!=null?rs.getString("nombre_vendedor"):"";
                html += "<option value=\"" + codigo_vendedor+"\">" + nombre_vendedor+"</option>";
            }
            rs.close();
        }catch(Exception e){
            msg = e.getMessage();
        }finally{
            objDB.cerrar();
        }

        try {
            out.print("obj»"+obj+"^msg»"+msg+"^frm»");
            out.print("<select id=\"vend\" size=\"5\" onclick=\""+onClick+"\">"+html+"</select>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
