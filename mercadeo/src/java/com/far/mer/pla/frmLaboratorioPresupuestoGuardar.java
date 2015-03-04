/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.far.mer.pla;

import com.far.mer.pla.clas.Laboratorio;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Jorge
 */
public class frmLaboratorioPresupuestoGuardar extends HttpServlet {

    private String _ip = null;
    private int _puerto = 1433;
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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession sesion = request.getSession(true);
        String usuario = (String)sesion.getAttribute("usuario");

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();

        String res = "msg»Ha ocurrido un error inesperado. Por favor inténtelo más tarde";
        Laboratorio objLaboratorio = new Laboratorio(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {

            String id_laboratorio = request.getParameter("id_lab");
            //String monto = request.getParameter("monto");
            int tope = Integer.parseInt(request.getParameter("tope"));
            
            String id_tipo_planes = "";
            String presupuestos = "";
            String axPresupuesto = "";
            for(int i=0; i<=tope; i++){
                if(request.getParameter("ax_id_tipo_plan"+i)!=null){
                    axPresupuesto = request.getParameter("ax_presupuesto"+i);
                    axPresupuesto = axPresupuesto.compareTo("")!=0 ? axPresupuesto : "0";
                    if(Float.parseFloat(axPresupuesto) > 0){
                        id_tipo_planes += "'" + request.getParameter("ax_id_tipo_plan"+i) + "',";
                        presupuestos += axPresupuesto + ",";
                    }
                }
            }
            if(id_tipo_planes.compareTo("")!=0){
                id_tipo_planes = id_tipo_planes.substring(0, id_tipo_planes.length()-1);
                presupuestos = presupuestos.substring(0, presupuestos.length()-1);
            }

            if(objLaboratorio.setPresupuestos(id_laboratorio, usuario, id_tipo_planes, presupuestos)){
                String html = "";
                float sumatoria = 0;
                try{
                    int i=0;
                    ResultSet rsTipoPlanes = objLaboratorio.getTiposPlanes(id_laboratorio);
                    String id_tipo_plan = "";
                    String nombre = "";
                    float presupuesto = 0;
                    while(rsTipoPlanes.next()){
                        id_tipo_plan = rsTipoPlanes.getString("id_tipo_plan")!=null ? rsTipoPlanes.getString("id_tipo_plan") : "";
                        nombre = rsTipoPlanes.getString("nombre")!=null ? rsTipoPlanes.getString("nombre") : "";
                        presupuesto = rsTipoPlanes.getString("presupuesto")!=null ? rsTipoPlanes.getFloat("presupuesto") : 0;
                        html += "<div class=\"jm_filaImp\" id=\"f"+i+"\" onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaImp'\">"+
                        "<div class=\"columna\" style=\"width:40px\"><input type=\"hidden\" id=\"id_tipo_plan"+i+"\" name=\"id_tipo_plan"+i+"\" value=\""+id_tipo_plan+"\" />"+id_tipo_plan+"</div>"+
                        "<div class=\"columna\" style=\"width:340px\">"+nombre+"</div>"+
                        "<div class=\"columna\" style=\"width:80px;text-align:right\"><input class=\"caja\" type=\"text\" id=\"presupuesto"+i+"\" name=\"presupuesto"+i+"\" value=\""+presupuesto+"\" style=\"width:78px;\" onkeypress=\"_evaluar(event, '0123456789.')\" /></div>"+
                        //"<div class=\"columna\" style=\"width:25px\"><div class=\"borrar\" title=\"Eliminar\" onclick=\"_R('f"+i+"');\">&nbsp;</div></div>"+
                        "</div>";
                        sumatoria += presupuesto;
                        i++;
                    }
                    rsTipoPlanes.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                res = "obj»TiPl^fun»mer_laboratorioBuscar();_('montor').value="+sumatoria+";_R('bloq_vta1');_R('vta1');^msg»Información guardada satisfactoriamente.^frm»" + html;
            }else{
                res = "msg»" + objLaboratorio.getError();
            }
            
        } catch(Exception e) {
            res = "msg»" + e.getMessage();
        }

        try{
            out.print(res);
        }finally {
            objLaboratorio.cerrar();
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
