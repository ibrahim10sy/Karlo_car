package projet.karlo.service;


import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import projet.karlo.model.Role;
import projet.karlo.repository.RoleRepository;
import org.springframework.stereotype.Service;


@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    IdGenerator idGenerator ;


    public Role createRole(Role role){
        Role r = roleRepository.findByLibelle(role.getLibelle());
        if(r != null)
            throw new IllegalStateException("Role existe déjà");
            
        String idcodes = idGenerator.genererCode();
        role.setIdRole(idcodes);

        return roleRepository.save(role);
    }

    public Role updateRole(Role role, String id){
        Role roles = roleRepository.findById(id).orElseThrow(() -> new IllegalStateException("Aucun role trouvé") );

        roles.setLibelle(role.getLibelle());
        roles.setDescription(role.getDescription());

        return roleRepository.save(roles);
    }

    public List<Role> getAllRole() {
        List<Role> role = roleRepository.findAll();

        if (role.isEmpty())
            throw new EntityNotFoundException("Aucune role trouvée");

        role.sort(Comparator.comparing(Role::getLibelle).reversed());
        return role;
    }

    public String deleteRole(String idRole){
        Role role = roleRepository.findById(idRole).orElseThrow(() -> new IllegalStateException("role non trouvé") );

        if(role == null)
            throw new IllegalStateException("Role not found");

            roleRepository.delete(role);
        return "Role supprimé avec succèss";
    }
}


