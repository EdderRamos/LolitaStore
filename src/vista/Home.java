package vista;

import controlador.ClienteController;
import controlador.CrearVentaController;
import controlador.HistorialVentasController;
import controlador.ProductoController;
import controlador.ProveedorController;
import EstructuraDeDatos.ABB.ArbolProducto;
import EstructuraDeDatos.ABB.OrdenamientoABB;
import EstructuraDeDatos.Arrays.CarritoDeCompras;
import EstructuraDeDatos.Arrays.CarritoItem;
import EstructuraDeDatos.ListaEnlazada.ListaClientes;
import EstructuraDeDatos.ListaEnlazada.ListaProveedores;
import EstructuraDeDatos.Pilas.ListaPilaVentas;
import modelo.Cliente;
import util.Eventos;
import modelo.Producto;
import modelo.Proveedor;
import modelo.Sesion;
import util.BoletaPDF;
import util.Colors;
import java.awt.Desktop;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Edder
 */
public final class Home extends javax.swing.JFrame {

    Eventos event = new Eventos();

    private CrearVentaController crearVentaController;
    private ClienteController clienteController;
    private ProductoController productoController;
    private ProveedorController proveedorController;
    private HistorialVentasController historialVentasController;

    private JButton[] opcionesDeClientes;
    private JButton[] opcionesDeProductos;
    private JButton[] opcionesDeProveedores;

    // matriz de privilegios roles x privilegios 
    // solo 2 roles: 0 = administrador, 1 = asistente
    // privilegios: 0 = Buscar, 1 = Agregar, 2 = Actualizar, 3 = Eliminar
    private boolean[][] privilegiosRoles;
    private Sesion sesion;

    public Home() {
        Sesion demo = new Sesion();
        demo.setRol("Asistente");
        demo.setId(1);
        demo.setContra("1234");
        demo.setNombre("prueba");
        demo.setCorreo("prueba@gmail.com");
        init(demo);
    }

    public Home(Sesion login) {
        init(login);
    }

    public void init(Sesion login) {
        this.sesion = login;
        this.setLocationRelativeTo(null);

        initComponents();

        //incializamos solo el controller del item seleccionado por default
        crearVentaController = new CrearVentaController(this);

        //acciones clientes
        opcionesDeClientes = new JButton[]{btnBuscarClienteEnClientes, btnAgregarCliente, btnActualizarCliente, btnEliminarCliente};
        //acciones de producto 
        opcionesDeProductos = new JButton[]{btnBuscarProducto, btnAgregarProducto, btnActualizarProducto, btnEliminarProducto};
        //accioens para proveedores 
        opcionesDeProveedores = new JButton[]{btnBuscarProveedorEnProveedores, btnAgregarProveedor, btnActualizarProveedor, btnEliminarProveedor};

        cargarOpcionesDeOrdenamientoABB();

        AutoCompleteDecorator.decorate(cbxProveedorProducto);
        //configure user options and user data
        configurarPrivilegiosRoles(); //configuramos lo que puede y no puede cada roll
        configureUserOption();

    }

    public void configurarPrivilegiosRoles() {
        //0 = Administrador, 1 = Asistente
        //0 = Buscar, 1 = Agregar, 2 = Actualizar, 3 = Eliminar
        privilegiosRoles = new boolean[2][4];

        // privilegios para el admin
        privilegiosRoles[0][0] = true;
        privilegiosRoles[0][1] = true;
        privilegiosRoles[0][2] = true;
        privilegiosRoles[0][3] = true;

        // privilegios para el asistente
        privilegiosRoles[1][0] = true; 
        privilegiosRoles[1][1] = false;
        privilegiosRoles[1][2] = false; 
        privilegiosRoles[1][3] = false; 
    }

    public void configureUserOption() {
        txtUserName.setText(sesion.getNombre());
        if (sesion.getRol().equals("Asistente")) {
            ajustarPrivilegios(1); // Asistente: solo tiene el privilegio de "Buscar"
        } else if (sesion.getRol().equals("Administrador")) {
            ajustarPrivilegios(0); // Administrador: todos los privilegios
        }
    }

