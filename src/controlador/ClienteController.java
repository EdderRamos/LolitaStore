/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import EstructuraDeDatos.ListaEnlazada.ListaClientes;
import modelo.Cliente;
import modelo.ClienteDAO;
import vista.Home;

/**
 *
 * @author eramos
 */
public class ClienteController {

    private Home vista;

    private ClienteDAO clienteDao;

    // lista enlazada simple
    private ListaClientes lesClientes;

    public ClienteController(Home vista) {
        this.vista = vista;

        clienteDao = new ClienteDAO();
        lesClientes = new ListaClientes();

        //cargamos a la estructura de datos :D
        for (Cliente cliente : clienteDao.obternerClientes()) {
            lesClientes.agregar(cliente);
        }
        vista.actualizarTablaDeClientes(lesClientes);
    }

    public void enActualizarCliente(Cliente cliente) {
        if (cliente.getId() == 0) {
            vista.mostrarMensaje("Selecciona una fila para editar");
            return;
        }

        if (cliente.getDni() == 0 || cliente.getNombre().isEmpty() || cliente.getTelefono() == 0 || cliente.getDireccion().isEmpty()) {
            vista.mostrarMensaje("Los campos están vacíos");
            return;
        }

        boolean actualizado = clienteDao.actualizarCliente(cliente);

        if (actualizado) {
            boolean actualizadoEnLista = lesClientes.actualizarCliente(cliente);

            if (actualizadoEnLista) {
                vista.mostrarMensaje("Datos del cliente actualizados");
                vista.actualizarTablaDeClientes(lesClientes);
                vista.actualizarImputsEnClientes(null);
            } else {
                vista.mostrarMensaje("Cliente no encontrado en la lista enlazada.");
            }
        } else {
            vista.mostrarMensaje("Hubo un error al actualizar el cliente en la base de datos.");
        }
    }

    public void enEliminarCliente(int id) {
        if (id == 0) {
            vista.mostrarMensaje("Selecciona una fila para eliminar (usa el buscador)");
            return;
        }
        if (clienteDao.buscarClientePorId(id).getId() == 0) {
            vista.mostrarMensaje("El cliente no existe en BD");
            return;
        }
        boolean eliminadoEnBD = clienteDao.eliminarCliente(id);

        if (!eliminadoEnBD) {
            vista.mostrarMensaje("Algo salio mal, no se pudo eliminar en BD");
            return;
        }
        boolean eliminadoEnLes = lesClientes.eliminarCliente(id);

        if (eliminadoEnLes) {
            vista.mostrarMensaje("Cliente eliminado exitosamente");
            vista.actualizarTablaDeClientes(lesClientes);
            vista.actualizarImputsEnClientes(null);
        } else {
            vista.mostrarMensaje("Algo salio mal!");
        }
    }

    public void enAgregarCliente(Cliente cliente) {
        if (cliente.getDni() == 0 || cliente.getNombre().isEmpty() || cliente.getTelefono() == 0 || cliente.getDireccion().isEmpty()) {
            vista.mostrarMensaje("Los campos están vacíos");
            return;
        }
        int id = clienteDao.registrarCliente(cliente);

        boolean registrado = id != -1;
        if (registrado) {
            cliente.setId(id);
            lesClientes.agregar(cliente);
            vista.mostrarMensaje("Cliente registrado exitosamente");
            vista.actualizarTablaDeClientes(lesClientes);
            vista.actualizarImputsEnClientes(null);
        } else {
            vista.mostrarMensaje("Hubo un error al registrar el cliente en la base de datos.");
        }

    }

    public void enBuscarCliente(int dni) {
        if (dni == 0) {
            vista.mostrarMensaje("Ingrese DNI o RUC para buscar");
            return;
        }

        Cliente cliente = lesClientes.buscarPorDni(dni);

        if (cliente != null) {
            vista.actualizarTablaDeClientes(lesClientes);
            vista.actualizarImputsEnClientes(cliente);
        } else {
            vista.mostrarMensaje("Cliente no encontrado");
        }
    }

}
