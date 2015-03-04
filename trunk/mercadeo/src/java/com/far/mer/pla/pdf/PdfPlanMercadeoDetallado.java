/**
** @version 1.0
** @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2011 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
** FACTURAPYMES es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia
* Attribution-NonCommercial-NoDerivs 3.0 Unported,
* de acuerdo con la publicada por la CREATIVE COMMONS CORPORATION.
*/

package com.far.mer.pla.pdf;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.ResultSet;
import com.far.lib.Pdf;

import com.far.lib.BaseDatos;

/**
 *
 * @author Jorge
 */
public class PdfPlanMercadeoDetallado extends HttpServlet {
    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    public void init(ServletConfig config) throws ServletException
    {
        this._ip = config.getServletContext().getInitParameter("_IP");
        this._puerto = Integer.parseInt(config.getServletContext().getInitParameter("_PUERTO"));
        this._db = config.getServletContext().getInitParameter("_DB");
        this._usuario = config.getServletContext().getInitParameter("_USUARIO");
        this._clave = config.getServletContext().getInitParameter("_CLAVE");
    }

    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");

        String num_plan = request.getParameter("nro");
        String tipo = request.getParameter("tpy1");
        String id_plan_mercadeo = request.getParameter("idPM1");
        String usuario = request.getParameter("usr1");
        String fecha_ini = request.getParameter("fi1");
        String fecha_fin = request.getParameter("ff1");
        
        String where = "where F.fecha_ingreso between '"+fecha_ini+"' and '"+fecha_fin+"'";
        //String where = "where";
        if(tipo.compareTo("0")!=0){
             where += " and P.id_tipo_plan='"+tipo+"'";
        }
        if(id_plan_mercadeo.compareTo("0")!=0){
             where += (where.compareTo("where")!=0 ? " and" : "") + " P.id_plan_mercadeo="+id_plan_mercadeo;
        }
        if(usuario.compareTo("0")!=0){
             where += (where.compareTo("where")!=0 ? " and" : "") + " P.usuario_creacion='"+usuario+"'";
        }
        /*if(where.compareTo("where")==0){
            where = "";
        }*/
        
        if(num_plan.compareTo("")!=0){
             where = "where sec_tipo_plan="+num_plan;
        }

        BaseDatos objDB = new BaseDatos(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet registros = objDB.consulta("select P.sec_tipo_plan, T.nombre, P.plan_mercadeo,  A.actividad, " + 
        "A.pre_cantidad, A.pre_p_u, A.pre_total, CONVERT(VARCHAR, F.fecha_ingreso, 103), CONVERT(VARCHAR, F.fecha, 103), "+ 
        "F.proveedor, F.detalle, case when A.tipo_pago='i' then 'COMPRAS INTERNAS' else 'PAGO EN FARMACIA' end, F.cantidad, "+ 
        "F.num_documento, F.total " +
        "from (((tbl_tipo_plan as T with (nolock) inner join tbl_plan_mercadeo as P with (nolock) on T.id_tipo_plan=P.id_tipo_plan) " +
        "inner join tbl_estrategia as E with (nolock) on P.id_plan_mercadeo=E.id_plan_mercadeo) " +
        "inner join tbl_actividad as A with (nolock) on E.id_estrategia=A.id_estrategia) " + 
        "inner join tbl_actividad_compra as F with (nolock) on A.id_actividad=F.id_actividad " +
        where +
        " order by P.plan_mercadeo");
        
        String titulo = "FARMAENLACE";
        String ruc = "RUC: 1791984722001";
        String subtitulo = "Plan de Mercadeo Detallado";
        String direccion = "AV. CAPITAN RAFAEL RAMOS E2-210 Y CASTELLI";
        String pie_pag = "Ecuador";
        Pdf objPdf = new Pdf(false, titulo, ruc, subtitulo, direccion, pie_pag);
        
        String [] cabTabla = new String [] {"#",  "No. PLAN", "TIPO", "NOMBRE DEL PLAN", "ACTIVIDAD", "CANTIDAD", "COSTO UNITARIO", 
            "PRES. TOTAL", "FECHA INGRESO", "FECHA DE FACTURA", "PROVEEDOR", "DETALLE", "MEDIO", "CANTIDAD", "No. DOCUMENTO", "VALOR TOTAL"};
        float [] anchoTabla = new float[]{25f, 40f, 90f, 120f, 70f, 50f, 60f, 60f, 70f, 70f, 120f, 120f, 100f, 50f, 90f, 50f};
        int [] alineaciones = new int[]{0,0,0,0,0,1,2,2,0,0,0,0,0,1,0,2};
        
        if(num_plan.compareTo("")!=0){
            int estado = 0;
            try{
                ResultSet rs = objDB.consulta("select estado from tbl_plan_mercadeo with(nolock) where sec_tipo_plan="+num_plan);
                if(rs.next()){
                    estado = rs.getString("estado")!=null ? rs.getInt("estado") : 0;
                    rs.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            if(estado==10){
                objPdf.setMensaje(response, "El plan se encuentra anulado");
            }else{
                objPdf.lista(response, registros, cabTabla, anchoTabla, alineaciones);
            }
        }else{
            objPdf.lista(response, registros, cabTabla, anchoTabla, alineaciones);
        }

        try{
            registros.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        objDB.cerrar();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