    // Deshabilita los botones según los privilegios del rol usando la matriz 2D
    public void ajustarPrivilegios(int rolIndex) {
        // Clientes
        btnBuscarClienteEnClientes.setEnabled(privilegiosRoles[rolIndex][0]);
        btnAgregarCliente.setEnabled(privilegiosRoles[rolIndex][1]);
        btnActualizarCliente.setEnabled(privilegiosRoles[rolIndex][2]);
        btnEliminarCliente.setEnabled(privilegiosRoles[rolIndex][3]);

        // Productos
        btnBuscarProducto.setEnabled(privilegiosRoles[rolIndex][0]);
        btnAgregarProducto.setEnabled(privilegiosRoles[rolIndex][1]);
        btnActualizarProducto.setEnabled(privilegiosRoles[rolIndex][2]);
        btnEliminarProducto.setEnabled(privilegiosRoles[rolIndex][3]);

        // Proveedores
        btnBuscarProveedorEnProveedores.setEnabled(privilegiosRoles[rolIndex][0]);
        btnAgregarProveedor.setEnabled(privilegiosRoles[rolIndex][1]);
        btnActualizarProveedor.setEnabled(privilegiosRoles[rolIndex][2]);
        btnEliminarProveedor.setEnabled(privilegiosRoles[rolIndex][3]);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "", JOptionPane.INFORMATION_MESSAGE);
    }

    public int mostrarPregunta(String mensage) {
        //si retorna 1 es que esta de acuerdo con la accion
        return JOptionPane.showConfirmDialog(null, mensage);
    }

    private void seleccionarBotonPorIndice(int index, JButton[] listaDeBotones) {
        for (int i = 0; i < listaDeBotones.length; i++) {
            if (i == index) {
                listaDeBotones[i].requestFocusInWindow();
                listaDeBotones[i].setBackground(Colors.colorSeleccionado);
            } else {
                listaDeBotones[i].setBackground(Colors.colorNoSeleccionado);
            }
        }
    }

    private JButton obtenerBotonSeleccionado(JButton[] listaDeBotones) {
        for (JButton boton : listaDeBotones) {
            if (boton.getBackground() == Colors.colorSeleccionado) {
                return boton;
            }
        }
        return null;
    }

    public void actualizarVentasRealizadasSuperior(int enElMes, int enLaSemana, int hoy) {
        txtVentaDelMes.setText("" + enElMes);
        txtVentaEnLaSemana.setText("" + enLaSemana);
        txtVentasDeHoy.setText("" + hoy);
    }

    // FUNCIONES DEL MODULO DE CREAR VENTA
    public void actualizarCarritodeVenta(CarritoDeCompras carrito) {
        DefaultTableModel modeloCarrito = (DefaultTableModel) tbCarritoDeVenta.getModel();
        modeloCarrito.setRowCount(0);

        for (CarritoItem item : carrito.getItems()) {
            Producto p = item.getProducto();
            Object[] fila = new Object[5];
            fila[0] = p.getCodigo();
            fila[1] = p.getNombre();
            fila[2] = p.getPrecio();
            fila[3] = item.getCantidad();
            fila[4] = item.calcularTotal();
            modeloCarrito.addRow(fila);
        }

        txtCarritoTotal.setText("S/. " + carrito.calcularTotal());

    }

    public void actualizarProductoSeleccionadoEnVenta(Producto producto) {
        if (producto == null) {
            txtCodigoDeProductoEnVenta.setText("");
            txtPrecioProductoEnVenta.setText("...");
            txtDescripcionVenta.setText("...");
            txtStockVenta.setText("...");
            txtCantidadVenta.setText("");
        } else {
            txtPrecioProductoEnVenta.setText(producto.getPrecio() + "");
            txtDescripcionVenta.setText(producto.getNombre());
            txtStockVenta.setText(producto.getStock() + "");
        }

    }

    public void actualizarClienteSeleccionadoEnVenta(Cliente cliente) {
        if (cliente == null) {
            txtDniORucClienteEnVenta.setText("");
            txtNombreClienteVenta.setText("...");
            txtTelefonoClienteVenta.setText("...");
        } else {
            txtNombreClienteVenta.setText(cliente.getNombre());
            txtTelefonoClienteVenta.setText(cliente.getTelefono() + "");
        }
    }

    public void reiniciarRealizarCompra() {
        DefaultTableModel model = (DefaultTableModel) tbCarritoDeVenta.getModel();
        model.setRowCount(0);
        txtDniORucClienteEnVenta.setText("");
        txtNombreClienteVenta.setText("...");
        txtTelefonoClienteVenta.setText("...");
        txtCarritoTotal.setText("...");
    }

    // FUNCIONES DEL MODULO DE CLIENTES 
    public void actualizarTablaDeClientes(ListaClientes lesClientes) {
        DefaultTableModel modelo = (DefaultTableModel) tbClientes.getModel();
        modelo.setRowCount(0);
        lesClientes.mostrarEnTabla(modelo);
    }

    public void actualizarImputsEnClientes(Cliente cliente) {
        if (cliente == null) {
            txtNombreCliente.setText("");
            txtDireccionCliente.setText("");
            txtTelefonoCliente.setText("");
            txtDniCliente.setText("");
        } else {
            txtNombreCliente.setText(cliente.getNombre());
            txtDireccionCliente.setText(cliente.getDireccion());
            txtTelefonoCliente.setText(cliente.getTelefono() + "");
            txtDniCliente.setText(cliente.getDni() + "");
            seleccionarItemEnTablaClintesPorId(cliente.getId());
        }
    }

    private void seleccionarItemEnTablaClintesPorId(int idBuscado) {
        for (int i = 0; i < tbClientes.getRowCount(); i++) {
            int id = (int) tbClientes.getValueAt(i, 0);
            if (idBuscado == id) {
                tbClientes.setRowSelectionInterval(i, i);
            }
        }
    }

    // FUNCIONES DEL MODULO DE PRODUCTOS 
    public void actualizarTablaDeProductos(ArbolProducto arbolProducto) {
        DefaultTableModel modelo = (DefaultTableModel) tbProductos.getModel();
        modelo.setRowCount(0);

        String seleccionado = (String) cbxOrdenABB.getSelectedItem();
        arbolProducto.mostrarOrdenado(seleccionado, modelo);
        System.out.println("Cambio ordenamiento:: " + seleccionado);

    }

    public void actualizarImputsEnProductos(Producto producto) {
        if (producto == null) {
            txtCodigoProducto.setText("");
            txtDesProducto.setText("");
            txtPrecioProducto.setText("");
            txtCantProducto.setText("");

        } else {
            txtCodigoProducto.setText(producto.getCodigo());
            txtDesProducto.setText(producto.getNombre());
            txtPrecioProducto.setText(producto.getPrecio() + "");
            seleccionarItemEnTablaProductoPorCodigo(producto.getCodigo());
        }
    }

    private void seleccionarItemEnTablaProductoPorCodigo(String codigo) {
        for (int i = 0; i < tbClientes.getRowCount(); i++) {
            String codigoEnTabla = String.valueOf(tbProductos.getValueAt(i, 1));

            if (codigoEnTabla.equals(codigo)) {
                tbClientes.setRowSelectionInterval(i, i);
            }
        }
    }

    private void cargarOpcionesDeOrdenamientoABB() {
        //cargar tipos de orden
        for (OrdenamientoABB options : OrdenamientoABB.ordenamientos) {
            cbxOrdenABB.addItem(options.getNombre());
        }
    }

    // FUNCIONES DEL MODULO DE PROVEEDORES 
    public void actualizarTablaDeProveedores(ListaProveedores lesProveedores) {
        DefaultTableModel modelo = (DefaultTableModel) tbProveedores.getModel();
        modelo.setRowCount(0);
        lesProveedores.mostrarEnTabla(modelo);
    }

    public void actualizarImputsEnProveedores(Proveedor proveedor) {
        if (proveedor == null) {
            txtNombreProveedor.setText("");
            txtDireccionProveedor.setText("");
            txtTelefonoProveedor.setText("");
            txtRucProveedor.setText("");
        } else {
            txtNombreProveedor.setText(proveedor.getNombre());
            txtDireccionProveedor.setText(proveedor.getDireccion());
            txtTelefonoProveedor.setText(proveedor.getTelefono() + "");
            seleccionarItemEnTablaProveedoresPorId(proveedor.getId());
        }
    }

    private void seleccionarItemEnTablaProveedoresPorId(int idBuscado) {
        for (int i = 0; i < tbProveedores.getRowCount(); i++) {
            int id = (int) tbProveedores.getValueAt(i, 0);
            if (idBuscado == id) {
                tbProveedores.setRowSelectionInterval(i, i);
            }
        }
    }

    //FUNCIONES DEL MODULO DE HISTORIAL DE VENTAS
    public void actualizarTablaDeVentas(ListaPilaVentas lpVentas) {
        DefaultTableModel modelo = (DefaultTableModel) tbVentas.getModel();
        modelo.setRowCount(0);
        lpVentas.recorrerATabla(modelo);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txtVentaDelMes = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtVentaEnLaSemana = new javax.swing.JLabel();
        jPanel99 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        txtVentasDeHoy = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel44 = new javax.swing.JLabel();
        btnActualizarNumeroDeVentas = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCodigoDeProductoEnVenta = new javax.swing.JTextField();
        txtCantidadVenta = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbCarritoDeVenta = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnGenerarVenta = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtCarritoTotal = new javax.swing.JLabel();
        txtDniORucClienteEnVenta = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtStockVenta = new javax.swing.JLabel();
        txtNombreClienteVenta = new javax.swing.JLabel();
        txtDescripcionVenta = new javax.swing.JLabel();
        txtTelefonoClienteVenta = new javax.swing.JLabel();
        btnAgregarUnProductoAVenta = new javax.swing.JButton();
        btnBucarProductoEnVentas = new javax.swing.JButton();
        btnEliminarSeleccionVenta = new javax.swing.JButton();
        txtPrecioProductoEnVenta = new javax.swing.JLabel();
        btnBuscarClienteEnVenta = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbClientes = new javax.swing.JTable();
        btnBuscarClienteEnClientes = new javax.swing.JButton();
        btnActualizarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        btnAgregarCliente = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtDniCliente = new javax.swing.JTextField();
        txtTelefonoCliente = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtDireccionCliente = new javax.swing.JTextField();
        btnCancelarEnClientes = new javax.swing.JButton();
        btnAceptarEnClientes = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbProveedores = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtRucProveedor = new javax.swing.JTextField();
        txtTelefonoProveedor = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtNombreProveedor = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtDireccionProveedor = new javax.swing.JTextField();
        btnCancelarEnProveedores = new javax.swing.JButton();
        btnAceptarEnProveedores = new javax.swing.JButton();
        btnBuscarProveedorEnProveedores = new javax.swing.JButton();
        btnAgregarProveedor = new javax.swing.JButton();
        btnActualizarProveedor = new javax.swing.JButton();
        btnEliminarProveedor = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbProductos = new javax.swing.JTable();
        btnActualizarProducto = new javax.swing.JButton();
        btnEliminarProducto = new javax.swing.JButton();
        btnAgregarProducto = new javax.swing.JButton();
        btnBuscarProducto = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        txtDesProducto = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtCantProducto = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtPrecioProducto = new javax.swing.JTextField();
        cbxProveedorProducto = new javax.swing.JComboBox<>();
        btnAceptarEnProductos = new javax.swing.JButton();
        btnCancelarEnProductos = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        cbxOrdenABB = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbVentas = new javax.swing.JTable();
        btnEliminarUltimaVenta = new javax.swing.JButton();
        btnAbrirPDFDeSeleccionado = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnNuevaVenta = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnProveedor = new javax.swing.JButton();
        btnProductos = new javax.swing.JButton();
        txtMenu = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JLabel();
        btnHistorialDeVentas = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        btnRegistrar1 = new javax.swing.JButton();
        jLayeredPane2 = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(246, 244, 242));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setForeground(new java.awt.Color(246, 244, 242));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(246, 244, 242));

        jPanel12.setBackground(java.awt.Color.white);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(java.awt.Color.darkGray);
        jLabel1.setText("Resumen");

        jPanel9.setBackground(new java.awt.Color(237, 246, 247));
        jPanel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel9.setForeground(java.awt.Color.black);

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel34.setForeground(java.awt.Color.darkGray);
        jLabel34.setText("Ventas en el Mes");

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/tienda.png"))); // NOI18N

        txtVentaDelMes.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtVentaDelMes.setForeground(java.awt.Color.darkGray);
        txtVentaDelMes.setText("24");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(txtVentaDelMes, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtVentaDelMes)
                .addGap(22, 22, 22))
        );

        jPanel10.setBackground(new java.awt.Color(237, 246, 247));
        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setForeground(java.awt.Color.darkGray);
        jLabel35.setText("Ventas en la Semana");

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/calendario_semana.png"))); // NOI18N

        txtVentaEnLaSemana.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtVentaEnLaSemana.setForeground(java.awt.Color.darkGray);
        txtVentaEnLaSemana.setText("24");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtVentaEnLaSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(txtVentaEnLaSemana)
                .addGap(22, 22, 22))
        );

        jPanel99.setBackground(new java.awt.Color(237, 246, 247));
        jPanel99.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel99.setForeground(new java.awt.Color(202, 217, 221));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel36.setForeground(java.awt.Color.darkGray);
        jLabel36.setText("Ventas de Hoy");

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/comprador.png"))); // NOI18N

        txtVentasDeHoy.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtVentasDeHoy.setForeground(java.awt.Color.darkGray);
        txtVentasDeHoy.setText("24");

        javax.swing.GroupLayout jPanel99Layout = new javax.swing.GroupLayout(jPanel99);
        jPanel99.setLayout(jPanel99Layout);
        jPanel99Layout.setHorizontalGroup(
            jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel99Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel99Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtVentasDeHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );
        jPanel99Layout.setVerticalGroup(
            jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel99Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtVentasDeHoy)
                .addGap(19, 19, 19))
        );

        jLabel44.setForeground(java.awt.Color.darkGray);
        jLabel44.setText("holaaa");
        jLayeredPane1.add(jLabel44);
        jLabel44.setBounds(25, 43, 43, 16);

        btnActualizarNumeroDeVentas.setBackground(new java.awt.Color(237, 246, 247));
        btnActualizarNumeroDeVentas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizarNumeroDeVentas.setForeground(java.awt.Color.darkGray);
        btnActualizarNumeroDeVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/refrescar.png"))); // NOI18N
        btnActualizarNumeroDeVentas.setToolTipText("");
        btnActualizarNumeroDeVentas.setHideActionText(true);
        btnActualizarNumeroDeVentas.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnActualizarNumeroDeVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarNumeroDeVentasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnActualizarNumeroDeVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)))
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83)
                        .addComponent(jPanel99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(237, 237, 237)
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(105, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizarNumeroDeVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap(68, Short.MAX_VALUE)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel99, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(246, 244, 242));
        jTabbedPane1.setForeground(java.awt.Color.darkGray);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        jTabbedPane1.setEnabled(false);
        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(java.awt.Color.darkGray);
        jLabel3.setText("Código:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(java.awt.Color.darkGray);
        jLabel4.setText("Descripción del producto:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(java.awt.Color.darkGray);
        jLabel5.setText("Cantidad:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(java.awt.Color.darkGray);
        jLabel6.setText("Precio:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(java.awt.Color.darkGray);
        jLabel7.setText("Disponible:");

        txtCodigoDeProductoEnVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoDeProductoEnVentaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoDeProductoEnVentaKeyTyped(evt);
            }
        });

        txtCantidadVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadVentaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadVentaKeyTyped(evt);
            }
        });

        tbCarritoDeVenta.setBackground(java.awt.Color.darkGray);
        tbCarritoDeVenta.setForeground(java.awt.Color.white);
        tbCarritoDeVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "DESCRIPCION", "PRECIO", "CANTIDAD", "TOTAL"
            }
        ));
        jScrollPane1.setViewportView(tbCarritoDeVenta);
        if (tbCarritoDeVenta.getColumnModel().getColumnCount() > 0) {
            tbCarritoDeVenta.getColumnModel().getColumn(0).setPreferredWidth(30);
            tbCarritoDeVenta.getColumnModel().getColumn(1).setPreferredWidth(100);
            tbCarritoDeVenta.getColumnModel().getColumn(2).setPreferredWidth(30);
            tbCarritoDeVenta.getColumnModel().getColumn(3).setPreferredWidth(30);
            tbCarritoDeVenta.getColumnModel().getColumn(4).setPreferredWidth(40);
        }

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(java.awt.Color.darkGray);
        jLabel8.setText("DNI/RUC:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(java.awt.Color.darkGray);
        jLabel9.setText("NOMBRE:");

        btnGenerarVenta.setBackground(new java.awt.Color(0, 156, 121));
        btnGenerarVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGenerarVenta.setForeground(java.awt.Color.white);
        btnGenerarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/tarjeta_de_debito.png"))); // NOI18N
        btnGenerarVenta.setText("Generar Venta");
        btnGenerarVenta.setToolTipText("");
        btnGenerarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarVentaActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 156, 121));
        jLabel10.setText("TOTAL");

        txtCarritoTotal.setForeground(new java.awt.Color(0, 156, 121));
        txtCarritoTotal.setText("-----");

        txtDniORucClienteEnVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDniORucClienteEnVentaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDniORucClienteEnVentaKeyTyped(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setForeground(java.awt.Color.darkGray);
        jLabel11.setText("TELEFONO:");

        txtStockVenta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtStockVenta.setForeground(java.awt.Color.darkGray);
        txtStockVenta.setText("...");

        txtNombreClienteVenta.setForeground(java.awt.Color.darkGray);
        txtNombreClienteVenta.setText("...");

        txtDescripcionVenta.setForeground(java.awt.Color.darkGray);
        txtDescripcionVenta.setText("...");

        txtTelefonoClienteVenta.setForeground(java.awt.Color.darkGray);
        txtTelefonoClienteVenta.setText("...");

        btnAgregarUnProductoAVenta.setBackground(new java.awt.Color(184, 207, 206));
        btnAgregarUnProductoAVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAgregarUnProductoAVenta.setForeground(java.awt.Color.darkGray);
        btnAgregarUnProductoAVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/carrito.png"))); // NOI18N
        btnAgregarUnProductoAVenta.setText("Agregar ");
        btnAgregarUnProductoAVenta.setToolTipText("");
        btnAgregarUnProductoAVenta.setHideActionText(true);
        btnAgregarUnProductoAVenta.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnAgregarUnProductoAVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarUnProductoAVentaActionPerformed(evt);
            }
        });

        btnBucarProductoEnVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar.png"))); // NOI18N
        btnBucarProductoEnVentas.setText("Buscar");
        btnBucarProductoEnVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBucarProductoEnVentasActionPerformed(evt);
            }
        });

        btnEliminarSeleccionVenta.setBackground(new java.awt.Color(246, 244, 242));
        btnEliminarSeleccionVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminarSeleccionVenta.setForeground(java.awt.Color.darkGray);
        btnEliminarSeleccionVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/bolsa_de_compras.png"))); // NOI18N
        btnEliminarSeleccionVenta.setText(" Borrar Seleccion");
        btnEliminarSeleccionVenta.setToolTipText("");
        btnEliminarSeleccionVenta.setHideActionText(true);
        btnEliminarSeleccionVenta.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnEliminarSeleccionVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSeleccionVentaActionPerformed(evt);
            }
        });

        txtPrecioProductoEnVenta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtPrecioProductoEnVenta.setForeground(java.awt.Color.darkGray);
        txtPrecioProductoEnVenta.setText("...");

        btnBuscarClienteEnVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/buscar.png"))); // NOI18N
        btnBuscarClienteEnVenta.setText("Buscar");
        btnBuscarClienteEnVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteEnVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(71, 71, 71)
                                        .addComponent(btnBucarProductoEnVentas)
                                        .addGap(74, 74, 74)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(txtCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(txtCodigoDeProductoEnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDescripcionVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(76, 76, 76)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPrecioProductoEnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(btnAgregarUnProductoAVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtStockVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDniORucClienteEnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(btnBuscarClienteEnVenta))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 785, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(jLabel10)
                                            .addGap(108, 108, 108)
                                            .addComponent(txtCarritoTotal))
                                        .addComponent(btnGenerarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel11)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtTelefonoClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtNombreClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(535, 535, 535)))))
                        .addContainerGap(101, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(661, Short.MAX_VALUE)
                    .addComponent(btnEliminarSeleccionVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(91, 91, 91)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel43)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtStockVenta)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCodigoDeProductoEnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBucarProductoEnVentas)
                            .addComponent(btnAgregarUnProductoAVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecioProductoEnVenta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtDescripcionVenta))))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtDniORucClienteEnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarClienteEnVenta))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtNombreClienteVenta))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtCarritoTotal)
                    .addComponent(jLabel11)
                    .addComponent(txtTelefonoClienteVenta))
                .addGap(27, 27, 27)
                .addComponent(btnGenerarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(402, Short.MAX_VALUE)
                    .addComponent(btnEliminarSeleccionVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(154, 154, 154)))
        );

        jTabbedPane1.addTab("1", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tbClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI/RUC", "NOMBRE", "TELEFONO", "DIRECCION"
            }
        ));
        tbClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClientesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbClientes);
        if (tbClientes.getColumnModel().getColumnCount() > 0) {
            tbClientes.getColumnModel().getColumn(0).setPreferredWidth(20);
            tbClientes.getColumnModel().getColumn(1).setPreferredWidth(50);
            tbClientes.getColumnModel().getColumn(2).setPreferredWidth(100);
            tbClientes.getColumnModel().getColumn(3).setPreferredWidth(50);
            tbClientes.getColumnModel().getColumn(4).setPreferredWidth(80);
        }

        btnBuscarClienteEnClientes.setBackground(new java.awt.Color(218, 210, 255));
        btnBuscarClienteEnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lupa.png"))); // NOI18N
        btnBuscarClienteEnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteEnClientesActionPerformed(evt);
            }
        });

        btnActualizarCliente.setBackground(new java.awt.Color(218, 210, 255));
        btnActualizarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/editar.png"))); // NOI18N
        btnActualizarCliente.setToolTipText("");
        btnActualizarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setBackground(new java.awt.Color(218, 210, 255));
        btnEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar.png"))); // NOI18N
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        btnAgregarCliente.setBackground(new java.awt.Color(218, 210, 255));
        btnAgregarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/sumar.png"))); // NOI18N
        btnAgregarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClienteActionPerformed(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(246, 244, 242));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setForeground(java.awt.Color.darkGray);
        jLabel12.setText("DNI/RUC:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setForeground(java.awt.Color.darkGray);
        jLabel14.setText("Teléfono:");

        txtDniCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDniClienteKeyTyped(evt);
            }
        });

        txtTelefonoCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoClienteKeyTyped(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setForeground(java.awt.Color.darkGray);
        jLabel13.setText("Nombre:");

        txtNombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreClienteKeyTyped(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setForeground(java.awt.Color.darkGray);
        jLabel16.setText("Dirección:");

        btnCancelarEnClientes.setText("Cancelar");
        btnCancelarEnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEnClientesActionPerformed(evt);
            }
        });

        btnAceptarEnClientes.setBackground(new java.awt.Color(186, 72, 127));
        btnAceptarEnClientes.setForeground(java.awt.Color.white);
        btnAceptarEnClientes.setText("Aceptar");
        btnAceptarEnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarEnClientesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDniCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(btnCancelarEnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(47, 47, 47)
                            .addComponent(btnAceptarEnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtDniCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelarEnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptarEnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(618, Short.MAX_VALUE)
                .addComponent(btnBuscarClienteEnClientes)
                .addGap(18, 18, 18)
                .addComponent(btnAgregarCliente)
                .addGap(18, 18, 18)
                .addComponent(btnActualizarCliente)
                .addGap(18, 18, 18)
                .addComponent(btnEliminarCliente)
                .addGap(120, 120, 120))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBuscarClienteEnClientes)
                    .addComponent(btnAgregarCliente)
                    .addComponent(btnActualizarCliente)
                    .addComponent(btnEliminarCliente))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(192, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("2", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        tbProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "RUC", "NOMBRE", "TELEFONO", "DIRECCION"
            }
        ));
        tbProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProveedoresMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbProveedores);
        if (tbProveedores.getColumnModel().getColumnCount() > 0) {
            tbProveedores.getColumnModel().getColumn(0).setPreferredWidth(20);
            tbProveedores.getColumnModel().getColumn(1).setPreferredWidth(40);
            tbProveedores.getColumnModel().getColumn(2).setPreferredWidth(100);
            tbProveedores.getColumnModel().getColumn(3).setPreferredWidth(50);
            tbProveedores.getColumnModel().getColumn(4).setPreferredWidth(80);
        }

        jPanel14.setBackground(new java.awt.Color(246, 244, 242));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setForeground(java.awt.Color.darkGray);
        jLabel15.setText("RUC:");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel28.setForeground(java.awt.Color.darkGray);
        jLabel28.setText("Teléfono:");

        txtRucProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtRucProveedorKeyTyped(evt);
            }
        });

        txtTelefonoProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoProveedorKeyTyped(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setForeground(java.awt.Color.darkGray);
        jLabel29.setText("Nombre:");

        txtNombreProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreProveedorKeyTyped(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel30.setForeground(java.awt.Color.darkGray);
        jLabel30.setText("Dirección:");

        btnCancelarEnProveedores.setText("Cancelar");
        btnCancelarEnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEnProveedoresActionPerformed(evt);
            }
        });

        btnAceptarEnProveedores.setBackground(new java.awt.Color(186, 72, 127));
        btnAceptarEnProveedores.setForeground(java.awt.Color.white);
        btnAceptarEnProveedores.setText("Aceptar");
        btnAceptarEnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarEnProveedoresActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRucProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTelefonoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addComponent(btnCancelarEnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(47, 47, 47)
                            .addComponent(btnAceptarEnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addComponent(jLabel30)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDireccionProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtRucProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtTelefonoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(txtDireccionProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelarEnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptarEnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        btnBuscarProveedorEnProveedores.setBackground(new java.awt.Color(218, 210, 255));
        btnBuscarProveedorEnProveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lupa.png"))); // NOI18N
        btnBuscarProveedorEnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorEnProveedoresActionPerformed(evt);
            }
        });

        btnAgregarProveedor.setBackground(new java.awt.Color(218, 210, 255));
        btnAgregarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/sumar.png"))); // NOI18N
        btnAgregarProveedor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProveedorActionPerformed(evt);
            }
        });

        btnActualizarProveedor.setBackground(new java.awt.Color(218, 210, 255));
        btnActualizarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/editar.png"))); // NOI18N
        btnActualizarProveedor.setToolTipText("");
        btnActualizarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProveedorActionPerformed(evt);
            }
        });

        btnEliminarProveedor.setBackground(new java.awt.Color(218, 210, 255));
        btnEliminarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar.png"))); // NOI18N
        btnEliminarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProveedorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnBuscarProveedorEnProveedores)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarProveedor)
                        .addGap(18, 18, 18)
                        .addComponent(btnActualizarProveedor)
                        .addGap(29, 29, 29)
                        .addComponent(btnEliminarProveedor))
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscarProveedorEnProveedores)
                    .addComponent(btnAgregarProveedor)
                    .addComponent(btnActualizarProveedor)
                    .addComponent(btnEliminarProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(248, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("3", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        tbProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CODIGO", "DESCRIPCION", "PROOVEEDOR", "STOCK", "PRECIO"
            }
        ));
        tbProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProductosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tbProductos);
        if (tbProductos.getColumnModel().getColumnCount() > 0) {
            tbProductos.getColumnModel().getColumn(0).setPreferredWidth(20);
            tbProductos.getColumnModel().getColumn(1).setPreferredWidth(50);
            tbProductos.getColumnModel().getColumn(2).setPreferredWidth(100);
            tbProductos.getColumnModel().getColumn(3).setPreferredWidth(60);
            tbProductos.getColumnModel().getColumn(4).setPreferredWidth(40);
            tbProductos.getColumnModel().getColumn(5).setPreferredWidth(40);
        }

        btnActualizarProducto.setBackground(new java.awt.Color(218, 210, 255));
        btnActualizarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/editar.png"))); // NOI18N
        btnActualizarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProductoActionPerformed(evt);
            }
        });

        btnEliminarProducto.setBackground(new java.awt.Color(218, 210, 255));
        btnEliminarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar.png"))); // NOI18N
        btnEliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoActionPerformed(evt);
            }
        });

        btnAgregarProducto.setBackground(new java.awt.Color(218, 210, 255));
        btnAgregarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/sumar.png"))); // NOI18N
        btnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoActionPerformed(evt);
            }
        });

        btnBuscarProducto.setBackground(new java.awt.Color(218, 210, 255));
        btnBuscarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/lupa.png"))); // NOI18N
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        jPanel13.setBackground(new java.awt.Color(246, 244, 242));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setForeground(java.awt.Color.darkGray);
        jLabel22.setText("Código:");

        txtCodigoProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoProductoKeyTyped(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setForeground(java.awt.Color.darkGray);
        jLabel23.setText("Descripción:");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setForeground(java.awt.Color.darkGray);
        jLabel24.setText("Cantidad:");

        txtCantProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantProductoActionPerformed(evt);
            }
        });
        txtCantProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantProductoKeyTyped(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setForeground(java.awt.Color.darkGray);
        jLabel25.setText("Precio:");

        txtPrecioProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrecioProductoKeyTyped(evt);
            }
        });

        cbxProveedorProducto.setBackground(new java.awt.Color(186, 72, 127));
        cbxProveedorProducto.setEditable(true);

        btnAceptarEnProductos.setBackground(new java.awt.Color(186, 72, 127));
        btnAceptarEnProductos.setForeground(java.awt.Color.white);
        btnAceptarEnProductos.setText("Aceptar");
        btnAceptarEnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarEnProductosActionPerformed(evt);
            }
        });

        btnCancelarEnProductos.setText("Cancelar");
        btnCancelarEnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEnProductosActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel27.setForeground(java.awt.Color.darkGray);
        jLabel27.setText("Proveedor:");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(txtPrecioProducto)
                        .addGap(108, 108, 108))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantProducto)
                        .addGap(107, 107, 107))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbxProveedorProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDesProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 56, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelarEnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnAceptarEnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDesProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtCantProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxProveedorProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptarEnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarEnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setForeground(java.awt.Color.darkGray);
        jLabel26.setText("Ordenar: ");

        cbxOrdenABB.setBackground(new java.awt.Color(186, 72, 127));
        cbxOrdenABB.setEditable(true);
        cbxOrdenABB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxOrdenABBItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel26)
                        .addGap(22, 22, 22)
                        .addComponent(cbxOrdenABB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnActualizarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                            .addComponent(btnActualizarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAgregarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBuscarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(cbxOrdenABB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(178, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("4", jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        tbVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CLIENTE", "VENDEDOR", "FECHA", "TOTAL"
            }
        ));
        tbVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbVentasMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tbVentas);
        if (tbVentas.getColumnModel().getColumnCount() > 0) {
            tbVentas.getColumnModel().getColumn(0).setPreferredWidth(20);
            tbVentas.getColumnModel().getColumn(1).setPreferredWidth(60);
            tbVentas.getColumnModel().getColumn(2).setPreferredWidth(60);
            tbVentas.getColumnModel().getColumn(4).setPreferredWidth(60);
        }

        btnEliminarUltimaVenta.setBackground(new java.awt.Color(3, 166, 161));
        btnEliminarUltimaVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminarUltimaVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/eliminar.png"))); // NOI18N
        btnEliminarUltimaVenta.setText("Eliminar Ultima Venta");
        btnEliminarUltimaVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarUltimaVentaActionPerformed(evt);
            }
        });

        btnAbrirPDFDeSeleccionado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAbrirPDFDeSeleccionado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/pdf.png"))); // NOI18N
        btnAbrirPDFDeSeleccionado.setText("Abrir PDF de selccionado ");
        btnAbrirPDFDeSeleccionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirPDFDeSeleccionadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(btnEliminarUltimaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                    .addContainerGap(643, Short.MAX_VALUE)
                    .addComponent(btnAbrirPDFDeSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(52, 52, 52)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btnEliminarUltimaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(191, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                    .addContainerGap(350, Short.MAX_VALUE)
                    .addComponent(btnAbrirPDFDeSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(198, 198, 198)))
        );

        jTabbedPane1.addTab("5", jPanel6);

        jPanel1.setBackground(new java.awt.Color(246, 244, 242));

        btnNuevaVenta.setBackground(new java.awt.Color(246, 244, 242));
        btnNuevaVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNuevaVenta.setForeground(java.awt.Color.darkGray);
        btnNuevaVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/carrito.png"))); // NOI18N
        btnNuevaVenta.setText("Nueva venta");
        btnNuevaVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaVentaActionPerformed(evt);
            }
        });

        btnClientes.setBackground(new java.awt.Color(246, 244, 242));
        btnClientes.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClientes.setForeground(java.awt.Color.black);
        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Clientes.png"))); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });

        btnProveedor.setBackground(new java.awt.Color(246, 244, 242));
        btnProveedor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnProveedor.setForeground(java.awt.Color.black);
        btnProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/proveedores.png"))); // NOI18N
        btnProveedor.setText("Proveedores");
        btnProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedorActionPerformed(evt);
            }
        });

        btnProductos.setBackground(new java.awt.Color(246, 244, 242));
        btnProductos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnProductos.setForeground(java.awt.Color.black);
        btnProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/productos.png"))); // NOI18N
        btnProductos.setText("Productos");
        btnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductosActionPerformed(evt);
            }
        });

        txtMenu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtMenu.setForeground(java.awt.Color.darkGray);
        txtMenu.setText("Menu");
        txtMenu.setToolTipText("");

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/logo.png"))); // NOI18N
        jLabel33.setText("jLabel33");

        txtUserName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtUserName.setForeground(java.awt.Color.darkGray);
        txtUserName.setText("...");

        btnHistorialDeVentas.setBackground(new java.awt.Color(246, 244, 242));
        btnHistorialDeVentas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHistorialDeVentas.setForeground(java.awt.Color.black);
        btnHistorialDeVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/historial_de_ventas.png"))); // NOI18N
        btnHistorialDeVentas.setText("Historial de Ventas");
        btnHistorialDeVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistorialDeVentasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMenu)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnNuevaVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnHistorialDeVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnProductos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(txtUserName)
                .addGap(35, 35, 35)
                .addComponent(txtMenu)
                .addGap(28, 28, 28)
                .addComponent(btnNuevaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnHistorialDeVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        btnCerrarSesion.setBackground(new java.awt.Color(237, 246, 247));
        btnCerrarSesion.setForeground(java.awt.Color.black);
        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cerrar_sesion.png"))); // NOI18N
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        btnRegistrar1.setBackground(new java.awt.Color(237, 246, 247));
        btnRegistrar1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar1.setForeground(java.awt.Color.black);
        btnRegistrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/agregar_usuario.png"))); // NOI18N
        btnRegistrar1.setText("Crear Usuario");
        btnRegistrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(btnRegistrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRegistrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, 950));

        jLayeredPane2.setLayout(new javax.swing.OverlayLayout(jLayeredPane2));
        getContentPane().add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 890, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        clienteController = new ClienteController(this);
        seleccionarBotonPorIndice(0, opcionesDeClientes);
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedorActionPerformed
        proveedorController = new ProveedorController(this);
        seleccionarBotonPorIndice(0, opcionesDeProveedores);
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_btnProveedorActionPerformed

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosActionPerformed
        productoController = new ProductoController(this);
        seleccionarBotonPorIndice(0, opcionesDeProductos);
        jTabbedPane1.setSelectedIndex(3);

        // cargarProveedores
        cbxProveedorProducto.removeAllItems();
        for (Proveedor prov : productoController.obtenerOpcionesDeProveedores()) {
            cbxProveedorProducto.addItem(prov.getNombre());
        }

    }//GEN-LAST:event_btnProductosActionPerformed

    private void btnNuevaVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaVentaActionPerformed
        jTabbedPane1.setSelectedIndex(0);
        crearVentaController.refreshListaDeProductos();
    }//GEN-LAST:event_btnNuevaVentaActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnRegistrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrar1ActionPerformed
        AddUser addUserView = new AddUser();
        addUserView.setVisible(true);
    }//GEN-LAST:event_btnRegistrar1ActionPerformed

    private void btnEliminarUltimaVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarUltimaVentaActionPerformed
        historialVentasController.enEliminarUltimaVenta();
    }//GEN-LAST:event_btnEliminarUltimaVentaActionPerformed

    private void tbVentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbVentasMouseClicked

    }//GEN-LAST:event_tbVentasMouseClicked

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed

    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoActionPerformed
        seleccionarBotonPorIndice(1, opcionesDeProductos);
    }//GEN-LAST:event_btnAgregarProductoActionPerformed

    private void btnEliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoActionPerformed

        seleccionarBotonPorIndice(3, opcionesDeProductos);
    }//GEN-LAST:event_btnEliminarProductoActionPerformed

    private void btnActualizarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProductoActionPerformed

        seleccionarBotonPorIndice(2, opcionesDeProductos);
    }//GEN-LAST:event_btnActualizarProductoActionPerformed

    private void tbProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosMouseClicked
        int fila = tbProductos.rowAtPoint(evt.getPoint());
        txtCodigoProducto.setText(tbProductos.getValueAt(fila, 1).toString());
        txtDesProducto.setText(tbProductos.getValueAt(fila, 2).toString());
        cbxProveedorProducto.setSelectedItem(tbProductos.getValueAt(fila, 3).toString());
        txtCantProducto.setText(tbProductos.getValueAt(fila, 4).toString());
        txtPrecioProducto.setText(tbProductos.getValueAt(fila, 5).toString());
    }//GEN-LAST:event_tbProductosMouseClicked

    private void txtPrecioProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioProductoKeyTyped
        event.numberDecimalKeyPress(evt, txtPrecioProducto);
    }//GEN-LAST:event_txtPrecioProductoKeyTyped

    private void txtCantProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantProductoKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtCantProductoKeyTyped

    private void txtCantProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantProductoActionPerformed

    private void txtCodigoProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtCodigoProductoKeyTyped


    private void btnAgregarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClienteActionPerformed
        seleccionarBotonPorIndice(1, opcionesDeClientes);
    }//GEN-LAST:event_btnAgregarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        seleccionarBotonPorIndice(3, opcionesDeClientes);
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnActualizarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarClienteActionPerformed
        seleccionarBotonPorIndice(2, opcionesDeClientes);
    }//GEN-LAST:event_btnActualizarClienteActionPerformed

    private void btnBuscarClienteEnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteEnClientesActionPerformed
        seleccionarBotonPorIndice(0, opcionesDeClientes);
    }//GEN-LAST:event_btnBuscarClienteEnClientesActionPerformed

    private void tbClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMouseClicked
        int fila = tbClientes.rowAtPoint(evt.getPoint());
        txtDniCliente.setText(tbClientes.getValueAt(fila, 1).toString());
        txtNombreCliente.setText(tbClientes.getValueAt(fila, 2).toString());
        txtTelefonoCliente.setText(tbClientes.getValueAt(fila, 3).toString());
        txtDireccionCliente.setText(tbClientes.getValueAt(fila, 4).toString());
    }//GEN-LAST:event_tbClientesMouseClicked

    private void txtTelefonoClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoClienteKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtTelefonoClienteKeyTyped

    private void txtNombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreClienteKeyTyped

    }//GEN-LAST:event_txtNombreClienteKeyTyped

    private void txtDniClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniClienteKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtDniClienteKeyTyped

    private void btnBuscarClienteEnVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteEnVentaActionPerformed
        crearVentaController.enBuscarCliente(txtDniORucClienteEnVenta.getText());
    }//GEN-LAST:event_btnBuscarClienteEnVentaActionPerformed

    private void btnEliminarSeleccionVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSeleccionVentaActionPerformed
        crearVentaController.enEliminarSeleccionado(tbCarritoDeVenta.getSelectedRow());
    }//GEN-LAST:event_btnEliminarSeleccionVentaActionPerformed

    private void btnBucarProductoEnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBucarProductoEnVentasActionPerformed
        crearVentaController.enBuscarProducto(txtCodigoDeProductoEnVenta.getText());
    }//GEN-LAST:event_btnBucarProductoEnVentasActionPerformed

    private void btnAgregarUnProductoAVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarUnProductoAVentaActionPerformed
        if ("".equals(txtCantidadVenta.getText())) {
            mostrarMensaje("Ingrese cantidad");
            return;
        }

        crearVentaController.enAgregarAlCarrito(txtCodigoDeProductoEnVenta.getText(), txtCantidadVenta.getText());
    }//GEN-LAST:event_btnAgregarUnProductoAVentaActionPerformed

    private void txtDniORucClienteEnVentaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniORucClienteEnVentaKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtDniORucClienteEnVentaKeyTyped

    private void txtDniORucClienteEnVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniORucClienteEnVentaKeyPressed

    }//GEN-LAST:event_txtDniORucClienteEnVentaKeyPressed

    private void btnGenerarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarVentaActionPerformed

        crearVentaController.enGenerarVenta(txtDniORucClienteEnVenta.getText(), sesion.getId() + "");
    }//GEN-LAST:event_btnGenerarVentaActionPerformed

    private void txtCantidadVentaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadVentaKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtCantidadVentaKeyTyped

    private void txtCantidadVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadVentaKeyPressed

    }//GEN-LAST:event_txtCantidadVentaKeyPressed

    private void txtCodigoDeProductoEnVentaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoDeProductoEnVentaKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtCodigoDeProductoEnVentaKeyTyped

    private void txtCodigoDeProductoEnVentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoDeProductoEnVentaKeyPressed

    }//GEN-LAST:event_txtCodigoDeProductoEnVentaKeyPressed

    private int tryToGetIdFromCustomerTable() {
        try {
            //obtenemos el id 
            int selectedRow = tbClientes.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tbClientes.getValueAt(selectedRow, 0);
                return id;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
    private void btnAceptarEnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarEnClientesActionPerformed
        Cliente cliente = new Cliente();
        try {
            int id = tryToGetIdFromCustomerTable();
            System.out.println("--" + id + "--");
            cliente.setId(id);
            cliente.setNombre(txtNombreCliente.getText());
            cliente.setDireccion(txtDireccionCliente.getText());

            cliente.setDni(Integer.parseInt(txtDniCliente.getText()));
            cliente.setTelefono(Integer.parseInt(txtTelefonoCliente.getText()));

        } catch (NumberFormatException e) {
            cliente.setTelefono(0);
            if (cliente.getDni() == 0) {
                mostrarMensaje("Por favor ingrese datos válidos.");
                return;
            }
        }

        JButton seleccionado = obtenerBotonSeleccionado(opcionesDeClientes);
        if (seleccionado == btnBuscarClienteEnClientes) {
            clienteController.enBuscarCliente(cliente.getDni());
        } else if (seleccionado == btnAgregarCliente) {
            clienteController.enAgregarCliente(cliente);
        } else if (seleccionado == btnActualizarCliente) {
            clienteController.enActualizarCliente(cliente);
        } else if (seleccionado == btnEliminarCliente) {
            clienteController.enEliminarCliente(cliente.getId());
        } else {
            mostrarMensaje("Selecciona una accion, buscar, agregar, Actualizar o Eliminar");
        }
    }//GEN-LAST:event_btnAceptarEnClientesActionPerformed

    private void btnCancelarEnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEnClientesActionPerformed
        actualizarImputsEnClientes(null);
    }//GEN-LAST:event_btnCancelarEnClientesActionPerformed

    private int tryToGetIdFromProductTable() {
        try {
            //obtenemos el id 
            int selectedRow = tbProductos.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tbProductos.getValueAt(selectedRow, 0);
                return id;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
    private void btnAceptarEnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarEnProductosActionPerformed
        Producto producto = new Producto();
        try {
            int id = tryToGetIdFromProductTable();
            System.out.println("--" + id + "--");
            producto.setId(id);
            producto.setCodigo(txtCodigoProducto.getText());
            producto.setNombre(txtDesProducto.getText());
            producto.setProveedor(cbxProveedorProducto.getSelectedItem().toString());
            producto.setStock(Integer.parseInt(txtCantProducto.getText()));
            producto.setPrecio(Double.parseDouble(txtPrecioProducto.getText()));

        } catch (NumberFormatException e) {
            producto.setStock(0);
            producto.setPrecio(0);
            if (producto.getCodigo().isBlank()) {
                mostrarMensaje("Por favor ingrese datos válidos.");
                return;
            }
        }
        JButton seleccionado = obtenerBotonSeleccionado(opcionesDeProductos);
        if (seleccionado == btnBuscarProducto) {
            productoController.enBuscarProducto(producto.getCodigo());
        } else if (seleccionado == btnAgregarProducto) {
            productoController.enAgregarProducto(producto);
        } else if (seleccionado == btnActualizarProducto) {
            productoController.enActualizarProducto(producto);
        } else if (seleccionado == btnEliminarProducto) {
            productoController.enEliminarProducto(producto.getCodigo());
        } else {
            mostrarMensaje("Selecciona una accion, buscar, agregar, Actualizar o Eliminar");
        }
    }//GEN-LAST:event_btnAceptarEnProductosActionPerformed

    private void btnCancelarEnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEnProductosActionPerformed
        actualizarImputsEnProductos(null);
    }//GEN-LAST:event_btnCancelarEnProductosActionPerformed

    private void cbxOrdenABBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxOrdenABBItemStateChanged
        if (productoController == null) {
            return;
        }
        productoController.enOrdenamientoCambio();
    }//GEN-LAST:event_cbxOrdenABBItemStateChanged

    private void btnHistorialDeVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistorialDeVentasActionPerformed
        historialVentasController = new HistorialVentasController(this);
        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_btnHistorialDeVentasActionPerformed

    private int tryToGetIdFromProviderTable() {
        try {
            //obtenemos el id 
            int selectedRow = tbProveedores.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tbProveedores.getValueAt(selectedRow, 0);
                return id;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
    private void btnAceptarEnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarEnProveedoresActionPerformed

        Proveedor proveedor = new Proveedor();
        try {
            int id = tryToGetIdFromProviderTable();
            System.out.println("--" + id + "--");
            proveedor.setId(id);
            proveedor.setNombre(txtNombreProveedor.getText());
            proveedor.setDireccion(txtDireccionProveedor.getText());

            proveedor.setRuc(Integer.parseInt(txtRucProveedor.getText()));
            proveedor.setTelefono(Integer.parseInt(txtTelefonoProveedor.getText()));

        } catch (NumberFormatException e) {
            proveedor.setTelefono(0);
            if (proveedor.getRuc() == 0) {
                mostrarMensaje("Por favor ingrese datos válidos.");
                return;
            }
        }

        JButton seleccionado = obtenerBotonSeleccionado(opcionesDeProveedores);
        if (seleccionado == btnBuscarProveedorEnProveedores) {
            proveedorController.enBuscarProveedor(proveedor.getRuc());
        } else if (seleccionado == btnAgregarProveedor) {
            proveedorController.enAgregarProveedor(proveedor);
        } else if (seleccionado == btnActualizarProveedor) {
            proveedorController.enActualizarProveedor(proveedor);
        } else if (seleccionado == btnEliminarProveedor) {
            proveedorController.enEliminarProveedor(proveedor.getId());
        } else {
            mostrarMensaje("Selecciona una accion, buscar, agregar, Actualizar o Eliminar");
        }
    }//GEN-LAST:event_btnAceptarEnProveedoresActionPerformed

    private void btnCancelarEnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEnProveedoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelarEnProveedoresActionPerformed

    private void txtNombreProveedorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreProveedorKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreProveedorKeyTyped

    private void txtTelefonoProveedorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoProveedorKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtTelefonoProveedorKeyTyped

    private void txtRucProveedorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRucProveedorKeyTyped
        event.numberKeyPress(evt);
    }//GEN-LAST:event_txtRucProveedorKeyTyped

    private void tbProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProveedoresMouseClicked
        int fila = tbProveedores.rowAtPoint(evt.getPoint());
        txtRucProveedor.setText(tbProveedores.getValueAt(fila, 1).toString());
        txtNombreProveedor.setText(tbProveedores.getValueAt(fila, 2).toString());
        txtTelefonoProveedor.setText(tbProveedores.getValueAt(fila, 3).toString());
        txtDireccionProveedor.setText(tbProveedores.getValueAt(fila, 4).toString());
    }//GEN-LAST:event_tbProveedoresMouseClicked

    private void btnBuscarProveedorEnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProveedorEnProveedoresActionPerformed
        seleccionarBotonPorIndice(0, opcionesDeProveedores);
    }//GEN-LAST:event_btnBuscarProveedorEnProveedoresActionPerformed

    private void btnAgregarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProveedorActionPerformed
        seleccionarBotonPorIndice(1, opcionesDeProveedores);
    }//GEN-LAST:event_btnAgregarProveedorActionPerformed

    private void btnActualizarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProveedorActionPerformed
        seleccionarBotonPorIndice(2, opcionesDeProveedores);
    }//GEN-LAST:event_btnActualizarProveedorActionPerformed

    private void btnEliminarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProveedorActionPerformed
        seleccionarBotonPorIndice(3, opcionesDeProveedores);
    }//GEN-LAST:event_btnEliminarProveedorActionPerformed

    private void btnAbrirPDFDeSeleccionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirPDFDeSeleccionadoActionPerformed
        try {

            int selectedRow = tbVentas.getSelectedRow();
            if (selectedRow == -1) {
                mostrarMensaje("No hay fila seleccionada");
                return;
            }

            int id = (int) tbVentas.getValueAt(selectedRow, 0);
            String fecha = String.valueOf(tbVentas.getValueAt(selectedRow, 3));
            String fileName = BoletaPDF.buildFileName(id, fecha);
            File file = new File("src/pdf/" + fileName);
            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            mostrarMensaje("Algo salio mal al abrir el archivo");
        }
    }//GEN-LAST:event_btnAbrirPDFDeSeleccionadoActionPerformed

    private void btnActualizarNumeroDeVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarNumeroDeVentasActionPerformed
        crearVentaController.enActualizarVentasRealizadas();
    }//GEN-LAST:event_btnActualizarNumeroDeVentasActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirPDFDeSeleccionado;
    private javax.swing.JButton btnAceptarEnClientes;
    private javax.swing.JButton btnAceptarEnProductos;
    private javax.swing.JButton btnAceptarEnProveedores;
    private javax.swing.JButton btnActualizarCliente;
    private javax.swing.JButton btnActualizarNumeroDeVentas;
    private javax.swing.JButton btnActualizarProducto;
    private javax.swing.JButton btnActualizarProveedor;
    private javax.swing.JButton btnAgregarCliente;
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnAgregarProveedor;
    private javax.swing.JButton btnAgregarUnProductoAVenta;
    private javax.swing.JButton btnBucarProductoEnVentas;
    private javax.swing.JButton btnBuscarClienteEnClientes;
    private javax.swing.JButton btnBuscarClienteEnVenta;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnBuscarProveedorEnProveedores;
    private javax.swing.JButton btnCancelarEnClientes;
    private javax.swing.JButton btnCancelarEnProductos;
    private javax.swing.JButton btnCancelarEnProveedores;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarProducto;
    private javax.swing.JButton btnEliminarProveedor;
    private javax.swing.JButton btnEliminarSeleccionVenta;
    private javax.swing.JButton btnEliminarUltimaVenta;
    private javax.swing.JButton btnGenerarVenta;
    private javax.swing.JButton btnHistorialDeVentas;
    private javax.swing.JButton btnNuevaVenta;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnProveedor;
    private javax.swing.JButton btnRegistrar1;
    private javax.swing.JComboBox<String> cbxOrdenABB;
    private javax.swing.JComboBox<String> cbxProveedorProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel99;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tbCarritoDeVenta;
    private javax.swing.JTable tbClientes;
    private javax.swing.JTable tbProductos;
    private javax.swing.JTable tbProveedores;
    private javax.swing.JTable tbVentas;
    private javax.swing.JTextField txtCantProducto;
    private javax.swing.JTextField txtCantidadVenta;
    private javax.swing.JLabel txtCarritoTotal;
    private javax.swing.JTextField txtCodigoDeProductoEnVenta;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDesProducto;
    private javax.swing.JLabel txtDescripcionVenta;
    private javax.swing.JTextField txtDireccionCliente;
    private javax.swing.JTextField txtDireccionProveedor;
    private javax.swing.JTextField txtDniCliente;
    private javax.swing.JTextField txtDniORucClienteEnVenta;
    private javax.swing.JLabel txtMenu;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JLabel txtNombreClienteVenta;
    private javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtPrecioProducto;
    private javax.swing.JLabel txtPrecioProductoEnVenta;
    private javax.swing.JTextField txtRucProveedor;
    private javax.swing.JLabel txtStockVenta;
    private javax.swing.JTextField txtTelefonoCliente;
    private javax.swing.JLabel txtTelefonoClienteVenta;
    private javax.swing.JTextField txtTelefonoProveedor;
    private javax.swing.JLabel txtUserName;
    private javax.swing.JLabel txtVentaDelMes;
    private javax.swing.JLabel txtVentaEnLaSemana;
    private javax.swing.JLabel txtVentasDeHoy;
    // End of variables declaration//GEN-END:variables

}
