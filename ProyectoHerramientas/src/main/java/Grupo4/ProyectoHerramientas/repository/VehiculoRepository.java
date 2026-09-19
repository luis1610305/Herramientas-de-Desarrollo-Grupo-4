package Grupo4.ProyectoHerramientas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Grupo4.ProyectoHerramientas.model.Vehiculo;



public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByEstado(String estado);

    // Busca vehículos que coincidan con la placa (parcial) Y el estado exacto
    List<Vehiculo> findByPlacaContainingIgnoreCaseAndEstado(String placa, String estado);

    // Busca solo por placa (por si el admin elige "Todos" los estados)
    List<Vehiculo> findByPlacaContainingIgnoreCase(String placa);

    // Filtro para el Cliente (Busca por marca y obliga a que esté Disponible)
    List<Vehiculo> findByMarcaContainingIgnoreCaseAndEstado(String marca, String estado);
}
