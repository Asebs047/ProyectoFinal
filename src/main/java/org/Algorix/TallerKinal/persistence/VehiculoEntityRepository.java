package org.Algorix.TallerKinal.persistence;

import org.Algorix.TallerKinal.dominio.dto.ModVehiculoDto;
import org.Algorix.TallerKinal.dominio.dto.VehiculoDto;
import org.Algorix.TallerKinal.dominio.exception.MarcaVehiculoNoExiste;
import org.Algorix.TallerKinal.dominio.exception.VehiculoDuplicadoPlaca;
import org.Algorix.TallerKinal.dominio.exception.VehiculoNoExiste;
import org.Algorix.TallerKinal.dominio.repository.VehiculoRepository;
import org.Algorix.TallerKinal.persistence.crud.CrudVehiculo;
import org.Algorix.TallerKinal.persistence.crud.CrudCliente;
import org.Algorix.TallerKinal.dominio.exception.ClienteNoExiste;
import org.Algorix.TallerKinal.persistence.entity.VehiculoEntity;
import org.Algorix.TallerKinal.web.mapper.VehiculoMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VehiculoEntityRepository implements VehiculoRepository {

    private final CrudVehiculo crudVehiculo;
    private final CrudCliente crudCliente;
    private final VehiculoMapper vehiculoMapper;

    public VehiculoEntityRepository(CrudVehiculo crudVehiculo, CrudCliente crudCliente, VehiculoMapper vehiculoMapper) {
        this.crudVehiculo = crudVehiculo;
        this.crudCliente = crudCliente;
        this.vehiculoMapper = vehiculoMapper;
    }


    @Override
    public List<VehiculoDto> obtenerTodo() {
        return this.vehiculoMapper.toDto(this.crudVehiculo.findAll());
    }

    @Override
    public VehiculoDto buscarPorPlaca(String placa) {
        VehiculoEntity entity = this.crudVehiculo.findFirstByPlacas(placa);
        if (entity == null) {
            throw new VehiculoNoExiste(placa);
        }
        return this.vehiculoMapper.toDto(entity);
    }

    @Override
    public VehiculoDto guardarVehiculo(VehiculoDto vehiculoDto) {
        if (vehiculoDto.marca() == null) {
            throw new MarcaVehiculoNoExiste();
        }
        if (this.crudVehiculo.findFirstByPlacas(vehiculoDto.licensePlate()) != null) {
            throw new VehiculoDuplicadoPlaca(vehiculoDto.licensePlate());
        }
        // Validar existencia de cliente referenciado para evitar violación de FK
        if (vehiculoDto.idCliente() == null || !this.crudCliente.existsById(vehiculoDto.idCliente())) {
            throw new ClienteNoExiste(vehiculoDto.idCliente());
        }
        VehiculoEntity vehiculo = this.vehiculoMapper.toEntity(vehiculoDto);
        this.crudVehiculo.save(vehiculo);
        return this.vehiculoMapper.toDto(vehiculo);
    }



    @Override
    public VehiculoDto modificarVehiculo(String placas, ModVehiculoDto vehiculoDto) {
        VehiculoEntity vehiculo = this.crudVehiculo.findFirstByPlacas(placas);
        if (vehiculo == null) {
            throw new VehiculoNoExiste(placas);
        }
        if (vehiculoDto.marca() == null) {
            throw new MarcaVehiculoNoExiste();
        }
        // Si se proporciona idCliente en la modificación, validar que exista
        if (vehiculoDto.idCliente() != null && !this.crudCliente.existsById(vehiculoDto.idCliente())) {
            throw new ClienteNoExiste(vehiculoDto.idCliente());
        }
        this.vehiculoMapper.modificarEntityFromDto(vehiculoDto, vehiculo);
        return  this.vehiculoMapper.toDto(this.crudVehiculo.save(vehiculo));
    }

    @Override
    public void eliminarVehiculo(Long id) {
        if (!this.crudVehiculo.existsById(id)) {
            throw new VehiculoNoExiste(id);
        }
        this.crudVehiculo.deleteById(id);
    }

    @Override
    public List<VehiculoDto> obtenerPorCliente(Long idCliente) {
        List<VehiculoEntity> all = new ArrayList<>();
        this.crudVehiculo.findAll().forEach(all::add);
        List<VehiculoEntity> filtered = new ArrayList<>();
        for (VehiculoEntity v : all) {
            if (v.getCliente() != null && v.getCliente().getId_cliente() != null && v.getCliente().getId_cliente().equals(idCliente)) {
                filtered.add(v);
            }
        }
        return this.vehiculoMapper.toDto(filtered);
    }
}
