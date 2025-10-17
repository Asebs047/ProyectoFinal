package org.Algorix.TallerKinal.persistence.crud;

import org.Algorix.TallerKinal.persistence.entity.ClienteEntity;
import org.springframework.data.repository.CrudRepository;

public interface CrudCliente extends CrudRepository<ClienteEntity,Long> {
    // Búsqueda insensible a mayúsculas/minúsculas para evitar problemas con la entrada del usuario
    ClienteEntity findFirstByCorreoIgnoreCase(String correo);
}
