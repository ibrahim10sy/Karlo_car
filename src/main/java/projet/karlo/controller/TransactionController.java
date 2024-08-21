package projet.karlo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import projet.karlo.model.Transaction;
import projet.karlo.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/addTransaction")
    @Operation(summary="Création d'une transaction")
    public ResponseEntity<Transaction> createTypes(@RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.createTransaction(transaction) , HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary="Modification")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.updateTrans(transaction, id), HttpStatus.OK);
    }

    @GetMapping("/getAllTransaction")
    @Operation(summary="Liste de toutes les transaction")
    public ResponseEntity<List<Transaction>> getAll(){
        return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    }

    @GetMapping("/getAllTransactionByType/{type}")
    @Operation(summary="Liste de toutes les transaction par type transaction")
    public ResponseEntity<List<Transaction>> getAllByType(String libelle){
        return new ResponseEntity<>(transactionService.getAllTransactionsByType(libelle), HttpStatus.OK);
    }

    @GetMapping("/getAllTransactionByDate/{date}")
    @Operation(summary="Liste de toutes les transaction par date")
    public ResponseEntity<List<Transaction>> getAllByDate(String date){
        return new ResponseEntity<>(transactionService.getAllTransactionsByDate(date), HttpStatus.OK);
    }

    @GetMapping("/totalDepot")
    public Long getTotalAmountForDepot() {
        return transactionService.getTotalAmountForDepot();
    }

    @GetMapping("/totalRetrait")
    public Long getTotalAmountForRetrait() {
        return transactionService.getTotalAmountForRetrait();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary="Supprimé de Transaction")
    public ResponseEntity<Void> deleteTransactions(@PathVariable("id") String id) {
        transactionService.deleteTransaction(id);
        return  new ResponseEntity<>(HttpStatus.OK); 
    }
}
