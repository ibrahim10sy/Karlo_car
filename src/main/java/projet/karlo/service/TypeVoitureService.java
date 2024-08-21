package projet.karlo.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projet.karlo.model.TypeTransaction;
import projet.karlo.model.TypeVoiture;
import projet.karlo.repository.TypeVoitureRepository;

@Service
public class TypeVoitureService {

    @Autowired
    IdGenerator idGenerator ;
    @Autowired
    TypeVoitureRepository typeVoitureRepository;

    public TypeVoiture createTypeVoiture(TypeVoiture typeVoiture){
        TypeVoiture r = typeVoitureRepository.findByNomTypeVoiture(typeVoiture.getNomTypeVoiture());

        if(r != null)
            throw new IllegalStateException("Ce type existe déjà");

        String idcodes = idGenerator.genererCode();
        typeVoiture.setIdTypeVoiture(idcodes);

        return typeVoitureRepository.save(typeVoiture);
    }


    public TypeVoiture updateType(TypeVoiture typeVoiture, String id){
        TypeVoiture t = typeVoitureRepository.findById(id).orElseThrow(()-> new IllegalStateException("Not found"));

        t.setNomTypeVoiture(typeVoiture.getNomTypeVoiture());
        t.setDescription(typeVoiture.getDescription());

        return typeVoitureRepository.save(t);
    }

    public List<TypeVoiture> getVoiture(){
        List<TypeVoiture> t = typeVoitureRepository.findAll();
        if(t.isEmpty())
            throw new IllegalStateException("Aucun type trouvée");
     t.sort(Comparator.comparing(TypeVoiture::getNomTypeVoiture));
        return t;
    }

    public String deleteType(String id){
        TypeVoiture t = typeVoitureRepository.findById(id).orElseThrow(()-> new IllegalStateException("Not found"));

        typeVoitureRepository.delete(t);
        return "Supprimé avec succès";
    }
}
