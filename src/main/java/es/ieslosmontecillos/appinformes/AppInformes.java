package es.ieslosmontecillos.appinformes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class AppInformes extends Application
{
    public static Connection conexion = null;
    @javafx.fxml.FXML
    private MenuItem listadoFacturas;
    @javafx.fxml.FXML
    private MenuItem ventasTotales;
    @javafx.fxml.FXML
    private MenuItem facturasPorCliente;
    @javafx.fxml.FXML
    private MenuItem subinformeListadoFacturas;

    @Override
    public void start(Stage primaryStage)
    {
        //establecemos la conexión con la BD
        conectaBD();

        // We charge the main report view
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("appinformes_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 250);
            primaryStage.setTitle("AppInformes");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void stop() throws Exception
    {
        try
        {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/SampleDB;shutdown=true");
        }
        catch (Exception ex)
        {
            System.out.println("No se pudo cerrar la conexion a la BD");
            ex.printStackTrace();
        }
    }

    public void conectaBD()
    {
        //Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost/test";
        String usuario = "SA";
        String clave = "";
        try
        {
            //Class.forName("org.hsqldb.jdbcDriver").newInstance();
            Class.forName("org.hsqldb.jdbcDriver");
            conexion = DriverManager.getConnection(baseDatos,usuario,clave);
        }
        catch (ClassNotFoundException cnfe)
        {
            System.err.println("Fallo al cargar JDBC");
            cnfe.printStackTrace();
            System.exit(1);
        }
        catch (SQLException sqle)
        {
            System.err.println("No se pudo conectar a BD");
            sqle.printStackTrace();
            System.exit(1);
        }
        catch (Exception ex)
        {
            System.err.println("Imposible Conectar");
            ex.printStackTrace();
            System.exit(1);
        }
    }
    public void generaInforme(TextField tintro)
    {
        try
        {
            JasperReport jr =
                    (JasperReport) JRLoader.loadObject(getClass().getResource("PedidosProd.jasper"));
            //Map de parámetros
            Map parametros = new HashMap();
            int nproducto = Integer.valueOf(tintro.getText());
            parametros.put("ParamProducto", nproducto);

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr,
                    parametros, conexion);
            JasperViewer.viewReport(jp);
        }
        catch (JRException ex)
        {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {launch(args);}

    @javafx.fxml.FXML
    public void loadListadoFacturas(ActionEvent actionEvent)
    {
        try
        {
            JasperReport jr =
                    (JasperReport) JRLoader.loadObject(getClass().getResource("Facturas.jasper"));
            /*
            //Map de parámetros
            Map parametros = new HashMap();
            int nproducto = Integer.valueOf(tintro.getText());
            parametros.put("ParamProducto", nproducto);

             */

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr,
                    null, conexion);
            JasperViewer.viewReport(jp);
        }
        catch (JRException ex)
        {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    @javafx.fxml.FXML
    public void loadVentasTotales(ActionEvent actionEvent)
    {
        try
        {
            JasperReport jr =
                    (JasperReport) JRLoader.loadObject(getClass().getResource("Ventas_Totales.jasper"));
            /*
            //Map de parámetros
            Map parametros = new HashMap();
            int nproducto = Integer.valueOf(tintro.getText());
            parametros.put("ParamProducto", nproducto);

             */

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr,
                    null, conexion);
            JasperViewer.viewReport(jp);
        }
        catch (JRException ex)
        {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    @javafx.fxml.FXML
    public void loadFacturasPorCliente(ActionEvent actionEvent)
    {
        TextField clientIdField = new TextField();
        Button generateReportBtn = new Button("Generar Informe");

        // Crear el contenedor VBox
        VBox root = new VBox();
        root.getChildren().addAll(clientIdField, generateReportBtn);

        // Crear una nueva escena
        Scene inputScene = new Scene(root, 300, 150);
        Stage inputStage = new Stage();
        inputStage.setScene(inputScene);
        inputStage.setTitle("Ingrese el ID del Cliente");
        inputStage.show();

        generateReportBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int clientId = Integer.parseInt(clientIdField.getText());
                generateReport(clientId);
                inputStage.close();
            }
        });
    }

    public void generateReport(int clientId)
    {
        try
        {
            JasperReport jr =
                    (JasperReport) JRLoader.loadObject(getClass().getResource("Facturas_por_Cliente.jasper"));

            Map parametros = new HashMap();
            parametros.put("Id_Cliente", clientId);

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr,
                    parametros, conexion);
            JasperViewer.viewReport(jp);
        }
        catch (JRException ex)
        {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    @javafx.fxml.FXML
    public void loadSubinformeListadoFacturas(ActionEvent actionEvent)
    {
        try
        {
            JasperReport jr =
                    (JasperReport) JRLoader.loadObject(getClass().getResource("Subinforme_Listado_Facturas.jasper"));
            /*
            //Map de parámetros
            Map parametros = new HashMap();
            int nproducto = Integer.valueOf(tintro.getText());
            parametros.put("ParamProducto", nproducto);

             */

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr,
                    null, conexion);
            JasperViewer.viewReport(jp);
        }
        catch (JRException ex)
        {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }
}

