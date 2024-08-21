package projet.karlo.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projet.karlo.model.Transaction;
import projet.karlo.model.TypeReservoir;
import projet.karlo.model.TypeTransaction;
import projet.karlo.model.User;
import projet.karlo.model.VoitureLouer;
import projet.karlo.repository.TypeTransactionRepository;

@Service
public class TypeTransactionService {

    @Autowired
    TypeTransactionRepository typeRepository;
    @Autowired
    IdGenerator idGenerator ;

    public TypeTransaction createType(TypeTransaction type){
        TypeTransaction r = typeRepository.findByLibelle(type.getLibelle());

        if(r != null)
            throw new IllegalStateException("Ce type existe déjà");

        String idcodes = idGenerator.genererCode();
        type.setIdTypeTransaction(idcodes);

        return typeRepository.save(type);
    }

    public TypeTransaction updateType(TypeTransaction type,String id){
        TypeTransaction t = typeRepository.findById(id).orElseThrow(()-> new IllegalStateException("Not found"));

        t.setLibelle(type.getLibelle());

        return typeRepository.save(t);
    }

    // public List<TypeTransaction> getTypeTransactionByLibelle(String libelle){
    //     List<TypeTransaction> t = typeRepository.findAllByLibelle(libelle);

    //     if(t.isEmpty())
    //     throw new IllegalStateException("Aucune transaction trouvée comme " + libelle);
    //     t = t
    //     .stream().sorted((m1,m2) -> m2.getL().compareTo(m1.getNom()))
    //     .collect(Collectors.toList());        return t;
    // }

    public List<TypeTransaction> getAllTypes() {
        List<TypeTransaction> typeList =  typeRepository.findAll();
        if(typeList.isEmpty())
            throw new IllegalStateException("Aucune transaction trouvée");
            typeList.sort(Comparator.comparing(TypeTransaction::getLibelle));
            return typeList;

    }

    public String deleteType(String id){
        TypeTransaction type = typeRepository.findById(id).orElseThrow();
        typeRepository.delete(type);
        return "Supprimé avec succès";
    }
}
